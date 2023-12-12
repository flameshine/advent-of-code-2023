package com.flameshine.advent.util;

public final class ParsingUtils {

    private ParsingUtils() {}

    public static Integer parseInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Unable to parse integer from input string: " + s, e);
        }
    }

    public static Long parseLong(String s) {
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Unable to parse long from input string: " + s, e);
        }
    }
}