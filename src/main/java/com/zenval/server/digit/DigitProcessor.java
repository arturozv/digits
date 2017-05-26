package com.zenval.server.digit;

import com.zenval.Application;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Process the digit in the server prior to write it into a file.
 */
public class DigitProcessor {
    private DigitUniqueControl digitUniqueControl;

    public DigitProcessor(DigitUniqueControl digitUniqueControl) {
        this.digitUniqueControl = digitUniqueControl;
    }

    public DIGIT_RESULT process(String input) {
        DIGIT_RESULT result;

        if (Application.TERMINATE_COMMAND.equals(input)) {
            result = DIGIT_RESULT.TERMINATE;

        } else if (!NumberUtils.isDigits(input)) {
            result = DIGIT_RESULT.WRONG_FORMAT;

        } else if (!digitUniqueControl.isUnique(input)) {
            result = DIGIT_RESULT.DUPLICATED;

        } else {
            result = DIGIT_RESULT.OK;
        }

        return result;
    }

    public enum DIGIT_RESULT {
        OK, WRONG_FORMAT, DUPLICATED, TERMINATE
    }
}
