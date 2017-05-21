package com.zenval.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by arturo on 20/05/17.
 */
public class ClientRunner implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientRunner.class);

    private final String host;
    private final int port;
    private ExecutorService executorService;
    private final int maxConcurrentConnections;
    private AtomicInteger currentConnections;
    private AtomicBoolean terminateSignalSent = new AtomicBoolean(false);

    public ClientRunner(String host, int port, int maxConcurrentConnections) {
        this.executorService = Executors.newFixedThreadPool(maxConcurrentConnections);
        this.maxConcurrentConnections = maxConcurrentConnections;
        this.currentConnections = new AtomicInteger(0);
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        ClientEventCallback clientEventCallback = clientEventCallback();
        while (!terminateSignalSent.get()) {
            if (currentConnections.get() < maxConcurrentConnections) {
                executorService.submit(new Client(host, port, clientEventCallback));
            }
        }
    }

    private ClientEventCallback clientEventCallback() {
        return new ClientEventCallback() {
            @Override
            public void onConnectionSuccess() {
                int current = currentConnections.incrementAndGet();
                logger.debug("Client connected!", current);
            }

            @Override
            public void onDisconnect() {
                int current = currentConnections.getAndDecrement();
                logger.debug("Client disconnected!", current);
            }

            @Override
            public void onTerminate() {
                terminateSignalSent.set(true);
            }
        };
    }
}
