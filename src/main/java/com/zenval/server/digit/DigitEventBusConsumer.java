package com.zenval.server.digit;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

import com.zenval.server.helper.TerminateSignal;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DigitEventBusConsumer {
    private ScheduledExecutorService executorService;
    private DigitWriterAggregator digitWriterAggregator;

    public DigitEventBusConsumer() {
        executorService = Executors.newScheduledThreadPool(1);
        digitWriterAggregator = new DigitWriterAggregator();
        executorService.scheduleWithFixedDelay(digitWriterAggregator, 0, 1, TimeUnit.MILLISECONDS);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void receiveMessage(String message) {
        digitWriterAggregator.offer(message);
    }

    @Subscribe
    public void close(TerminateSignal terminateSignal) {
        executorService.shutdown();
        digitWriterAggregator.stop();
    }

}
