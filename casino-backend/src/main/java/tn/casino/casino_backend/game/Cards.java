package tn.casino.casino_backend.game;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

// Carte = nombre de 0 à 51. rang = c % 13 (0=A, 1=2 ... 9=10, 10=J, 11=Q, 12=K), couleur = c / 13
public final class Cards {

    private Cards() {
    }

    private static final String[] RANKS = { "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K" };
    private static final String[] SUITS = { "♠", "♥", "♦", "♣" };

    // Fisher-Yates: 51 tirages avec bornes 52, 51, ... 2
    public static final int[] SHUFFLE_BOUNDS = IntStream.range(0, 51).map(i -> 52 - i).toArray();

    // Mélange le paquet à partir des 51 valeurs fair
    public static int[] shuffle(int[] values) {
        int[] deck = IntStream.range(0, 52).toArray();
        for (int i = 0; i < 51; i++) {
            int j = i + values[i];
            int tmp = deck[i];
            deck[i] = deck[j];
            deck[j] = tmp;
        }
        return deck;
    }

    public static String label(int card) {
        return RANKS[card % 13] + SUITS[card / 13];
    }

    public static List<String> labels(List<Integer> cards) {
        return cards.stream().map(Cards::label).toList();
    }

    public static List<Integer> parse(String csv) {
        if (csv == null || csv.isBlank())
            return new ArrayList<>();
        return Arrays.stream(csv.split(",")).map(Integer::parseInt)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static String join(List<Integer> cards) {
        return cards.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    public static String join(int[] cards) {
        return Arrays.stream(cards).mapToObj(String::valueOf).collect(Collectors.joining(","));
    }

    // Total blackjack: l'as vaut 11, ou 1 si on dépasse 21
    public static int blackjackTotal(List<Integer> cards) {
        int sum = 0, aces = 0;
        for (int c : cards) {
            int rank = c % 13;
            if (rank == 0) {
                aces++;
                sum += 11;
            } else
                sum += Math.min(rank + 1, 10);
        }
        while (sum > 21 && aces > 0) {
            sum -= 10;
            aces--;
        }
        return sum;
    }
}