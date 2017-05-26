package com.zenval.server.digit;

import com.google.common.io.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by arturo on 20/05/17.
 */
public class DigitFileWriter {
    private static final Logger logger = LoggerFactory.getLogger(DigitFileWriter.class);
    private FileChannel fileChannel;
    final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    private BlockingQueue<String> queue = new ArrayBlockingQueue<>(10_000_000);

    public DigitFileWriter() {
        try {
            File file = new File("digits.log");
            Files.write(new byte[]{}, file); //clear file
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileChannel = fileOutputStream.getChannel();
        } catch (IOException e) {
            logger.error("Error creating opening the file", e);
            System.exit(1);
        }

        //every X seconds drain the queue and write the file
        executorService.scheduleAtFixedRate(() -> {
            int toDrain = queue.size();
            if (toDrain > 0) {
                Set<String> toProcess = new HashSet<>();
                queue.drainTo(toProcess, toDrain);

                if (toProcess.size() > 0) {
                    logger.debug("writing {} digits", toProcess.size());
                    write(String.join(System.lineSeparator(), toProcess));
                }
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    public void writeAsync(final String content) {
        queue.offer(content);
    }

    public void write(final String content) {
        try {
            fileChannel.write(ByteBuffer.wrap(content.getBytes()));
        } catch (IOException e) {
            logger.error("Error writing file", e);
            System.exit(1);
        }
    }
}
