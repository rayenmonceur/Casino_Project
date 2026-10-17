package tn.casino.casino_backend.game;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FairServiceTest {

    // Nafs les entrées = dima nafs el natija (c'est la base du provably fair)
    @Test
    void computeIsDeterministic() {
        int a = FairService.compute("secret", "client", 5, 0, 37);
        int b = FairService.compute("secret", "client", 5, 0, 37);
        assertEquals(a, b);
    }

    // El natija dima fi [0, bound[
    @Test
    void computeStaysInRange() {
        for (int nonce = 0; nonce < 10_000; nonce++) {
            int v = FairService.compute("secret", "client", nonce, 0, 37);
            assertTrue(v >= 0 && v < 37, "hors limites: " + v);
        }
    }

    // Ken nbaddlou nonce wela clientSeed, el natija tetbaddel (3la el 9olla)
    @Test
    void differentInputsGiveDifferentStreams() {
        int same = 0;
        for (int nonce = 0; nonce < 1000; nonce++) {
            if (FairService.compute("secret", "A", nonce, 0, 37) == FairService.compute("secret", "B", nonce, 0, 37))
                same++;
        }
        assertTrue(same < 100, "trop de collisions: " + same); // ~27 attendues sur 1000
    }

    // Vecteur de test connu de SHA-256
    @Test
    void sha256KnownVector() {
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                FairService.sha256("abc"));
    }
}