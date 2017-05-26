package com.zenval.server.digit;

import com.carrotsearch.hppc.IntHashSet;
import com.zenval.server.helper.NumberFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * - Controls that no duplicate digits are processed.
 * - Reports the process stats
 */
public class DigitUniqueControl {
    private static final Logger logger = LoggerFactory.getLogger(DigitUniqueControl.class);
    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private volatile long duplicates = 0;
    private long unique = 0;
    private long iterations = 1;

    private AtomicLong processedSize = new AtomicLong(0);

    //primitive set to optimize memory size
    private IntHashSet processed;

    public DigitUniqueControl() {
        processed = new IntHashSet();

        //report stats every X seconds
        executorService.scheduleAtFixedRate(() -> {

            long processedSize = this.processedSize.get();

            logger.info("Received {} unique numbers, {} duplicates. Avg: {}. Unique total: {}",
                        NumberFormatter.format(processedSize - unique),
                        NumberFormatter.format(duplicates),
                        NumberFormatter.format(processedSize / iterations++),
                        NumberFormatter.format(processedSize));

            unique = processedSize;
            duplicates = 0l;

        }, 1, 10, TimeUnit.SECONDS);
    }

    /**
     * Check if a digit is unique
     * @param candidate digit to be checked
     * @return true if is unique, false if it has been previously processed.
     */
    public synchronized boolean isUnique(String candidate) {
        int asInt = Integer.parseInt(candidate);
        if (processed.contains(asInt)) {
            duplicates++;
            return false;
        }
        processed.add(asInt);
        processedSize.incrementAndGet();
        return true;
    }

    /**
     * Stops the reporter
     */
    public void stop() {
        logger.info("Reporter stopped");
        executorService.shutdown();
    }
}
