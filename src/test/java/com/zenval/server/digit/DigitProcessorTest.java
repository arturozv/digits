package com.zenval.server.digit;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by arturo on 26/05/17.
 */
public class DigitProcessorTest {

    private DigitProcessor digitProcessor;

    @Test
    public void digit_processor() {
        DigitUniqueControl digitUniqueControl = mock(DigitUniqueControl.class);

        String digit = "123";
        when(digitUniqueControl.isUnique(digit)).thenReturn(true);
        digitProcessor = new DigitProcessor(digitUniqueControl);

        DigitProcessor.DIGIT_RESULT result = digitProcessor.process(digit);

        assertEquals(result, DigitProcessor.DIGIT_RESULT.OK);

        verify(digitUniqueControl).isUnique(digit);
    }

}
