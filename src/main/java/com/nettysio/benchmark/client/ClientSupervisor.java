package com.nettysio.benchmark.client;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.japi.Creator;
import com.google.inject.Inject;
import com.nettysio.benchmark.client.actor.BenchmarkActor;
import com.nettysio.benchmark.client.actor.BenchmarkSupervisor;
import com.nettysio.benchmark.client.message.Concurrency;
import com.nettysio.benchmark.client.message.Interval;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Yulin Chen
 * @since 11/28/14
 */
public class ClientSupervisor {

    @Inject
    public ClientSupervisor() {

    }

    public void start(int concurrency, int interval, int cutoff) throws InterruptedException {
        final ActorSystem system = ActorSystem.create("NettySioBenchmarkClientSystem");
        final ActorRef supervisor = system.actorOf(Props.create(new BenchmarkSupervisorCreator(cutoff)));
        supervisor.tell(new Concurrency(concurrency), supervisor);
        supervisor.tell(new Interval(interval, TimeUnit.MILLISECONDS), supervisor);
    }

    private static class BenchmarkSupervisorCreator implements Creator<BenchmarkSupervisor> {

        private final int cutoff;

        public BenchmarkSupervisorCreator(int cutoff) {
            this.cutoff = cutoff;
        }

        @Override
        public BenchmarkSupervisor create() {
            return new BenchmarkSupervisor(cutoff);
        }
    }

}
