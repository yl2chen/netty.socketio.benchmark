package com.nettysio.benchmark.sio;

import com.corundumstudio.socketio.SocketIOServer;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.inject.Inject;

/**
 * @author Yulin Chen
 * @since  2014-11-11
 */
public class NettyServer extends AbstractIdleService {

    private final SocketIOServer sio;

    @Inject
    public NettyServer(SocketIOServer sio) {
        this.sio = sio;
    }

    @Override
    protected void startUp() throws Exception {
        sio.start();
    }

    @Override
    protected void shutDown() throws Exception {
        sio.stop();
    }
}
