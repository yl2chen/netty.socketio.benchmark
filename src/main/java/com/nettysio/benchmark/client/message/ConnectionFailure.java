package com.nettysio.benchmark.client.message;

/**
 * @author Yulin Chen
 * @since 11/28/14
 */
public class ConnectionFailure {

    private static ConnectionFailure instance = new ConnectionFailure();

    public static ConnectionFailure getInstance() {
        return instance;
    }
}
