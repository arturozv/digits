package com.zenval.server.digit;

import com.google.common.collect.Sets;

import com.zenval.server.helper.NumberFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;
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

    private Set<String> processed;
    private Set<String> duplicated;

    public DigitUniqueControl() {
        processed = Sets.newConcurrentHashSet();
        duplicated = Sets.newConcurrentHashSet();

        executorService.scheduleAtFixedRate(() -> {
            int duplicatedSize = duplicated.size();
            int processedSize = processed.size();

            logger.info(String.format("Received %s unique numbers, %s duplicates. Unique total: %s",
                                      NumberFormatter.format(processedSize - unique),
                                      NumberFormatter.format(duplicatedSize - duplicates),
                                      NumberFormatter.format(processedSize)));

            unique = processedSize;
            duplicates = duplicatedSize;
        }, 0, 1, TimeUnit.SECONDS);
    }

    public synchronized boolean isUnique(String candidate) {
        if (processed.contains(candidate)) {
            duplicated.add(candidate);
            return false;
        }
        processed.add(candidate);
        return true;
    }
}
