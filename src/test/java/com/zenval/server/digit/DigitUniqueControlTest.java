package com.zenval.server.digit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;

/**
 * Created by arturo on 26/05/17.
 */
public class DigitUniqueControlTest {
    private DigitUniqueControl digitUniqueControl;

    @Test
    public void unique_control_ok() {
        digitUniqueControl = new DigitUniqueControl();

        String digit = "0123";

        boolean result = digitUniqueControl.isUnique(digit);
        assertEquals(result, true);


        result = digitUniqueControl.isUnique(digit);
        assertEquals(result, false);
    }
}