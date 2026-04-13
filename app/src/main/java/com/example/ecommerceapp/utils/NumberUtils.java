package com.example.ecommerceapp.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberUtils {

    public static String formatCompact(BigDecimal number) {
        if (number == null) return "0";

        BigDecimal billion = new BigDecimal("1000000000");
        BigDecimal million = new BigDecimal("1000000");
        BigDecimal thousand = new BigDecimal("1000");

        if (number.compareTo(billion) >= 0) {
            return format(number.divide(billion)) + "B";
        } else if (number.compareTo(million) >= 0) {
            return format(number.divide(million)) + "M";
        } else if (number.compareTo(thousand) >= 0) {
            return format(number.divide(thousand)) + "K";
        } else {
            return number.setScale(0, RoundingMode.DOWN).toPlainString();
        }
    }

    private static String format(BigDecimal value) {
        return value.setScale(1, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }
}