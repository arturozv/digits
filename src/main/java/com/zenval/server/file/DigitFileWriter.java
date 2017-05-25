package com.zenval.server.file;

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
    final Object sync = new Object();

    private BlockingQueue<String> queue = new ArrayBlockingQueue<>(100_000_000);

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

        executorService.scheduleAtFixedRate(() -> {
            synchronized (sync) {
                if (!queue.isEmpty()) {
                    Set<String> toProcess = new HashSet<>();
                    int toDrain = queue.size();
                    queue.drainTo(toProcess, toDrain);

                    if (toProcess.size() > 0) {
                        logger.info("writing {} digits", toProcess.size());
                        write(String.join(System.lineSeparator(), toProcess));
                    }
                }
            }
        }, 0, 1, TimeUnit.MILLISECONDS);
    }

    public synchronized void writeAsync(final String content) {
        queue.offer(content);
    }

    public void write(final String content) {
        try {
            fileChannel.write(ByteBuffer.wrap(content.getBytes()));
        } catch (IOException e) {
            logger.error("Error writing file", e);
        }
    }
}
