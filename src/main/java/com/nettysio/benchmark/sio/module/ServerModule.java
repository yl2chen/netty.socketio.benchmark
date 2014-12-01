package com.nettysio.benchmark.sio.module;

import com.google.inject.AbstractModule;
import com.nettysio.benchmark.sio.NettyServer;

/**
 * Created by yulin on 11/11/14.
 */
public class ServerModule extends AbstractModule {

    @Override
    protected void configure() {
        installModules();
        setBindings();
    }

    private void installModules() {
        install(new SioModule());
        install(new ConfigModule());
        install(new ListenerModule());
    }

    private void setBindings() {
        bind(NettyServer.class).asEagerSingleton();
    }
}
