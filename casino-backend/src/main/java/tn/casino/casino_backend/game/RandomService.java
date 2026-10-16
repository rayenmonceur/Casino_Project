package tn.casino.casino_backend.game;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

// Générateur aléatoire sécurisé: ma ynejemch ay wa7ed yetwa99a3 el nata2ej
@Service
public class RandomService {

    private final SecureRandom random = new SecureRandom();

    // Yraja3 nombre men 0 l (bound - 1)
    public int nextInt(int bound) {
        return random.nextInt(bound);
    }
}