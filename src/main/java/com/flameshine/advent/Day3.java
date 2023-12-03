package com.flameshine.advent;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.common.base.Preconditions;

/**
 * Day 3: Gear Ratios
 *
 * Part 1:
 *
 * You and the Elf eventually reach a gondola lift station; he says the gondola lift will take you up to the water source, but this is as far as he can bring you.
 * You go inside.
 * It doesn't take long to find the gondolas, but there seems to be a problem: they're not moving.
 * "Aaah!"
 * You turn around to see a slightly-greasy Elf with a wrench and a look of surprise.
 * "Sorry, I wasn't expecting anyone!
 * The gondola lift isn't working right now; it'll still be a while before I can fix it."
 * You offer to help.
 * The engineer explains that an engine part seems to be missing from the engine, but nobody can figure out which one.
 * If you can add up all the part numbers in the engine schematic, it should be easy to work out which part is missing.
 * The engine schematic (your puzzle input) consists of a visual representation of the engine.
 * There are lots of numbers and symbols you don't really understand, but apparently any number adjacent to a symbol, even diagonally, is a "part number" and should be included in your sum.
 * (Periods (.) do not count as a symbol.)
 * Here is an example engine schematic:
 * 467..114..
 * ...*......
 * ..35..633.
 * ......#...
 * 617*......
 * .....+.58.
 * ..592.....
 * ......755.
 * ...$.*....
 * .664.598..
 * In this schematic, two numbers are not part numbers because they arev c not adjacent to a symbol: 114 (top right) and 58 (middle right).
 * Every other number is adjacent to a symbol and so is a part number; their sum is 4361.
 * Of course, the actual engine schematic is much larger.
 * What is the sum of all of the part numbers in the engine schematic?
 *
 * Part 2: to do
 */
public class Day3 {

    private static final char[][] SCHEMATIC;

    private static int rows;
    private static int columns;

    static {

        var fileUrl = Day3.class.getResource("day3/schematic.txt");

        Preconditions.checkState(fileUrl != null);

        try (var lines = Files.lines(Path.of(fileUrl.getPath()), StandardCharsets.UTF_8)) {

            SCHEMATIC = lines.map(String::toCharArray)
                .toArray(char[][]::new);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void main(String... args) {

        var sum = 0;

        rows = SCHEMATIC.length;
        columns = SCHEMATIC[0].length;

        for (var i = 0; i < rows; i++) {
            for (var j = 0; j < columns; j++) {

                var numberBuilder = new StringBuilder();
                var numberStartIndex = j;

                while (j != columns && Character.isDigit(SCHEMATIC[i][j])) {
                    numberBuilder.append(SCHEMATIC[i][j++]);
                }

                if (numberBuilder.isEmpty()) {
                    continue;
                }

                int number;
                try {
                    number = Integer.parseInt(numberBuilder.toString());
                } catch (NumberFormatException e) {
                    throw new IllegalStateException("Unable to parse schematic element", e);
                }

                if (hasAdjacentSymbol(i, numberStartIndex, j)) {
                    sum += number;
                }
            }
        }

        System.out.println(sum);
    }

    private static boolean hasAdjacentSymbol(int row, int numberStartIndex, int numberEndIndex) {
        for (var i = numberStartIndex; i < numberEndIndex; i++) {

            // check horizontally

            for (var direction : new int[][] {{ -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }}) {
                var x = row + direction[0];
                var y = i + direction[1];
                if (x >= 0 && x < rows && y >= 0 && y < columns && isSymbol(SCHEMATIC[x][y])) {
                    return true;
                }
            }

            // check diagonally

            for (var direction : new int[][] {{ -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 }}) {
                var x = row + direction[0];
                var y = i + direction[1];
                if (x >= 0 && x < rows && y >= 0 && y < columns && isSymbol(SCHEMATIC[x][y])) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isSymbol(char c) {
        return !Character.isLetterOrDigit(c) && c != '.';
    }
}