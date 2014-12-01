package com.nettysio.benchmark;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.nettysio.benchmark.client.ClientSupervisor;
import com.nettysio.benchmark.sio.NettyServer;
import com.nettysio.benchmark.sio.module.ServerModule;
import lombok.extern.slf4j.Slf4j;

/**
 * Server entry point
 *
 * @author Yulin Chen
 * @since   2014-11-11
 */
@Slf4j
public class Server
{
    public static void main( String[] args ) throws Exception {
        if ("client".equals(args[0])) {
            int concurrency = Integer.parseInt(args[1]);
            int interval = Integer.parseInt(args[2]);
            int cutoff = Integer.parseInt(args[3]);
            runBenchmarkClient(concurrency, interval, cutoff);
        } else {
            runBenchmarkerServer();
        }
    }

    private static void runBenchmarkerServer() {
        log.info("benchmark server");
        Injector injector = Guice.createInjector(new ServerModule());
        final NettyServer netty = injector.getInstance(NettyServer.class);

        netty.startAsync();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                netty.stopAsync();
                log.info("gracefully shut down");
            }
        });
    }

    private static void runBenchmarkClient(int concurrency, int interval, int cutoff) throws InterruptedException {
        log.info("benchmark client");
        Injector injector = Guice.createInjector();
        injector.getInstance(ClientSupervisor.class).start(concurrency, interval, cutoff);
    }
}
