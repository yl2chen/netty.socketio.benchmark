package com.nettysio.benchmark.client.actor;

import akka.actor.*;
import akka.japi.Creator;
import com.nettysio.benchmark.client.message.Benchmark;
import com.nettysio.benchmark.client.message.Concurrency;
import com.nettysio.benchmark.client.message.ConnectionFailure;
import com.nettysio.benchmark.client.message.Interval;
import lombok.extern.slf4j.Slf4j;
import scala.concurrent.duration.Duration;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author Yulin Chen
 * @since 11/28/14
 */
@Slf4j
public class BenchmarkSupervisor extends UntypedActor {

    private final int tripCutoff;
    private int numberOfChildren;
    private Interval currentInterval;
    private int total = 0;
    private int count = 0;
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    private Cancellable tick = getContext().system().scheduler().schedule(
            Duration.create(1, TimeUnit.SECONDS),
            Duration.create(1, TimeUnit.SECONDS),
            getSelf(), "tick", getContext().dispatcher(), null);

    public BenchmarkSupervisor(int tripCutoff) {
        this.tripCutoff = tripCutoff;
    }

    @Override
    public void preStart() {
        numberOfChildren = 0;
        currentInterval = Interval.defaultInterval();
    }

    @Override
    public void postStop() {
        for (ActorRef child : getContext().getChildren()) {
           child.tell(PoisonPill.getInstance(), self());
        }
        tick.cancel();
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof Benchmark) {
            addBenchmark((Benchmark) o);
        } else if ("tick".equals(o)) {
            showBenchmark();
        } else if (o instanceof Concurrency) {
            updateConcurrency((Concurrency) o);
        } else if (o instanceof Interval) {
            upldateInterval((Interval) o);
        } else if (o instanceof ConnectionFailure) {
            // TODO error handling
            log.error("Connection failed");
        } else {
            unhandled(o);
        }
    }

    private void showBenchmark() {
        if (count == 0) return;
        w.lock();
        int tripTime = total/count;
        log.info("return trip {}ms", tripTime);
        if (tripTime <= tripCutoff) {
            self().tell(new Concurrency(numberOfChildren + 1), self());
        }
        total = 0;
        count = 0;
        w.unlock();
    }

    private void addBenchmark(Benchmark b) {
        r.lock();
        total += b.getDiff();
        count++;
        r.unlock();
    }

    private void updateConcurrency(Concurrency concurrency) {
        log.info("{} concurrent connections", concurrency.getNumber());
        if (numberOfChildren > concurrency.getNumber()) {
            int numberToKill = numberOfChildren - concurrency.getNumber();
            for (ActorRef child : getContext().getChildren()) {
                child.tell(PoisonPill.getInstance(), self());
                if (--numberToKill == 0) {
                    break;
                }
            }
        } else {
            for (int i = 0; i <  concurrency.getNumber() - numberOfChildren; i++) {
                ActorRef actor = getContext().actorOf(Props.create(BenchmarkActorCreator.getInstance()));
                actor.tell(currentInterval, self());
            }
        }
        numberOfChildren = concurrency.getNumber();
    }

    private void upldateInterval(Interval interval) {
        currentInterval = interval;
        for (ActorRef child : getContext().getChildren()) {
            child.tell(currentInterval, self());
        }
    }

    static class BenchmarkActorCreator implements Creator<BenchmarkActor> {
        private static BenchmarkActorCreator instance = new BenchmarkActorCreator();
        private static int count = -1;
        private static String room = UUID.randomUUID().toString();

        public static BenchmarkActorCreator getInstance() {
            return instance;
        }

        private synchronized BenchmarkActor getActor() {
            if (++count > 1) {
                count = 0;
                room = UUID.randomUUID().toString();
            }
            return new BenchmarkActor(room);
        }

        @Override
        public BenchmarkActor create() {
            return getActor();
        }
    }
}
