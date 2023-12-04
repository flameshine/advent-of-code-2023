package com.flameshine.advent.days;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

import com.flameshine.advent.util.Utils;

/**
 * Day 2: Cube Conundrum
 *
 * Part 1:
 *
 * You're launched high into the atmosphere!
 * The apex of your trajectory just barely reaches the surface of a large island floating in the sky.
 * You gently land in a fluffy pile of leaves.
 * It's quite cold, but you don't see much snow.
 * An Elf runs over to greet you.
 * The Elf explains that you've arrived at Snow Island and apologizes for the lack of snow.
 * He'll be happy to explain the situation, but it's a bit of a walk, so you have some time.
 * They don't get many visitors up here; would you like to play a game in the meantime?
 * As you walk, the Elf shows you a small bag and some cubes which are either red, green, or blue.
 * Each time you play this game, he will hide a secret number of cubes of each color in the bag, and your goal is to figure out information about the number of cubes.
 * To get information, once a bag has been loaded with cubes, the Elf will reach into the bag, grab a handful of random cubes, show them to you, and then put them back in the bag.
 * He'll do this a few times per game.
 * You play several games and record the information from each game (your puzzle input).
 * Each game is listed with its ID number (like the 11 in Game 11: ...) followed by a semicolon-separated list of subsets of cubes that were revealed from the bag (like 3 red, 5 green, 4 blue).
 * For example, the record of a few games might look like this:
 *   - Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
 *   - Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
 *   - Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
 *   - Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
 *   - Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
 * In game 1, three sets of cubes are revealed from the bag (and then put back again).
 * The first set is 3 blue cubes and 4 red cubes; the second set is 1 red cube, 2 green cubes, and 6 blue cubes; the third set is only 2 green cubes.
 * The Elf would first like to know which games would have been possible if the bag contained only 12 red cubes, 13 green cubes, and 14 blue cubes?
 * In the example above, games 1, 2, and 5 would have been possible if the bag had been loaded with that configuration.
 * However, game 3 would have been impossible because at one point the Elf showed you 20 red cubes at once; similarly, game 4 would also have been impossible because the Elf showed you 15 blue cubes at once.
 * If you add up the IDs of the games that would have been possible, you get 8.
 * Determine which games would have been possible if the bag had been loaded with only 12 red cubes, 13 green cubes, and 14 blue cubes.
 * What is the sum of the IDs of those games?
 *
 * Part 2:
 *
 * The Elf says they've stopped producing snow because they aren't getting any water!
 * He isn't sure why the water stopped; however, he can show you how to get to the water source to check it out for yourself.
 * It's just up ahead!
 * As you continue your walk, the Elf poses a second question: in each game you played, what is the fewest number of cubes of each color that could have been in the bag to make the game possible?
 * Again consider the example games from earlier:
 *   - Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
 *   - Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
 *   - Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
 *   - Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
 *   - Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
 * Explanations:
 *   - In game 1, the game could have been played with as few as 4 red, 2 green, and 6 blue cubes.
 *   If any color had even one fewer cube, the game would have been impossible.
 *   - Game 2 could have been played with a minimum of 1 red, 3 green, and 4 blue cubes.
 *   - Game 3 must have been played with at least 20 red, 13 green, and 6 blue cubes.
 *   - Game 4 required at least 14 red, 3 green, and 15 blue cubes.
 *   - Game 5 needed no fewer than 6 red, 3 green, and 2 blue cubes in the bag.
 * The power of a set of cubes is equal to the numbers of red, green, and blue cubes multiplied together.
 * The power of the minimum set of cubes in game 1 is 48.
 * In games 2-5 it was 12, 1560, 630, and 36, respectively.
 * Adding up these five powers produces the sum 2286.
 * For each game, find the minimum set of cubes that must have been present.
 * What is the sum of the power of these sets?
 */
public class Day2 {

    private static final Pattern GAME_ID_PATTERN = Pattern.compile("\\d+");
    private static final Pattern CUBE_CONFIGURATION_PATTERN = Pattern.compile("(\\d+) (\\w+)");

    public static void main(String... args) {

        var lines = Utils.readAllLines(Day2.class.getResource("day2/games.txt"));
        var games = lines.stream().map(Day2::parseGame).toList();

        // Part 1

        System.out.println(
            games.stream().filter(Game::isPossible).map(Game::id).mapToInt(Integer::intValue).sum()
        );

        // Part 2

        System.out.println(
            games.stream().map(g -> calculatePowerOfRequiredCubes(g.configurations())).mapToInt(Integer::intValue).sum()
        );
    }

    private static Game parseGame(String game) {

        var gameId = extractGameId(game);
        var gameIdSeparatorIndex = game.indexOf(":");
        var sets = game.substring(gameIdSeparatorIndex + 2).split("; ");

        List<CubeConfiguration> configurations = new LinkedList<>();

        for (var set : sets) {
            var configurationStrings = set.split(", ");
            for (var configurationString : configurationStrings) {
                var cubeConfiguration = buildCubeConfiguration(configurationString);
                configurations.add(cubeConfiguration);
            }
        }

        return new Game(gameId, configurations);
    }

    private static int calculatePowerOfRequiredCubes(List<CubeConfiguration> configurations) {
        var maximumRed = calculateRequiredCubeCountByColor(configurations, ColorConfiguration.RED);
        var maximumGreen = calculateRequiredCubeCountByColor(configurations, ColorConfiguration.GREEN);
        var maximumBlue = calculateRequiredCubeCountByColor(configurations, ColorConfiguration.BLUE);
        return maximumRed * maximumGreen * maximumBlue;
    }

    private static int extractGameId(String game) {
        var matcher = GAME_ID_PATTERN.matcher(game);
        Preconditions.checkState(matcher.find());
        var groupIdAsString = matcher.group(0);
        return Utils.parseInt(groupIdAsString);
    }

    private static CubeConfiguration buildCubeConfiguration(String cubeConfiguration) {

        var matcher = CUBE_CONFIGURATION_PATTERN.matcher(cubeConfiguration);

        Preconditions.checkState(matcher.find());

        var countAsString = matcher.group(1);
        var count = Utils.parseInt(countAsString);
        var color = matcher.group(2);
        var colorConfiguration = ColorConfiguration.valueOf(color.toUpperCase());

        return new CubeConfiguration(count, colorConfiguration);
    }

    private static int calculateRequiredCubeCountByColor(List<CubeConfiguration> configurations, ColorConfiguration colorConfiguration) {
        return configurations.stream()
            .filter(c -> colorConfiguration == c.colorConfiguration())
            .map(CubeConfiguration::count)
            .max(Integer::compareTo)
            .orElse(1);
    }

    private record Game(
        int id,
        List<CubeConfiguration> configurations
    ) {
        public boolean isPossible() {
            return configurations.stream().allMatch(c -> c.count() <= c.colorConfiguration().limit());
        }
    }

    private record CubeConfiguration(
        int count,
        ColorConfiguration colorConfiguration
    ) {}

    private enum ColorConfiguration {

        RED(12),
        GREEN(13),
        BLUE(14);

        private final int limit;

        ColorConfiguration(int limit) {
            this.limit = limit;
        }

        public int limit() {
            return limit;
        }
    }
}