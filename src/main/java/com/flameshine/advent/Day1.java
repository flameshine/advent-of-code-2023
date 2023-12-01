package com.flameshine.advent;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import com.google.common.base.Preconditions;

/**
 * Day 1: Trebuchet?!
 *
 * Part 1:
 *
 * You try to ask why they can't just use a weather machine ("not powerful enough") and where they're even sending you ("the sky") and why your map looks mostly blank ("you sure ask a lot of questions") and hang on did you just say the sky ("of course, where do you think snow comes from") when you realize that the Elves are already loading you into a trebuchet ("please hold still, we need to strap you in").
 * As they're making the final adjustments, they discover that their calibration document (your puzzle input) has been amended by a very young Elf who was apparently just excited to show off her art skills.
 * Consequently, the Elves are having trouble reading the values on the document.
 * The newly-improved calibration document consists of lines of text; each line originally contained a specific calibration value that the Elves now need to recover.
 * On each line, the calibration value can be found by combining the first digit and the last digit (in that order) to form a single two-digit number.
 *
 * Part 2:
 *
 * Your calculation isn't quite right.
 * It looks like some of the digits are actually spelled out with letters: one, two, three, four, five, six, seven, eight, and nine also count as valid "digits".
 * Equipped with this new information, you now need to find the real first and last digit on each line.
 */
public class Day1 {

    private static final Map<String, Integer> DIGITS = Map.of(
        "one", 1,
        "two", 2,
        "three", 3,
        "four", 4,
        "five", 5,
        "six", 6,
        "seven", 7,
        "eight", 8,
        "nine", 9
    );

    public static void main(String... args) {

        var fileUrl = Day1.class.getResource("day1/input.txt");

        Preconditions.checkState(fileUrl != null);

        try (var lines = Files.lines(Path.of(fileUrl.getPath()), StandardCharsets.UTF_8)) {

            System.out.println(
                lines.map(Day1::extractCalibrationValues).mapToInt(Integer::intValue).sum()
            );

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static int extractCalibrationValues(String s) {

        var leftAlphabeticDigit = Integer.MAX_VALUE;
        var leftAlphabeticDigitIndex = Integer.MAX_VALUE;

        for (var entry : DIGITS.entrySet()) {
            var index = s.indexOf(entry.getKey());
            if (index != -1 && leftAlphabeticDigitIndex > index) {
                leftAlphabeticDigit = entry.getValue();
                leftAlphabeticDigitIndex = index;
            }
        }

        var rightAlphabeticDigit = Integer.MAX_VALUE;
        var rightAlphabeticDigitIndex = Integer.MIN_VALUE;

        for (var entry : DIGITS.entrySet()) {
            var index = s.lastIndexOf(entry.getKey());
            if (index != -1 && leftAlphabeticDigitIndex < index && rightAlphabeticDigitIndex < index) {
                rightAlphabeticDigit = entry.getValue();
                rightAlphabeticDigitIndex = index;
            }
        }

        if (rightAlphabeticDigit == Integer.MAX_VALUE) {
            if (leftAlphabeticDigit == Integer.MAX_VALUE) {
                rightAlphabeticDigit = Integer.MIN_VALUE;
                rightAlphabeticDigitIndex = Integer.MIN_VALUE;
            } else {
                rightAlphabeticDigit = leftAlphabeticDigit;
                rightAlphabeticDigitIndex = leftAlphabeticDigitIndex;
            }
        }

        var leftNumericDigit = Integer.MAX_VALUE;
        var leftNumericDigitIndex = Integer.MAX_VALUE;

        for (var entry : DIGITS.entrySet()) {
            var value = entry.getValue();
            var index = s.indexOf(String.valueOf(value));
            if (index != -1 && leftNumericDigitIndex > index) {
                leftNumericDigit = value;
                leftNumericDigitIndex = index;
            }
        }

        var rightNumericDigit = Integer.MAX_VALUE;
        var rightNumericDigitIndex = Integer.MIN_VALUE;

        for (var entry : DIGITS.entrySet()) {
            var value = entry.getValue();
            var index = s.lastIndexOf(String.valueOf(value));
            if (index != -1 && leftNumericDigitIndex < index && rightNumericDigitIndex < index) {
                rightNumericDigit = value;
                rightNumericDigitIndex = index;
            }
        }

        if (rightNumericDigit == Integer.MAX_VALUE) {
            if (leftNumericDigit == Integer.MAX_VALUE) {
                rightNumericDigit = Integer.MIN_VALUE;
                rightNumericDigitIndex = Integer.MIN_VALUE;
            } else {
                rightNumericDigit = leftNumericDigit;
                rightNumericDigitIndex = leftNumericDigitIndex;
            }
        }

        var first = leftAlphabeticDigitIndex < leftNumericDigitIndex ? leftAlphabeticDigit : leftNumericDigit;
        var second = rightAlphabeticDigitIndex > rightNumericDigitIndex ? rightAlphabeticDigit : rightNumericDigit;
        var result = String.valueOf(first) + second;

        try {
            return Integer.parseInt(result);
        } catch (NumberFormatException e) {
            throw new IllegalStateException("Unable to extract calibration values for string: " + s, e);
        }
    }
}