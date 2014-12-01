package com.nettysio.benchmark.sio.handler.impl;

import com.corundumstudio.socketio.SocketIOServer;
import com.nettysio.benchmark.sio.handler.Listener;
import lombok.Getter;

/**
 * @author Yulin Chen
 * @since 11/15/14
 */
public abstract class BaseListener implements Listener {

    @Getter
    private SocketIOServer sio;

    @Override
    public void setSio(SocketIOServer sio) {
        this.sio = sio;
    }
}
