package com.zenval.client;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by arturo on 20/05/17.
 */
public class Client implements Runnable {

    private String host;
    private int port;

    private ClientEventCallback clientEventCallback;

    public Client(String host, int port, ClientEventCallback clientEventCallback) {
        this.clientEventCallback = clientEventCallback;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try (
                Socket socket = new Socket(host, port);
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true)
        ) {

            clientEventCallback.onConnectionSuccess();

            while (!out.checkError()) {
                out.println(RandomStringUtils.randomNumeric(9));
                Thread.sleep(100);
            }

        } catch (IOException e) {
            e.printStackTrace();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            clientEventCallback.onDisconnect();
        }
    }
}
