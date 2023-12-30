package com.flameshine.advent.days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.flameshine.advent.util.IOUtils;
import com.flameshine.advent.util.ParsingUtils;

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
 * "Sorry, I wasn't expecting anyone! The gondola lift isn't working right now; it'll still be a while before I can fix it."
 * You offer to help.
 * The engineer explains that an engine part seems to be missing from the engine, but nobody can figure out which one.
 * If you can add up all the part numbers in the engine schematic, it should be easy to work out which part is missing.
 * The engine schematic (your puzzle input) consists of a visual representation of the engine.
 * There are lots of numbers and symbols you don't really understand, but apparently any number adjacent to a symbol, even diagonally, is a "part number" and should be included in your sum.
 * (Periods (.) do not count as a symbol.)
 * Here is an example engine schematic:
 *
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
 *
 * In this schematic, two numbers are not part numbers because they are not adjacent to a symbol: 114 (top right) and 58 (middle right).
 * Every other number is adjacent to a symbol and so is a part number; their sum is 4361.
 * Of course, the actual engine schematic is much larger.
 * What is the sum of all of the part numbers in the engine schematic?
 *
 * Part 2:
 *
 * The engineer finds the missing part and installs it in the engine! As the engine springs to life, you jump in the closest gondola, finally ready to ascend to the water source.
 * You don't seem to be going very fast, though.
 * Maybe something is still wrong?
 * Fortunately, the gondola has a phone labeled "help", so you pick it up and the engineer answers.
 * Before you can explain the situation, she suggests that you look out the window.
 * There stands the engineer, holding a phone in one hand and waving with the other.
 * You're going so slowly that you haven't even left the station.
 * You exit the gondola.
 * The missing part wasn't the only issue - one of the gears in the engine is wrong.
 * A gear is any * symbol that is adjacent to exactly two part numbers.
 * Its gear ratio is the result of multiplying those two numbers together.
 * This time, you need to find the gear ratio of every gear and add them all up so that the engineer can figure out which gear needs to be replaced.
 * Consider the same engine schematic again:
 *
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
 *
 * In this schematic, there are two gears.
 * The first is in the top left; it has part numbers 467 and 35, so its gear ratio is 16345.
 * The second gear is in the lower right; its gear ratio is 451490.
 * (The * adjacent to 617 is not a gear because it is only adjacent to one part number.)
 * Adding up all of the gear ratios produces 467835.
 * What is the sum of all of the gear ratios in your engine schematic?
 */
public class Day03 {

    private static final char[][] SCHEMATIC;

    private static int rows;
    private static int columns;

    static {

        var lines = IOUtils.readAllLinesFrom(Day03.class.getResource("day03/schematic.txt"));

        SCHEMATIC = lines.stream().map(String::toCharArray)
            .toArray(char[][]::new);
    }

    public static void main(String... args) {

        rows = SCHEMATIC.length;
        columns = SCHEMATIC[0].length;

        var schematicNumbers = extractSchematicNumbers();

        // Part 1

        var partNumberSum = 0;

        for (var number : schematicNumbers) {
            if (hasAdjacentSymbol(number)) {
                partNumberSum += number.value();
            }
        }

        System.out.println(partNumberSum);

        // Part 2

        var gearRatioSum = 0;

        for (var i = 0; i < rows; i++) {
            for (var j = 0; j < columns; j++) {
                if (SCHEMATIC[i][j] == '*') {
                    var adjacentNumbers = getAdjacent(i, j, schematicNumbers);
                    if (adjacentNumbers.size() == 2) {
                        var gearRatio = adjacentNumbers.stream()
                            .map(SchematicNumber::value)
                            .reduce((a, b) -> a * b)
                            .orElse(0);
                        gearRatioSum += gearRatio;
                    }
                }
            }
        }

        System.out.println(gearRatioSum);
    }

    private static List<SchematicNumber> extractSchematicNumbers() {

        List<SchematicNumber> resultBuilder = new ArrayList<>();

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

                var number = ParsingUtils.parseInt(numberBuilder.toString());

                resultBuilder.add(
                    new SchematicNumber(numberStartIndex, j, i, number)
                );
            }
        }

        return Collections.unmodifiableList(resultBuilder);
    }

    private static boolean hasAdjacentSymbol(SchematicNumber schematicNumber) {

        var schematicNumberRow = schematicNumber.row();

        for (var i = schematicNumber.columnRangeStart(); i < schematicNumber.columnRangeEnd(); i++) {

            for (var direction : new int[][] {{ -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }}) {
                var x = schematicNumberRow + direction[0];
                var y = i + direction[1];
                if (x >= 0 && x < rows && y >= 0 && y < columns && isSymbolExcludingDot(SCHEMATIC[x][y])) {
                    return true;
                }
            }

            for (var direction : new int[][] {{ -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 }}) {
                var x = schematicNumberRow + direction[0];
                var y = i + direction[1];
                if (x >= 0 && x < rows && y >= 0 && y < columns && isSymbolExcludingDot(SCHEMATIC[x][y])) {
                    return true;
                }
            }
        }

        return false;
    }

    private static boolean isSymbolExcludingDot(char c) {
        return !Character.isLetterOrDigit(c) && c != '.';
    }

    private static List<SchematicNumber> getAdjacent(int row, int column, List<SchematicNumber> schematicNumbers) {

        List<SchematicNumber> resultBuilder = new ArrayList<>();

        for (var number : schematicNumbers) {

            SchematicNumber gearPart = null;

            for (var direction : new int[][] {{ -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 }}) {
                var x = row + direction[0];
                var y = column + direction[1];
                if (x >= 0 && x < rows && y >= 0 && y < columns && number.contains(x, y)) {
                    gearPart = number;
                    break;
                }
            }

            if (gearPart != null) {
                resultBuilder.add(gearPart);
                continue;
            }

            for (var direction : new int[][] {{ -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 }}) {
                var x = row + direction[0];
                var y = column + direction[1];
                if (x >= 0 && x < rows && y >= 0 && y < columns && number.contains(x, y)) {
                    resultBuilder.add(number);
                    break;
                }
            }
        }

        return Collections.unmodifiableList(resultBuilder);
    }

    private record SchematicNumber(
        int columnRangeStart,
        int columnRangeEnd,
        int row,
        int value
    ) {
        boolean contains(int rowIndex, int columnIndex) {
            return rowIndex == row && columnIndex >= columnRangeStart && columnIndex < columnRangeEnd;
        }
    }
}