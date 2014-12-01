package com.nettysio.benchmark.sio.module;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import com.nettysio.benchmark.sio.handler.impl.ConnectionListener;
import com.nettysio.benchmark.sio.handler.Listener;

/**
 * @author Yulin Chen
 * @since 11/15/14
 */
public class ListenerModule extends AbstractModule {

    @Override
    protected void configure() {
        Multibinder<Listener> listenerBinder = Multibinder.newSetBinder(binder(), Listener.class);
        listenerBinder.addBinding().to(ConnectionListener.class);
    }
}
