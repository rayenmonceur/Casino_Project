package tn.casino.casino_backend.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.casino.casino_backend.dto.DiceRequest;
import tn.casino.casino_backend.dto.GameResult;
import tn.casino.casino_backend.service.WalletService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class DiceService {

    // Retour théorique: 97% (même niveau que la roulette)
    private static final BigDecimal RTP_PERCENT = new BigDecimal("97");

    private final FairService fairService;
    private final WalletService walletService;

    public GameResult play(String username, DiceRequest req) {
        BigDecimal bet = req.getAmount();
        walletService.validateBet(bet);

        String dir = req.getDirection().toUpperCase();
        int target = req.getTarget();
        if (!dir.equals("UNDER") && !dir.equals("OVER"))
            throw new IllegalArgumentException("Direction invalide (UNDER ou OVER)");
        if (target < 2 || target > 98)
            throw new IllegalArgumentException("Le target doit être entre 2 et 98");

        // Tirage fair: nombre de 0 à 9999 (= 0.00 à 99.99)
        FairService.Draw draw = fairService.draw(username, 10000);
        int roll = draw.values()[0];

        boolean win;
        int chance; // chance de gagner en %
        if (dir.equals("UNDER")) {
            win = roll < target * 100;
            chance = target;
        } else {
            win = roll >= target * 100;
            chance = 100 - target;
        }

        // gain = mise x 97 / chance (arrondi vers le bas, 2 décimales)
        BigDecimal payout = win
                ? bet.multiply(RTP_PERCENT).divide(BigDecimal.valueOf(chance), 2, RoundingMode.DOWN)
                : BigDecimal.ZERO;

        BigDecimal balance = walletService.settle(username, "DICE", bet, payout);

        String outcome = String.format(Locale.ROOT, "%d.%02d %s %d", roll / 100, roll % 100, dir, target);
        return new GameResult("DICE", outcome, win, bet, payout, balance,
                draw.serverSeedHash(), draw.clientSeed(), draw.nonce());
    }
}