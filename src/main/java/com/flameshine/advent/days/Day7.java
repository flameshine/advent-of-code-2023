package com.flameshine.advent.days;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.flameshine.advent.util.IOUtils;
import com.flameshine.advent.util.ParsingUtils;

/**
 * Day 7: Camel Cards
 *
 * Part 1:
 *
 * Your all-expenses-paid trip turns out to be a one-way, five-minute ride in an airship (at least it's a cool airship!).
 * It drops you off at the edge of a vast desert and descends back to Island Island.
 * "Did you bring the parts?"
 * You turn around to see an Elf completely covered in white clothing, wearing goggles, and riding a large camel.
 * "Did you bring the parts?" she asks again, louder this time.
 * You aren't sure what parts she's looking for; you're here to figure out why the sand stopped.
 * "The parts! For the sand, yes! Come with me; I will show you."
 * She beckons you onto the camel.
 * After riding a bit across the sands of Desert Island, you can see what look like very large rocks covering half of the horizon.
 * The Elf explains that the rocks are all along the part of Desert Island that is directly above Island Island, making it hard to even get there.
 * Normally, they use big machines to move the rocks and filter the sand, but the machines have broken down because Desert Island recently stopped receiving the parts they need to fix the machines.
 * You've already assumed it'll be your job to figure out why the parts stopped when she asks if you can help.
 * You agree automatically.
 * Because the journey will take a few days, she offers to teach you the game of Camel Cards.
 * Camel Cards is sort of similar to poker except it's designed to be easier to play while riding a camel.
 * In Camel Cards, you get a list of hands, and your goal is to order them based on the strength of each hand.
 * A hand consists of five cards labeled one of A, K, Q, J, T, 9, 8, 7, 6, 5, 4, 3, or 2.
 * The relative strength of each card follows this order, where A is the highest and 2 is the lowest.
 * Every hand is exactly one type.
 * From strongest to weakest, they are:
 *
 * - Five of a kind, where all five cards have the same label: AAAAA
 * - Four of a kind, where four cards have the same label and one card has a different label: AA8AA
 * - Full house, where three cards have the same label, and the remaining two cards share a different label: 23332
 * - Three of a kind, where three cards have the same label, and the remaining two cards are each different from any other card in the hand: TTT98
 * - Two pair, where two cards share one label, two other cards share a second label, and the remaining card has a third label: 23432
 * - One pair, where two cards share one label, and the other three cards have a different label from the pair and each other: A23A4
 * - High card, where all cards' labels are distinct: 23456
 * - Hands are primarily ordered based on type; for example, every full house is stronger than any three of a kind.
 *
 * If two hands have the same type, a second ordering rule takes effect.
 * Start by comparing the first card in each hand.
 * If these cards are different, the hand with the stronger first card is considered stronger.
 * If the first card in each hand have the same label, however, then move on to considering the second card in each hand.
 * If they differ, the hand with the higher second card wins; otherwise, continue with the third card in each hand, then the fourth, then the fifth.
 * So, 33332 and 2AAAA are both four of a kind hands, but 33332 is stronger because its first card is stronger.
 * Similarly, 77888 and 77788 are both a full house, but 77888 is stronger because its third card is stronger (and both hands have the same first and second card).
 * To play Camel Cards, you are given a list of hands and their corresponding bid (your puzzle input).
 * For example:
 *
 * 32T3K 765
 * T55J5 684
 * KK677 28
 * KTJJT 220
 * QQQJA 483
 *
 * This example shows five hands; each hand is followed by its bid amount.
 * Each hand wins an amount equal to its bid multiplied by its rank, where the weakest hand gets rank 1, the second-weakest hand gets rank 2, and so on up to the strongest hand.
 * Because there are five hands in this example, the strongest hand will have rank 5 and its bid will be multiplied by 5.
 *
 * So, the first step is to put the hands in order of strength:
 *
 * - 32T3K is the only one pair and the other hands are all a stronger type, so it gets rank 1.
 * - KK677 and KTJJT are both two pair.
 *   Their first cards both have the same label, but the second card of KK677 is stronger (K vs T), so KTJJT gets rank 2 and KK677 gets rank 3.
 * - T55J5 and QQQJA are both three of a kind.
 *   QQQJA has a stronger first card, so it gets rank 5 and T55J5 gets rank 4.
 *
 * Now, you can determine the total winnings of this set of hands by adding up the result of multiplying each hand's bid with its rank (765 * 1 + 220 * 2 + 28 * 3 + 684 * 4 + 483 * 5).
 * So the total winnings in this example are 6440.
 * Find the rank of every hand in your set.
 * What are the total winnings?
 *
 * Part 2:
 *
 * To make things a little more interesting, the Elf introduces one additional rule.
 * Now, J cards are jokers - wildcards that can act like whatever card would make the hand the strongest type possible.
 * To balance this, J cards are now the weakest individual cards, weaker even than 2.
 * The other cards stay in the same order: A, K, Q, T, 9, 8, 7, 6, 5, 4, 3, 2, J.
 * J cards can pretend to be whatever card is best for the purpose of determining hand type; for example, QJJQ2 is now considered four of a kind.
 * However, for the purpose of breaking ties between two hands of the same type, J is always treated as J, not the card it's pretending to be: JKKK2 is weaker than QQQQ2 because J is weaker than Q.
 * Now, the above example goes very differently:
 *
 * 32T3K 765
 * T55J5 684
 * KK677 28
 * KTJJT 220
 * QQQJA 483
 *
 * - 32T3K is still the only one pair; it doesn't contain any jokers, so its strength doesn't increase.
 * - KK677 is now the only two pair, making it the second-weakest hand.
 * - T55J5, KTJJT, and QQQJA are now all four of a kind! T55J5 gets rank 3, QQQJA gets rank 4, and KTJJT gets rank 5.
 * With the new joker rule, the total winnings in this example are 5905.
 *
 * Using the new joker rule, find the rank of every hand in your set. What are the new total winnings?
 */
