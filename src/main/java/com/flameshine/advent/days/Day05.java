package com.flameshine.advent.days;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.flameshine.advent.util.ParsingUtils;

/**
 * Day 5: If You Give A Seed A Fertilizer
 *
 * Part 1:
 *
 * You take the boat and find the gardener right where you were told he would be: managing a giant "garden" that looks more to you like a farm.
 * "A water source? Island Island is the water source!"
 * You point out that Snow Island isn't receiving any water.
 * "Oh, we had to stop the water because we ran out of sand to filter it with! Can't make snow with dirty water. Don't worry, I'm sure we'll get more sand soon; we only turned off the water a few days... weeks... oh no."
 * His face sinks into a look of horrified realization.
 * "I've been so busy making sure everyone here has food that I completely forgot to check why we stopped getting more sand! There's a ferry leaving soon that is headed over in that direction - it's much faster than your boat. Could you please go check it out?"
 * You barely have time to agree to this request when he brings up another.
 * "While you wait for the ferry, maybe you can help us with our food production problem. The latest Island Island Almanac just arrived and we're having trouble making sense of it."
 * The almanac (your puzzle input) lists all of the seeds that need to be planted.
 * It also lists what type of soil to use with each kind of seed, what type of fertilizer to use with each kind of soil, what type of water to use with each kind of fertilizer, and so on.
 * Every type of seed, soil, fertilizer and so on is identified with a number, but numbers are reused by each category - that is, soil 123 and fertilizer 123 aren't necessarily related to each other.
 * For example:
 *
 * seeds: 79 14 55 13
 *
 * seed-to-soil map:
 * 50 98 2
 * 52 50 48
 *
 * soil-to-fertilizer map:
 * 0 15 37
 * 37 52 2
 * 39 0 15
 *
 * fertilizer-to-water map:
 * 49 53 8
 * 0 11 42
 * 42 0 7
 * 57 7 4
 *
 * water-to-light map:
 * 88 18 7
 * 18 25 70
 *
 * light-to-temperature map:
 * 45 77 23
 * 81 45 19
 * 68 64 13
 *
 * temperature-to-humidity map:
 * 0 69 1
 * 1 0 69
 *
 * humidity-to-location map:
 * 60 56 37
 * 56 93 4
 *
 * The almanac starts by listing which seeds need to be planted: seeds 79, 14, 55, and 13.
 * The rest of the almanac contains a list of maps which describe how to convert numbers from a source category into numbers in a destination category.
 * That is, the section that starts with seed-to-soil map: describes how to convert a seed number (the source) to a soil number (the destination).
 * This lets the gardener and his team know which soil to use with which seeds, which water to use with which fertilizer, and so on.
 * Rather than list every source number and its corresponding destination number one by one, the maps describe entire ranges of numbers that can be converted.
 * Each line within a map contains three numbers: the destination range start, the source range start, and the range length.
 * Consider again the example seed-to-soil map:
 *
 * 50 98 2
 * 52 50 48
 *
 * The first line has a destination range start of 50, a source range start of 98, and a range length of 2.
 * This line means that the source range starts at 98 and contains two values: 98 and 99.
 * The destination range is the same length, but it starts at 50, so its two values are 50 and 51.
 * With this information, you know that seed number 98 corresponds to soil number 50 and that seed number 99 corresponds to soil number 51.
 * The second line means that the source range starts at 50 and contains 48 values: 50, 51, ..., 96, 97.
 * This corresponds to a destination range starting at 52 and also containing 48 values: 52, 53, ..., 98, 99. So, seed number 53 corresponds to soil number 55.
 * Any source numbers that aren't mapped correspond to the same destination number.
 * So, seed number 10 corresponds to soil number 10.
 * So, the entire list of seed numbers and their corresponding soil numbers looks like this:
 *
 * seed  soil
 * 0     0
 * 1     1
 * ...   ...
 * 48    48
 * 49    49
 * 50    52
 * 51    53
 * ...   ...
 * 96    98
 * 97    99
 * 98    50
 * 99    51
 *
 * With this map, you can look up the soil number required for each initial seed number:
 *
 * - Seed number 79 corresponds to soil number 81.
 * - Seed number 14 corresponds to soil number 14.
 * - Seed number 55 corresponds to soil number 57.
 * - Seed number 13 corresponds to soil number 13.
 *
 * The gardener and his team want to get started as soon as possible, so they'd like to know the closest location that needs a seed.
 * Using these maps, find the lowest location number that corresponds to any of the initial seeds.
 * To do this, you'll need to convert each seed number through other categories until you can find its corresponding location number.
 * In this example, the corresponding types are:
 *
 * - Seed 79, soil 81, fertilizer 81, water 81, light 74, temperature 78, humidity 78, location 82.
 * - Seed 14, soil 14, fertilizer 53, water 49, light 42, temperature 42, humidity 43, location 43.
 * - Seed 55, soil 57, fertilizer 57, water 53, light 46, temperature 82, humidity 82, location 86.
 * - Seed 13, soil 13, fertilizer 52, water 41, light 34, temperature 34, humidity 35, location 35.
 *
 * So, the lowest location number in this example is 35.
 * What is the lowest location number that corresponds to any of the initial seed numbers?
 *
 * Part 2:
 *
 * Everyone will starve if you only plant such a small number of seeds.
 * Re-reading the almanac, it looks like the seeds: line actually describes ranges of seed numbers.
 * The values on the initial seeds: line come in pairs.
 * Within each pair, the first value is the start of the range and the second value is the length of the range.
 * So, in the first line of the example above:
 * seeds: 79 14 55 13
 * This line describes two ranges of seed numbers to be planted in the garden.
 * The first range starts with seed number 79 and contains 14 values: 79, 80, ..., 91, 92.
 * The second range starts with seed number 55 and contains 13 values: 55, 56, ..., 66, 67.
 * Now, rather than considering four seed numbers, you need to consider a total of 27 seed numbers.
 * In the above example, the lowest location number can be obtained from seed number 82, which corresponds to soil 84, fertilizer 84, water 84, light 77, temperature 45, humidity 46, and location 46.
 * So, the lowest location number is 46.
 * Consider all of the initial seed numbers listed in the ranges on the first line of the almanac.
 * What is the lowest location number that corresponds to any of the initial seed numbers?
 */
