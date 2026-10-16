package tn.casino.casino_backend.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.casino.casino_backend.dto.GameResult;
import tn.casino.casino_backend.dto.SlotsRequest;
import tn.casino.casino_backend.service.WalletService;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SlotsService {

    // 5 symboles, kol wa7ed 3andou nafs el 7dhdh (1/5) fi kol rouleau
    private static final String[] SYMBOLS = {"CHERRY", "LEMON", "BELL", "STAR", "SEVEN"};
    // Gain ken yjou 3 symboles kif kif (dhroub el mise)
    private static final int[] MULTIPLIERS = {10, 15, 20, 30, 45};

    private final RandomService randomService;
    private final WalletService walletService;

    public GameResult play(String username, SlotsRequest req) {
        BigDecimal bet = req.getAmount();
        walletService.validateBet(bet);

        // Nlaffou 3 rouleaux, kol wa7ed independant
        int r1 = randomService.nextInt(SYMBOLS.length);
        int r2 = randomService.nextInt(SYMBOLS.length);
        int r3 = randomService.nextInt(SYMBOLS.length);

        boolean win = (r1 == r2 && r2 == r3);
        BigDecimal payout = win ? bet.multiply(BigDecimal.valueOf(MULTIPLIERS[r1])) : BigDecimal.ZERO;

        BigDecimal balance = walletService.settle(username, "SLOTS", bet, payout);
        String outcome = SYMBOLS[r1] + " | " + SYMBOLS[r2] + " | " + SYMBOLS[r3];

        return new GameResult("SLOTS", outcome, win, bet, payout, balance);
    }
}