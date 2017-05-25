package com.zenval.server.digit;

import com.zenval.Application;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by arturo on 20/05/17.
 */
public class DigitProcessor {
    private DigitWriterAggregator aggregator;
    private DigitUniqueControl digitUniqueControl;

    public DigitProcessor(DigitWriterAggregator aggregator, DigitUniqueControl digitUniqueControl) {
        this.aggregator = aggregator;
        this.digitUniqueControl = digitUniqueControl;
    }

    public DIGIT_RESULT process(String input) {
        DIGIT_RESULT result;
        if (Application.TERMINATE_COMMAND.equals(input)) {
            result = DIGIT_RESULT.TERMINATE;

        } else if (NumberUtils.isDigits(input)) {
            if (digitUniqueControl.isUnique(input)) {
                aggregator.offer(input);
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
