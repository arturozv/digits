package com.zenval.server;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import com.zenval.server.digit.DigitProcessor;
import com.zenval.server.digit.DigitSocketReader;
import com.zenval.server.digit.DigitUniqueControl;
import com.zenval.server.digit.DigitWriterAggregator;
import com.zenval.server.helper.DisconnectCallback;
import com.zenval.server.helper.TerminateSignal;
import com.zenval.server.stats.StatsReporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by arturo on 20/05/17.
 */
public class ServerManager implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ServerManager.class);
    private final int maxConnections;

    private Server server;

    private ExecutorService executorService;
    private StatsReporter statsReporter;
    private DigitProcessor digitProcessor;

    private Set<DigitSocketReader> tasks = new HashSet<>();
    private AtomicBoolean stopSignalReceived = new AtomicBoolean(false);
    private DisconnectCallback disconnectCallback;

    public ServerManager(int port, int maxConnections) throws IOException {
        this.server = new Server(port);
        this.maxConnections = maxConnections;

        setup();
    }

    @Override
    public void run() {

        while (!stopSignalReceived.get()) {
            if (tasks.size() < maxConnections) {

                Socket connection = server.getConnection();

                try {
                    DigitSocketReader digitSocketReader = new DigitSocketReader(connection, disconnectCallback, digitProcessor);
                    tasks.add(digitSocketReader);
                    executorService.submit(digitSocketReader);
                    logger.debug("{} started", digitSocketReader);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        shutdown();
    }

    void setup() {
        this.executorService = Executors.newFixedThreadPool(maxConnections);

        this.disconnectCallback = this::removeSocketHandler;

        DigitUniqueControl digitUniqueControl = new DigitUniqueControl();
        this.digitProcessor = new DigitProcessor(new DigitWriterAggregator(), digitUniqueControl);

        this.statsReporter = new StatsReporter(digitUniqueControl, TimeUnit.SECONDS.toMillis(10));
        this.statsReporter.start();
    }

    @Subscribe
    public void terminate(TerminateSignal terminateSignal) {
        stopSignalReceived.set(true);
    }

    void shutdown() {
        logger.debug("Shutting down the server!! tasks: {}", tasks);

        synchronized (this) {
            for (DigitSocketReader digitSocketReader : tasks) {
                logger.debug("TERMINATE - telling {} to stop. Remaining tasks: {}", digitSocketReader, tasks);
                digitSocketReader.setShouldStop(true);
            }
        }

        long timeout = System.currentTimeMillis() + 3000;
        while (!tasks.isEmpty() && System.currentTimeMillis() < timeout) {
            try {
                logger.debug("TERMINATE - Waiting for workers to finish...");
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        logger.info("All workers have been terminated");
        statsReporter.stop();
        server.stop();
    }

    void removeSocketHandler(DigitSocketReader digitSocketReader) {
        synchronized (this) {
            tasks.remove(digitSocketReader);
        }
    }
}
