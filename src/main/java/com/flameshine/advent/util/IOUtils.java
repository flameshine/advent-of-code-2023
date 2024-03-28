package com.flameshine.advent.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.google.common.base.Preconditions;

public final class IOUtils {

    private IOUtils() {}

    public static List<String> readAllLines(URL url) {
        Preconditions.checkState(url != null);
        try (var lines = Files.lines(Path.of(url.getPath()), StandardCharsets.UTF_8)) {
            return lines.toList();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}