package com.zenval.server.digit;

import com.zenval.Application;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

/**
 * Created by arturo on 26/05/17.
 */
public class DigitProcessorTest {

    private DigitProcessor digitProcessor;

    @Test
    public void digit_processor_ok() {
        DigitUniqueControl digitUniqueControl = mock(DigitUniqueControl.class);

        String digit = "0123";
        when(digitUniqueControl.isUnique(digit)).thenReturn(true);
        digitProcessor = new DigitProcessor(digitUniqueControl);

        DigitProcessor.DIGIT_RESULT result = digitProcessor.process(digit);

        assertEquals(result, DigitProcessor.DIGIT_RESULT.OK);

        verify(digitUniqueControl).isUnique(digit);
    }


    @Test
    public void digit_processor_ok_then_duplicate() {
        DigitUniqueControl digitUniqueControl = mock(DigitUniqueControl.class);

        String digit = "123";
        when(digitUniqueControl.isUnique(digit)).thenReturn(true, false);

        digitProcessor = new DigitProcessor(digitUniqueControl);

        DigitProcessor.DIGIT_RESULT result = digitProcessor.process(digit);
        assertEquals(result, DigitProcessor.DIGIT_RESULT.OK);

        result = digitProcessor.process(digit);
        assertEquals(result, DigitProcessor.DIGIT_RESULT.DUPLICATED);
    }



    @Test
    public void digit_processor_terminate() {
        DigitUniqueControl digitUniqueControl = mock(DigitUniqueControl.class);

        String digit = Application.TERMINATE_COMMAND;
        when(digitUniqueControl.isUnique(digit)).thenReturn(true);

        digitProcessor = new DigitProcessor(digitUniqueControl);

        DigitProcessor.DIGIT_RESULT result = digitProcessor.process(digit);
        assertEquals(result, DigitProcessor.DIGIT_RESULT.TERMINATE);

        verify(digitUniqueControl, never()).isUnique(digit);
    }

    @Test
    public void digit_processor_wrong_format() {
        DigitUniqueControl digitUniqueControl = mock(DigitUniqueControl.class);

        String digit = "123ABC";
        when(digitUniqueControl.isUnique(digit)).thenReturn(true);

        digitProcessor = new DigitProcessor(digitUniqueControl);

        DigitProcessor.DIGIT_RESULT result = digitProcessor.process(digit);
        assertEquals(result, DigitProcessor.DIGIT_RESULT.WRONG_FORMAT);

        verify(digitUniqueControl, never()).isUnique(digit);
    }
}
