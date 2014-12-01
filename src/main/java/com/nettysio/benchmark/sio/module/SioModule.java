package com.nettysio.benchmark.sio.module;

import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.nettysio.benchmark.sio.handler.Listener;

import java.util.Set;

/**
 * @author Yulin Chen
 * @since 11/12/14
 */
public class SioModule extends AbstractModule {

    @Override
    protected void configure() {

    }

    @Provides
    @Singleton
    SocketIOServer provideSio(Configuration config, Set<Listener> listeners) {
        SocketIOServer sio = new SocketIOServer(config);
        for(Listener listener : listeners) {
            listener.setSio(sio);
            sio.addListeners(listener);
        }
        return sio;
    }
}
