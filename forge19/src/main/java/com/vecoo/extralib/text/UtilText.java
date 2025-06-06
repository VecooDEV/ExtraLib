package com.vecoo.extralib.text;

public class UtilText {
    public static String getFormattedFloat(float value) {
        String format = String.format("%.3f", value)
                .replaceAll("\\.?0+$", "");

        if (format.endsWith(",")) {
            format = format.replace(",", "");
        }

        return format;
    }
}