package com.nettysio.benchmark.sio.handler;

import com.corundumstudio.socketio.SocketIOServer;

/**
 * @author  Yulin Chen
 * @since   11/15/14
 */
public interface Listener {
    public void setSio(SocketIOServer sio);
}
