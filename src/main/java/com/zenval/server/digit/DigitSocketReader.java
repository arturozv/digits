package com.zenval.server.digit;

import com.zenval.server.helper.DisconnectCallback;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by arturo on 20/05/17.
 */
public class DigitSocketReader implements Runnable {

    private DisconnectCallback serverEventHandler;
    private DigitProcessor digitProcessor;

    private Socket socket;
    private BufferedReader in;

    private String id;
    private boolean shouldStop;

    public DigitSocketReader(Socket socket, DisconnectCallback serverEventHandler, DigitProcessor digitProcessor) throws IOException {
        this.socket = socket;
        this.digitProcessor = digitProcessor;
        this.serverEventHandler = serverEventHandler;
        this.id = RandomStringUtils.randomAlphabetic(5);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    @Override
    public void run() {
        while (!shouldStop) {
            String input = readSocket();
            if (input != null) {
                if (!digitProcessor.processAndKeepRunning(input)) {
                    close();
                }
            }
        }
        close();
    }

    private String readSocket() {
        String input = null;
        try {
            if ((input = in.readLine()) != null) {
                return input;
            }
        } catch (IOException e) {
            this.shouldStop = true;
        }
        return input;
    }

    public void close() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        serverEventHandler.onDisconnect(this);
    }

    public void setShouldStop(boolean shouldStop) {
        this.shouldStop = shouldStop;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("SocketHandler{");
        sb.append("id='").append(id).append('\'');
        sb.append('}');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DigitSocketReader)) return false;
        DigitSocketReader that = (DigitSocketReader) o;
        return !(id != null ? !id.equals(that.id) : that.id != null);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
