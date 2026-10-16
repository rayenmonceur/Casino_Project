package tn.casino.casino_backend.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.casino.casino_backend.dto.GameResult;
import tn.casino.casino_backend.dto.RouletteRequest;
import tn.casino.casino_backend.service.WalletService;

import java.math.BigDecimal;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RouletteService {

    // Les numéros rouges fel roulette européenne
    private static final Set<Integer> RED = Set.of(
            1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36);

    private final RandomService randomService;
    private final WalletService walletService;

    public GameResult play(String username, RouletteRequest req) {
        BigDecimal bet = req.getAmount();
        walletService.validateBet(bet);

        String type = req.getBetType().toUpperCase();
        if (type.equals("NUMBER") && (req.getNumber() == null || req.getNumber() < 0 || req.getNumber() > 36))
            throw new IllegalArgumentException("Numéro invalide (0 à 36)");

        // Nlaffou el roulette: nombre men 0 l 36 (37 possibilité)
        int n = randomService.nextInt(37);
        String color = (n == 0) ? "GREEN" : RED.contains(n) ? "RED" : "BLACK";

        // Multiplicateur: 0 = khsarna, sinon yerja3 el gain (mise incluse)
        int multiplier = switch (type) {
            case "NUMBER" -> (n == req.getNumber()) ? 36 : 0;           // 35:1
            case "RED"    -> color.equals("RED") ? 2 : 0;               // 1:1
            case "BLACK"  -> color.equals("BLACK") ? 2 : 0;
            case "EVEN"   -> (n != 0 && n % 2 == 0) ? 2 : 0;
            case "ODD"    -> (n % 2 == 1) ? 2 : 0;
            default -> throw new IllegalArgumentException("Type de pari invalide");
        };

        BigDecimal payout = bet.multiply(BigDecimal.valueOf(multiplier));
        BigDecimal balance = walletService.settle(username, "ROULETTE", bet, payout);

        return new GameResult("ROULETTE", n + " " + color, multiplier > 0, bet, payout, balance);
    }
}