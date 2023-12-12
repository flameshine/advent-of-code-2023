package com.flameshine.advent.util;

public final class StringUtils {

    private StringUtils() {}

    public static String removeTrailingWhitespaces(String s) {
        return s.replaceAll("\\s{2,}", " ").trim();
    }
}