public class Day05 {

    private static final List<MappingDescriptor> DESTINATION_DESCRIPTORS = List.of(
        new MappingDescriptor(Category.SEED, Category.SOIL),
        new MappingDescriptor(Category.SOIL, Category.FERTILIZER),
        new MappingDescriptor(Category.FERTILIZER, Category.WATER),
        new MappingDescriptor(Category.WATER, Category.LIGHT),
        new MappingDescriptor(Category.LIGHT, Category.TEMPERATURE),
        new MappingDescriptor(Category.TEMPERATURE, Category.HUMIDITY),
        new MappingDescriptor(Category.HUMIDITY, Category.LOCATION)
    );

    public static void main(String... args) throws ExecutionException, InterruptedException {

        List<Long> seeds = new ArrayList<>();
        Map<Long, Long> seedRanges = new HashMap<>();
        List<AlmanacEntry> entries = new ArrayList<>();

        try (var scanner = new Scanner(new File(Objects.requireNonNull(Day05.class.getResource("day05/almanac.txt")).getPath()))) {

            if (scanner.hasNextLine()) {
                var seedsLine = scanner.nextLine();
                var seedsAsString = seedsLine.split(" ");
                for (var i = 1; i < seedsAsString.length; i += 2) {
                    var firstSeedItem = ParsingUtils.parseLong(seedsAsString[i]);
                    var secondSeedItem = ParsingUtils.parseLong(seedsAsString[i + 1]);
                    seeds.add(firstSeedItem);
                    seeds.add(secondSeedItem);
                    seedRanges.put(firstSeedItem, firstSeedItem + secondSeedItem);
                }
            }

            scanner.nextLine();

            for (var destination : DESTINATION_DESCRIPTORS) {
                scanner.nextLine();
                while (scanner.hasNextLine()) {
                    var line = scanner.nextLine();
                    if (line.isBlank()) {
                        break;
                    }
                    var entry = buildEntry(destination, line);
                    entries.add(entry);
                }
            }
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }

        // Part 1

        System.out.println(
            findMinimumLocationForListedSeeds(seeds, entries)
        );

        // Part 2

        System.out.println(
            findMinimumLocationForSeedRanges(seedRanges, entries)
        );
    }

    private static Long findMinimumLocationForListedSeeds(List<Long> seeds, List<AlmanacEntry> entries) {

        var min = Long.MAX_VALUE;

        for (var seed : seeds) {
            Set<MappingDescriptor> processed = new HashSet<>();
            for (var entry : entries) {
                var descriptor = entry.mappingDescriptor();
                if (!processed.contains(descriptor) && entry.contains(seed)) {
                    seed = entry.map(seed);
                    processed.add(descriptor);
                }
            }
            min = Math.min(min, seed);
        }

        return min;
    }

    private static Long findMinimumLocationForSeedRanges(Map<Long, Long> seedRanges, List<AlmanacEntry> entries) throws ExecutionException, InterruptedException {

        List<Future<Long>> futures = new ArrayList<>();

        try (var executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())) {

            for (var seedRange : seedRanges.entrySet()) {

                var future = executorService.submit(() -> {
                    var location = Long.MAX_VALUE;
                    for (var i = seedRange.getKey(); i < seedRange.getValue(); i++) {
                        Set<MappingDescriptor> processed = new HashSet<>();
                        var seed = i;
                        for (var entry : entries) {
                            var descriptor = entry.mappingDescriptor();
                            if (!processed.contains(descriptor) && entry.contains(seed)) {
                                seed = entry.map(seed);
                                processed.add(descriptor);
                            }
                        }
                        location = Math.min(location, seed);
                    }
                    return location;
                });

                futures.add(future);
            }
        }

        var min = Long.MAX_VALUE;

        for (var future : futures) {
            min = Math.min(min, future.get());
        }

        return min;
    }

    private static AlmanacEntry buildEntry(MappingDescriptor mappingDescriptor, String line) {
        var values = line.split(" ");
        var destinationRangeStart = ParsingUtils.parseLong(values[0]);
        var sourceRangeStart = ParsingUtils.parseLong(values[1]);
        var rangeLength = ParsingUtils.parseLong(values[2]);
        return new AlmanacEntry(
            mappingDescriptor,
            destinationRangeStart,
            sourceRangeStart,
            rangeLength
        );
    }

    private record MappingDescriptor(
        Category source,
        Category destination
    ) { }

    private record AlmanacEntry(
        MappingDescriptor mappingDescriptor,
        Long destinationRangeStart,
        Long sourceRangeStart,
        Long rangeLength
    ) {
        public boolean contains(Long seed) {
            return seed >= sourceRangeStart && seed <= sourceRangeStart + rangeLength;
        }

        public Long map(Long seed) {
            return destinationRangeStart + (seed - sourceRangeStart);
        }
    }

    private enum Category {
        SEED,
        SOIL,
        FERTILIZER,
        WATER,
        LIGHT,
        TEMPERATURE,
        HUMIDITY,
        LOCATION
    }
}