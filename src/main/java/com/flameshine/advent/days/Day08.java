package com.flameshine.advent.days;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UncheckedIOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;

import com.google.common.base.Preconditions;

/**
 * Day 8: Haunted Wasteland
 *
 * Part 1:
 *
 * You're still riding a camel across Desert Island when you spot a sandstorm quickly approaching.
 * When you turn to warn the Elf, she disappears before your eyes!
 * To be fair, she had just finished warning you about ghosts a few minutes ago.
 * One of the camel's pouches is labeled "maps" - sure enough, it's full of documents (your puzzle input) about how to navigate the desert.
 * At least, you're pretty sure that's what they are; one of the documents contains a list of left/right instructions, and the rest of the documents seem to describe some kind of network of labeled nodes.
 * It seems like you're meant to use the left/right instructions to navigate the network.
 * Perhaps if you have the camel follow the same instructions, you can escape the haunted wasteland!
 * After examining the maps for a bit, two nodes stick out: AAA and ZZZ.
 * You feel like AAA is where you are now, and you have to follow the left/right instructions until you reach ZZZ.
 * This format defines each node of the network individually.
 * For example:
 *
 * RL
 *
 * AAA = (BBB, CCC)
 * BBB = (DDD, EEE)
 * CCC = (ZZZ, GGG)
 * DDD = (DDD, DDD)
 * EEE = (EEE, EEE)
 * GGG = (GGG, GGG)
 * ZZZ = (ZZZ, ZZZ)
 *
 * Starting with AAA, you need to look up the next element based on the next left/right instruction in your input.
 * In this example, start with AAA and go right (R) by choosing the right element of AAA, CCC.
 * Then, L means to choose the left element of CCC, ZZZ.
 * By following the left/right instructions, you reach ZZZ in 2 steps.
 * Of course, you might not find ZZZ right away.
 * If you run out of left/right instructions, repeat the whole sequence of instructions as necessary: RL really means RLRLRLRLRLRLRLRL...and so on.
 * For example, here is a situation that takes 6 steps to reach ZZZ:
 *
 * LLR
 *
 * AAA = (BBB, BBB)
 * BBB = (AAA, ZZZ)
 * ZZZ = (ZZZ, ZZZ)
 *
 * Starting at AAA, follow the left/right instructions.
 * How many steps are required to reach ZZZ?
 *
 * Part 2:
 *
 * The sandstorm is upon you and you aren't any closer to escaping the wasteland.
 * You had the camel follow the instructions, but you've barely left your starting position.
 * It's going to take significantly more steps to escape!
 * What if the map isn't for people - what if the map is for ghosts?
 * Are ghosts even bound by the laws of spacetime?
 * Only one way to find out.
 * After examining the maps a bit longer, your attention is drawn to a curious fact: the number of nodes with names ending in A is equal to the number ending in Z!
 * If you were a ghost, you'd probably just start at every node that ends with A and follow all of the paths at the same time until they all simultaneously end up at nodes that end with Z.
 * For example:
 *
 * LR
 *
 * 11A = (11B, XXX)
 * 11B = (XXX, 11Z)
 * 11Z = (11B, XXX)
 * 22A = (22B, XXX)
 * 22B = (22C, 22C)
 * 22C = (22Z, 22Z)
 * 22Z = (22B, 22B)
 * XXX = (XXX, XXX)
 *
 * Here, there are two starting nodes, 11A and 22A (because they both end with A).
 * As you follow each left/right instruction, use that instruction to simultaneously navigate away from both nodes you're currently on.
 * Repeat this process until all of the nodes you're currently on end with Z.
 * (If only some of the nodes you're on end with Z, they act like any other node and you continue as normal.)
 * In this example, you would proceed as follows:
 *
 * - Step 0: You are at 11A and 22A.
 * - Step 1: You choose all of the left paths, leading you to 11B and 22B.
 * - Step 2: You choose all of the right paths, leading you to 11Z and 22C.
 * - Step 3: You choose all of the left paths, leading you to 11B and 22Z.
 * - Step 4: You choose all of the right paths, leading you to 11Z and 22B.
 * - Step 5: You choose all of the left paths, leading you to 11B and 22C.
 * - Step 6: You choose all of the right paths, leading you to 11Z and 22Z.
 *
 * So, in this example, you end up entirely on nodes that end in Z after 6 steps.
 * Simultaneously start on every node that ends with A.
 * How many steps does it take before you're only on nodes that end with Z?
 */
public class Day08 {

    private static final Pattern ROUTE_PATTERN = Pattern.compile("(\\w+) = \\((\\w+), (\\w+)\\)");

    public static void main(String... args) {

        List<Direction> directions = new ArrayList<>();
        Map<String, Route> nodes = new HashMap<>();

        try (var scanner = new Scanner(new File(Objects.requireNonNull(Day08.class.getResource("day08/instructions.txt")).getPath()))) {

            if (scanner.hasNextLine()) {
                var directionsLine = scanner.nextLine();
                directionsLine.chars()
                    .mapToObj(c -> String.valueOf((char) c))
                    .map(Direction::valueOf)
                    .forEach(directions::add);
            }

            scanner.nextLine();

            while (scanner.hasNextLine()) {
                var line = scanner.nextLine();
                var matcher = ROUTE_PATTERN.matcher(line);
                Preconditions.checkState(matcher.find());
                var route = new Route(matcher.group(2), matcher.group(3));
                nodes.put(matcher.group(1), route);
            }

        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }

        // Part 1

        System.out.println(
            calculateSteps(directions, nodes)
        );

        // Part 2

        System.out.println(
            calculateStepsSimultaneously(directions, nodes)
        );
    }

    private static int calculateSteps(List<Direction> directions, Map<String, Route> nodes) {

        var result = 0;
        var current = "AAA";

        while (!current.equals("ZZZ")) {
            var route = nodes.get(current);
            for (var direction : directions) {
                ++result;
                current = direction == Direction.L ? route.left() : route.right();
            }
        }

        return result;
    }

    private static BigInteger calculateStepsSimultaneously(List<Direction> directions, Map<String, Route> nodes) {

        var nodesEndingWithA = nodes.keySet().stream()
            .filter(node -> node.endsWith("A"))
            .toList();

        List<Integer> steps = new LinkedList<>();

        for (var node : nodesEndingWithA) {
            var numberOfSteps = calculateStepsForNode(node, directions, nodes);
            steps.add(numberOfSteps);
        }

        return calculateLeastCommonMultiple(steps);
    }

    public static BigInteger calculateLeastCommonMultiple(List<Integer> steps) {
        return steps.stream()
            .map(BigInteger::valueOf)
            .reduce(BigInteger.ONE, (a, b) -> a.multiply(b).divide(a.gcd(b)));
    }

    public static int calculateStepsForNode(String current, List<Direction> directions, Map<String, Route> nodes) {

        var count = 1;

        for (var directionPointer = 0;; directionPointer = (directionPointer + 1) % directions.size(), count++) {
            var route = nodes.get(current);
            current = directions.get(directionPointer) == Direction.L ? route.left() : route.right();
            if (current.endsWith("Z")) {
                return count;
            }
        }
    }

    private enum Direction {
        L,
        R
    }

    private record Route(String left, String right) { }
}