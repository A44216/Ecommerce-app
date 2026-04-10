package com.example.ecommerceapp.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static String formatDateTime(String isoTime) {
        if (isoTime == null || isoTime.isEmpty()) return "";

        try {
            // backend: 2026-04-10T13:22:00
            SimpleDateFormat input =
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

            Date date = input.parse(isoTime);

            SimpleDateFormat output =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

            return output.format(date);

        } catch (Exception e) {
            return isoTime;
        }
    }
}