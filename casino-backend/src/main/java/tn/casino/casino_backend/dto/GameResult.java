package tn.casino.casino_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

// El jaweb elli yetba3eth lel Angular ba3d kol le3ba
@Data
@AllArgsConstructor
public class GameResult {
    private String game;
    private String outcome;     // mathalan "17 BLACK" wela "BELL | BELL | BELL"
    private boolean win;
    private BigDecimal bet;
    private BigDecimal payout;  // el montant elli yerja3 lel joueur (0 ken khsser)
    private BigDecimal balance; // el solde el jdid
}