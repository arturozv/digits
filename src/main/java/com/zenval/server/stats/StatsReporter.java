package com.zenval.server.stats;

import com.zenval.server.digit.DigitUniqueControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by arturo on 20/05/17.
 */
public class StatsReporter {
    private static final Logger logger = LoggerFactory.getLogger(StatsReporter.class);

    private final static String MESSAGE = "Received %s unique numbers, %s duplicates. Unique total: %s";

    private Timer timer;
    private DigitUniqueControl digitUniqueControl;
    private long reportPeriod;

    private long unique = 0;
    private long duplicates = 0;

    public StatsReporter(DigitUniqueControl digitUniqueControl, long reportPeriod) {
        this.digitUniqueControl = digitUniqueControl;
        this.reportPeriod = reportPeriod;
    }

    String reportStats() {
        int duplicatedSize = digitUniqueControl.getDuplicatedSize();
        int processedSize = digitUniqueControl.getProcessedSize();

        String result = String.format(MESSAGE,
                                      NumberFormatter.format(processedSize - unique),
                                      NumberFormatter.format(duplicatedSize - duplicates),
                                      NumberFormatter.format(processedSize));

        unique = processedSize;
        duplicates = duplicatedSize;

        return result;
    }

    public void start() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                logger.info(reportStats());
            }
        }, 0, reportPeriod);
    }

    public void stop() {
        timer.cancel();
        reportStats();
    }
}
