package com.equinox.qikexpress.Utils;

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
}
