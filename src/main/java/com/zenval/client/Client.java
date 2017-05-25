package com.zenval.client;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by arturo on 20/05/17.
 */
public class Client implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private String host;
    private int port;

    private ClientEventCallback clientEventCallback;

    public Client(String host, int port, ClientEventCallback clientEventCallback) {
        this.clientEventCallback = clientEventCallback;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {

    }
}
