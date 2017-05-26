package com.zenval.server.digit;

import com.zenval.server.helper.NumberFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by arturo on 20/05/17.
 */
public class DigitUniqueControl {
    private static final Logger logger = LoggerFactory.getLogger(DigitUniqueControl.class);
    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private volatile long unique = 0;
    private volatile long duplicates = 0;

    private Set<Integer> processed;

    public DigitUniqueControl() {
        processed = ConcurrentHashMap.newKeySet();

        executorService.scheduleAtFixedRate(() -> {

            int processedSize = processed.size();

            logger.info("Received {} unique numbers, {} duplicates. Unique total: {}",
                        NumberFormatter.format(processedSize - unique),
                        NumberFormatter.format(duplicates),
                        NumberFormatter.format(processedSize));

            unique = processedSize;
            duplicates = 0l;

        }, 1, 1, TimeUnit.SECONDS);
    }

    public synchronized boolean isUnique(String candidate) {
        Integer asInt = Integer.parseInt(candidate);
        if (processed.contains(asInt)) {
            duplicates++;
            return false;
        }
        processed.add(asInt);
        return true;
    }
}
