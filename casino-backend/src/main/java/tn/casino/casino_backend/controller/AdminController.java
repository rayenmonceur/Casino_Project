package tn.casino.casino_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.casino.casino_backend.entity.*;
import tn.casino.casino_backend.repository.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

// Kol el routes hedhi ADMIN barka (SecurityConfig: /api/admin/**)
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final GameTransactionRepository transactionRepository;
    private final SecurityEventRepository eventRepository;
    private final UserRepository userRepository;

    // Dernières transactions: émetteur, récepteur, montant
    @GetMapping("/transactions")
    public List<GameTransaction> transactions() {
        return transactionRepository.findTop50ByOrderByCreatedAtDesc();
    }

    // Les alertes sécurité (attaques)
    @GetMapping("/security-events")
    public List<SecurityEvent> events() {
        return eventRepository.findTop100ByOrderByCreatedAtDesc();
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {
        BigDecimal bets = transactionRepository.sumByType(TransactionType.BET);
        BigDecimal wins = transactionRepository.sumByType(TransactionType.WIN);
        return Map.of(
                "users", userRepository.count(),
                "totalBets", bets,
                "totalWins", wins,
                "casinoProfit", bets.subtract(wins));
    }
}