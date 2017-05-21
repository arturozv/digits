package com.zenval.server.digit;

import com.zenval.server.file.DigitFileWriter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by arturo on 20/05/17.
 * Aggregates the digits on a queue and send them in batches to the file writer
 */
public class DigitWriterAggregator implements Runnable {

    private BlockingQueue<String> queue = new ArrayBlockingQueue<>(100000);
    private DigitFileWriter digitFileWriter;

    public DigitWriterAggregator() {
        try {
            digitFileWriter = new DigitFileWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        if (!queue.isEmpty()) {
            Set<String> toProcess = new HashSet<>();
            queue.drainTo(toProcess, 100000);

            if (toProcess.size() > 0) {
                try {
                    digitFileWriter.write(String.join(System.lineSeparator(), toProcess));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void offer(String message) {
        queue.offer(message);
    }

    public void stop() {
        try {
            digitFileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
