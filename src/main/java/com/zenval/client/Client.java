package com.zenval.client;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by arturo on 20/05/17.
 */
public class Client implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    private final String host;
    private final int port;
    private ExecutorService executorService;
    private final int maxConcurrentConnections;
    private AtomicInteger currentConnections;

    public Client(String host, int port, int maxConcurrentConnections) {
        this.executorService = Executors.newFixedThreadPool(maxConcurrentConnections);
        this.maxConcurrentConnections = maxConcurrentConnections;
        this.currentConnections = new AtomicInteger(0);
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        while (currentConnections.get() < maxConcurrentConnections) {
            executorService.submit(() -> {
                try (
                        Socket socket = new Socket(host, port);
                        PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
                ) {

                    currentConnections.incrementAndGet();

                    while (!out.checkError()) {
                        long t = System.currentTimeMillis();
                        String digit = RandomStringUtils.randomNumeric(9);
                        out.println(digit);


                        long time = (System.currentTimeMillis() - t);
                        if (time > 100) {
                            logger.debug("digit {} took {}ms to be send", digit, time);
                        }
                    }

                } catch (IOException e) {
                    logger.error("Error sending digit: " + e.getMessage());
                } finally {
                    currentConnections.getAndDecrement();
                }
            });
        }
    }
}
