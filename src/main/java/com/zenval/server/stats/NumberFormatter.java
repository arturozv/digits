package com.zenval.server.stats;

import java.text.DecimalFormat;

/**
 * Created by someone from stackoverflow.
 */
public class NumberFormatter {
    private NumberFormatter() {
    }

    private static String[] suffix = new String[]{"", "k", "m", "b", "t"};

    public static String format(double number) {
        String r = new DecimalFormat("##0E0").format(number);
        r = r.replaceAll("E[0-9]", suffix[Character.getNumericValue(r.charAt(r.length() - 1)) / 3]);
        int MAX_LENGTH = 4;
        while (r.length() > MAX_LENGTH || r.matches("[0-9]+\\.[a-z]")) {
            r = r.substring(0, r.length() - 2) + r.substring(r.length() - 1);
        }
        return r;
    }
}
