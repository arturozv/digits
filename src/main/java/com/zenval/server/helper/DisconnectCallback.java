package com.zenval.server.helper;

import com.zenval.server.digit.DigitSocketReader;

/**
 * Created by arturo on 20/05/17.
 */
public interface DisconnectCallback {
    void onDisconnect(DigitSocketReader digitSocketReader);
}
