package tn.casino.casino_backend.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Simulation: on rejoue le vrai générateur (HMAC) 1 000 000 de fois
class RtpSimulationTest {

    private static final int N = 1_000_000;
    private static final int[] MULTIPLIERS = { 10, 15, 20, 30, 45 }; // comme SlotsService

    @Test
    void rouletteRedBetRtpIsAbout97percent() {
        double paid = 0;
        for (int i = 0; i < N; i++) {
            int n = FairService.compute("seed-test", "client-test", i, 0, 37);
            boolean red = java.util.Set.of(1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36)
                    .contains(n);
            if (red)
                paid += 2; // mise 1 -> retour 2
        }
        double rtp = paid / N;
        System.out.printf("RTP roulette (rouge): %.4f (théorique 0.9730)%n", rtp);
        assertEquals(0.973, rtp, 0.01);
    }

    @Test
    void rouletteNumbersAreUniform() {
        int[] count = new int[37];
        int draws = 370_000;
        for (int i = 0; i < draws; i++)
            count[FairService.compute("seed-test", "client-test", i, 0, 37)]++;
        int expected = draws / 37; // 10 000
        for (int c : count)
            assertTrue(Math.abs(c - expected) < expected * 0.1, "numéro biaisé: " + c);
    }

    @Test
    void slotsRtpIsAbout96percent() {
        double paid = 0;
        for (int i = 0; i < N; i++) {
            int r1 = FairService.compute("seed-test", "client-test", i, 0, 5);
            int r2 = FairService.compute("seed-test", "client-test", i, 1, 5);
            int r3 = FairService.compute("seed-test", "client-test", i, 2, 5);
            if (r1 == r2 && r2 == r3)
                paid += MULTIPLIERS[r1];
        }
        double rtp = paid / N;
        System.out.printf("RTP slots: %.4f (théorique 0.9600)%n", rtp);
        assertEquals(0.96, rtp, 0.03);
    }

    @Test
    void diceUnder50RtpIsAbout97percent() {
        double paid = 0;
        for (int i = 0; i < N; i++) {
            int roll = FairService.compute("seed-test", "client-test", i, 0, 10000);
            if (roll < 5000)
                paid += 97.0 / 50; // mise 1, chance 50%
        }
        double rtp = paid / N;
        System.out.printf("RTP dice (under 50): %.4f (théorique 0.9700)%n", rtp);
        assertEquals(0.97, rtp, 0.01);
    }
}