package com.nettysio.benchmark.client.actor;

import akka.actor.Cancellable;
import akka.actor.UntypedActor;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Ack;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.common.primitives.Ints;
import com.nettysio.benchmark.client.message.Benchmark;
import com.nettysio.benchmark.client.message.ConnectionFailure;
import com.nettysio.benchmark.client.message.Interval;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.duration.Duration;

import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author Yulin Chen
 * @since 11/28/14
 */
@Slf4j
public class BenchmarkActor extends UntypedActor {

    private Cancellable tick;
    private Socket socket;
    private String room;
    private boolean roomConfigured = false;

    public BenchmarkActor(String room) {
        this.room = room;
    }

    @Override
    public void postStop() {
        tick.cancel();
    }

    @Override
    public void preStart() throws URISyntaxException {
        setupSocket();
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o.equals("tick")) {
            sendTick();
        } else if (o instanceof Interval) {
            setTick((Interval) o);
        } else {
            unhandled(o);
        }
    }

    private void setTick(Interval interval) {
        if (socket != null) {
            if (tick != null) {
                tick.cancel();
            }
            tick = getContext().system().scheduler().schedule(
                    Duration.create(new Random(System.currentTimeMillis()).nextInt(1000),
                            TimeUnit.MILLISECONDS),
                    Duration.create(interval.getNumber(), interval.getTimeunit()),
                    getSelf(), "tick", getContext().dispatcher(), null);
        }
    }

    private void sendTick() {
        if (roomConfigured) {
            socket.emit("echo", System.currentTimeMillis());
        }
    }

    private void setupSocket() throws URISyntaxException {
        socket = null;
        IO.Options op = new IO.Options();
        op.forceNew = true;
        socket = IO.socket("http://localhost:3000", op);
        socket.on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                getContext().parent().tell(ConnectionFailure.getInstance(), self());
            }
        });
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                socket.emit("room", room, new Ack() {
                    @Override
                    public void call(Object... objects) {
                        roomConfigured = true;
                    }
                });
            }
        });
        socket.on("echoback", new Emitter.Listener() {
            @Override
            public void call(Object... objects) {
                Long sentTime = (Long) objects[0];
                if (sentTime != null) {
                    int returnTrip = Ints.checkedCast(System.currentTimeMillis() - sentTime);
                    getContext().parent().tell(new Benchmark(returnTrip), self());
                }
            }
        });
        socket.connect();
    }
}
