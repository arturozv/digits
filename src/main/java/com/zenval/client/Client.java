package com.zenval.client;

import com.zenval.Application;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by arturo on 20/05/17.
 */
public class Client implements Runnable {

    private Socket socket;
    private PrintWriter out;
    private ClientEventCallback clientEventCallback;

    public Client(String host, int port, ClientEventCallback clientEventCallback) {
        this.clientEventCallback = clientEventCallback;

        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            clientEventCallback.onConnectionSuccess();

        } catch (IOException e) {
            e.printStackTrace();
            clientEventCallback.onDisconnect();
        }
    }

    @Override
    public void run() {
        while (!out.checkError()) {
            String digits = RandomStringUtils.randomNumeric(9);

            //randomly send a terminate command
            if (Long.parseLong(digits) % 10000000 == 0) {
                out.println(Application.TERMINATE_COMMAND);
                clientEventCallback.onTerminate();
                break;

            //randomly send a bad digit
            } else if (Long.parseLong(digits) % 1000000 == 0) {
                out.println(digits+"a"); //force error

            //send good digit
            } else {
                out.println(digits);
            }
        }
        clientEventCallback.onDisconnect();
    }
}