public class Day7 {

    public static void main(String... args) {

        var lines = IOUtils.readAllLinesFrom(Day7.class.getResource("day7/hands.txt"));

        // Part 1

        System.out.println(
            calculateTotalWinnings(lines)
        );

        // Part 2

        System.out.println(
            calculateTotalWinningsConsideringJokers(lines)
        );
    }

    private static int calculateTotalWinnings(List<String> lines) {
        var hands = buildHands(lines);
        return getTotalWinnings(hands);
    }

    private static int calculateTotalWinningsConsideringJokers(List<String> lines) {

        var hands = buildHands(lines).stream()
            .map(Hand::new)
            .toList();

        return getTotalWinnings(hands);
    }

    private static List<Hand> buildHands(List<String> lines) {

        List<Hand> resultBuilder = new ArrayList<>();

        for (var line : lines) {
            var parts = line.split("\\s");
            var bid = ParsingUtils.parseInt(parts[1]);
            var hand = new Hand(parts[0], bid);
            resultBuilder.add(hand);
        }

        return Collections.unmodifiableList(resultBuilder);
    }

    private static int getTotalWinnings(List<Hand> hands) {

        var handsSortedByCombinationType = hands.stream()
            .sorted(Hand::compareTo)
            .toList();

        var bidToMultiplier = handsSortedByCombinationType.stream()
            .collect(Collectors.toUnmodifiableMap(Hand::bid, h -> handsSortedByCombinationType.indexOf(h) + 1));

        return bidToMultiplier.entrySet().stream()
            .mapToInt(e -> e.getKey() * e.getValue())
            .sum();
    }

    private static class Hand implements Comparable<Hand> {

        private static final Map<Character, Integer> CARD_BY_STRENGTH = IntStream.range(0, 13)
            .boxed()
            .collect(Collectors.toMap("AKQJT98765432"::charAt, i -> 13 - i));

        private final String combination;
        private final int bid;
        private final Type type;
        private final boolean considerJokers;

        private int numberOfJokers;

        public Hand(String combination, int bid) {
            this.combination = combination;
            this.bid = bid;
            this.considerJokers = false;

            var characterToCount = combination.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            this.type = determineType(characterToCount);
        }

        public Hand(Hand hand) {
            this.combination = hand.combination();
            this.bid = hand.bid();
            this.considerJokers = true;

            CARD_BY_STRENGTH.put('J', 0);

            var characterToCount = combination.chars()
                .mapToObj(c -> (char) c)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            this.numberOfJokers = characterToCount.getOrDefault('J', 0L).intValue();

            characterToCount.remove('J');

            this.type = determineType(characterToCount);
        }

        @Override
        public int compareTo(Hand other) {

            var otherType = other.type();

            if (type != otherType) {
                return type.strength() - otherType.strength();
            }

            for (var i = 0; i < combination.length(); i++) {
                var thisCombinationCharStrength = CARD_BY_STRENGTH.get(combination.charAt(i));
                var otherCombinationCharStrength = CARD_BY_STRENGTH.get(other.combination().charAt(i));
                if (!Objects.equals(thisCombinationCharStrength, otherCombinationCharStrength)) {
                    return thisCombinationCharStrength - otherCombinationCharStrength;
                }
            }

            throw new IllegalStateException();
        }

        public String combination() {
            return combination;
        }

        public int bid() {
            return bid;
        }

        public Type type() {
            return type;
        }

        private Type determineType(Map<Character, Long> characterToCount) {

            var sortedCounts = characterToCount.values().stream()
                .mapToInt(Long::intValue)
                .sorted()
                .boxed()
                .toList();

            var max = sortedCounts.isEmpty() ? 0 : sortedCounts.getLast();

            if (considerJokers) {
                max += numberOfJokers;
            }

            return switch (max) {
                case 5 -> Type.FIVE_OF_A_KIND;
                case 4 -> Type.FOUR_OF_A_KIND;
                case 3 -> {
                    var secondMax = sortedCounts.get(sortedCounts.size() - 2);
                    yield secondMax == 2 ? Type.FULL_HOUSE : Type.THREE_OF_A_KIND;
                }
                case 2 -> {

                    var pairsCount = characterToCount.values().stream()
                        .filter(count -> count == 2)
                        .count();

                    yield pairsCount == 2 ? Type.TWO_PAIR : Type.ONE_PAIR;
                }
                default -> Type.HIGH_CARD;
            };
        }
    }

    private enum Type {

        FIVE_OF_A_KIND(7),
        FOUR_OF_A_KIND(6),
        FULL_HOUSE(5),
        THREE_OF_A_KIND(4),
        TWO_PAIR(3),
        ONE_PAIR(2),
        HIGH_CARD(1);

        private final int strength;

        Type(int strength) {
            this.strength = strength;
        }

        public int strength() {
            return strength;
        }
    }
}