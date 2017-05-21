package com.zenval.server.file;

import com.google.common.io.Files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by arturo on 20/05/17.
 */
public class DigitFileWriter {
    public final static String FILE_NAME = "digits.log";
    final File file;
    final FileWriter filewriter;
    final BufferedWriter bufferedwriter;

    public DigitFileWriter() throws IOException {
        file = new File(FILE_NAME);
        clearFile();
        filewriter = new FileWriter(file, true);
        bufferedwriter = new BufferedWriter(filewriter);
    }

    private void clearFile() throws IOException {
        Files.write(new byte[]{}, file);
    }

    public void write(final String content) throws IOException {
        bufferedwriter.append(content);
        bufferedwriter.append(System.lineSeparator());
        bufferedwriter.flush();
        filewriter.flush();

    }

    public void close() throws IOException {
        bufferedwriter.close();
        filewriter.close();
    }
}
