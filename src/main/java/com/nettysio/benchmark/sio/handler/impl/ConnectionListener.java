package com.nettysio.benchmark.sio.handler.impl;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.ClientOperations;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author  Yulin Chen
 * @since   11/12/14
 */
@Slf4j
@SuppressWarnings("unused")
public class ConnectionListener extends BaseListener {

    private Map<UUID, String> map = new ConcurrentHashMap<UUID, String>();

    @Inject
    public ConnectionListener() {

    }

    @OnConnect
    public void onConnectHandler(SocketIOClient client) throws InterruptedException {
        log.info("connect to client: {}", client.getRemoteAddress());
    }

    @OnDisconnect
    public void onDisconnectHandler(SocketIOClient client) {
        log.info("disconnect from client {}", client.getRemoteAddress());
    }

    @OnEvent("room")
    public void onBeatHandler(SocketIOClient client, String room) {
        map.put(client.getSessionId(), room);
        client.joinRoom(room);
    }

    @OnEvent("echo")
    public void echo(SocketIOClient client, Long message, AckRequest ackRequest) {
        getRoom(client).sendEvent("echoback", message);
    }

    private ClientOperations getRoom(SocketIOClient client) {
        String room = map.get(client.getSessionId());
        if (Strings.isNullOrEmpty(room)) {
            return client;
        }
        return getSio().getRoomOperations(room);
    }
}
