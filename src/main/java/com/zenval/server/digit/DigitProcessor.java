package com.zenval.server.digit;

import com.zenval.Application;
import com.zenval.server.file.DigitFileWriter;

import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

/**
 * Created by arturo on 20/05/17.
 */
public class DigitProcessor {
    private DigitFileWriter digitFileWriter;
    private DigitUniqueControl digitUniqueControl;

    public DigitProcessor(DigitFileWriter digitFileWriter, DigitUniqueControl digitUniqueControl) {
        this.digitFileWriter = digitFileWriter;
        this.digitUniqueControl = digitUniqueControl;
    }

    public DIGIT_RESULT process(String input) throws IOException {
        DIGIT_RESULT result;
        if (Application.TERMINATE_COMMAND.equals(input)) {
            result = DIGIT_RESULT.TERMINATE;

        } else if (NumberUtils.isDigits(input)) {
            if (digitUniqueControl.isUnique(input)) {
                digitFileWriter.writeAsync(input);
                result = DIGIT_RESULT.OK;
            } else {
                result = DIGIT_RESULT.DUPLICATED;
            }
        } else {
            return DIGIT_RESULT.WRONG_FORMAT;
        }
        return result;
    }

    public enum DIGIT_RESULT {
        OK, WRONG_FORMAT, DUPLICATED, TERMINATE
    }
}
