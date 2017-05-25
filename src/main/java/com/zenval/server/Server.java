package com.zenval.server;

import com.zenval.server.digit.DigitProcessor;
import com.zenval.server.digit.DigitUniqueControl;
import com.zenval.server.digit.DigitWriterAggregator;
import com.zenval.server.stats.StatsReporter;

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
        DigitProcessor digitProcessor = new DigitProcessor(new DigitWriterAggregator(), digitUniqueControl);
        StatsReporter statsReporter = new StatsReporter(digitUniqueControl);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (!executorService.isShutdown() && runningTasks.get() < maxConnections) {

                Socket socket = serverSocket.accept();
                runningTasks.incrementAndGet();

                executorService.submit(() -> {
                    logger.info("Server started {}", socket);
                    try (
                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
                    ) {
                        String inputLine;
                        boolean shouldStop = false;

                        while (!executorService.isShutdown() && !shouldStop && (inputLine = in.readLine()) != null) {
                            DigitProcessor.DIGIT_RESULT result = digitProcessor.process(inputLine);

                            switch (result) {
                                case TERMINATE:
                                    executorService.shutdown();
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

        } catch (IOException e) {
            logger.error("Error creating the Server", e);
        } finally {
            statsReporter.stop();
        }
    }
}
