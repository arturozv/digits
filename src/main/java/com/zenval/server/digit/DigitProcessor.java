package com.zenval.server.digit;

import com.google.common.eventbus.EventBus;

import com.zenval.Application;
import com.zenval.server.helper.TerminateSignal;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by arturo on 20/05/17.
 */
public class DigitProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DigitProcessor.class);

    private EventBus eventbus;
    private DigitUniqueControl digitUniqueControl;

    public DigitProcessor(EventBus eventbus, DigitUniqueControl digitUniqueControl){
        this.eventbus = eventbus;
        this.digitUniqueControl = digitUniqueControl;
    }

    boolean processAndKeepRunning(String input) {
        if (Application.TERMINATE_COMMAND.equals(input)) {
            logger.info("{} received a Terminate Signal by SOCKET!!", this.toString());
            eventbus.post(new TerminateSignal());

        } else if (NumberUtils.isDigits(input)){
            if (digitUniqueControl.isUnique(input)) {
                eventbus.post(input);
            }

        } else {
            logger.debug("{} received a BAD input: {}!", this.toString(), input);
            return false;
        }
        return true;
    }
}
