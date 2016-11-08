package com.equinox.qikexpress.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by mukht on 10/31/2016.
 */

public class StringManipulation {

    public static String CapsFirst(String string) {
        String[] words = string.split(" ");
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<words.length; i++) {
            builder.append(Character.toUpperCase(words[i].charAt(0))
                + words[i].substring(1).toLowerCase() + " ");
        }
        return builder.toString();
    }

    public static String getFormattedDate(Integer seconds) {
        Long sec = (long) seconds;
        sec *= 1000;
        Date date = new Date(sec);
        DateFormat simpleDateFormat = SimpleDateFormat.getDateTimeInstance();
        return simpleDateFormat.format(date);
    }
}
