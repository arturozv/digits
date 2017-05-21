package com.zenval;

import com.zenval.client.ClientRunner;
import com.zenval.server.ServerManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by arturo on 20/05/17.
 */
public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public final static String TERMINATE_COMMAND = "terminate";
    public final static int CONCURRENT_CONNECTIONS = 5;

    public final static String HOST = "127.0.0.1";
    public final static int PORT = 4000;

    public static void main(String[] args) throws IOException {
        logger.info("Starting application...");

        new Thread(new ServerManager(PORT, CONCURRENT_CONNECTIONS)).start();
        new Thread(new ClientRunner(HOST, PORT, CONCURRENT_CONNECTIONS)).start();

        logger.info("Application started!");
    }
}
