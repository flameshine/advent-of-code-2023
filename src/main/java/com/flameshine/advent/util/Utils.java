package com.flameshine.advent.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.common.base.Preconditions;

public final class Utils {

    private Utils() {}

    public static List<String> readAllLines(URL url) {
        Preconditions.checkState(url != null);
        try (var lines = Files.lines(Path.of(url.getPath()), StandardCharsets.UTF_8)) {
            return lines.toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

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