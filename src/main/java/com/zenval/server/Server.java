package com.zenval.server;

import com.zenval.server.digit.DigitFileWriter;
import com.zenval.server.digit.DigitProcessor;
import com.zenval.server.digit.DigitUniqueControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by arturo on 20/05/17.
 */
public class Server implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    private final int maxConnections;
    private final int port;
    private AtomicInteger runningTasks = new AtomicInteger(0);

    public Server(int port, int maxConnections) throws IOException {
        this.port = port;
        this.maxConnections = maxConnections;
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newFixedThreadPool(maxConnections);
        DigitUniqueControl digitUniqueControl = new DigitUniqueControl();

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            DigitProcessor digitProcessor = new DigitProcessor(digitUniqueControl);
            DigitFileWriter digitFileWriter = new DigitFileWriter();

            //run until one worker receives a termination signal
            while (!executorService.isShutdown()) {

                //spawn workers until desired capacity
                while (runningTasks.get() < maxConnections) {

                    logger.info("Attempting to get socket {}", runningTasks.get());
                    Socket socket = serverSocket.accept();

                    runningTasks.incrementAndGet();


                    executorService.submit(() -> {
                        logger.info("Connection started {}", socket);
                        try (
                                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                        ) {
                            String digit;
                            boolean shouldStop = false;

                            //read from the socket until a termination signal or an invalid digit is received
                            while (!executorService.isShutdown() && !shouldStop && (digit = in.readLine()) != null) {

                                //process the digit
                                switch (digitProcessor.process(digit)) {
                                    case OK:
                                        digitFileWriter.writeAsync(digit);
                                        break;

                                    case TERMINATE:
                                        logger.warn("TERMINATION SIGNAL RECEIVED");
                                        executorService.shutdown();
                                        digitUniqueControl.stop();
                                        digitFileWriter.stop();
                                        break;

                                    case WRONG_FORMAT:
                                        shouldStop = true;
                                        break;

                                    default:
                                        break;
                                }
                            }

                            runningTasks.decrementAndGet();

                        } catch (IOException e) {
                            logger.error("Error reading from socket", e);
                        }
                    });
                }
            }

        } catch (IOException e) {
            logger.error("Error creating the Server", e);
            System.exit(1);
        }

        if (executorService.isShutdown()) {
            logger.info("Server stopped, exiting program!");
            System.exit(0);
        }
    }
}
