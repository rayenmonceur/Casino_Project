package tn.casino.casino_backend.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.casino.casino_backend.entity.CardRound;
import tn.casino.casino_backend.repository.CardRoundRepository;
import tn.casino.casino_backend.service.WalletService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PokerService {

    private static final String GAME = "POKER";

    // Paytable 9/6 (multiplicateur de la mise)
    private static final Map<String, Integer> PAYTABLE = Map.of(
            "ROYAL_FLUSH", 800, "STRAIGHT_FLUSH", 50, "FOUR_OF_A_KIND", 25,
            "FULL_HOUSE", 9, "FLUSH", 6, "STRAIGHT", 4,
            "THREE_OF_A_KIND", 3, "TWO_PAIR", 2, "JACKS_OR_BETTER", 1);

    private final FairService fairService;
    private final WalletService walletService;
    private final CardRoundRepository repo;

    public record State(Long id, List<String> hand, boolean active, String result, int multiplier,
            BigDecimal bet, BigDecimal payout, BigDecimal balance,
            String serverSeedHash, String clientSeed, long nonce) {
    }

    @Transactional(readOnly = true)
    public Optional<State> current(String username) {
        return repo.findByUsernameAndGameAndActiveTrue(username, GAME).map(this::state);
    }

    // Distribue 5 cartes
    @Transactional
    public State deal(String username, BigDecimal bet) {
        walletService.validateBet(bet);
        if (repo.findByUsernameAndGameAndActiveTrue(username, GAME).isPresent())
            throw new IllegalArgumentException("Une partie est déjà en cours");

        walletService.debit(username, GAME, bet);

        FairService.Draw draw = fairService.draw(username, Cards.SHUFFLE_BOUNDS);
        int[] deck = Cards.shuffle(draw.values());

        CardRound r = CardRound.builder()
                .username(username).game(GAME)
                .deck(Cards.join(deck)).nextIndex(5)
                .playerCards(Cards.join(Arrays.copyOfRange(deck, 0, 5)))
                .bet(bet).payout(BigDecimal.ZERO).active(true)
                .serverSeedHash(draw.serverSeedHash()).clientSeed(draw.clientSeed()).nonce(draw.nonce())
                .build();
        return state(repo.save(r));
    }

    // holds[i] = true -> on garde la carte i, sinon on la remplace
    @Transactional
    public State draw(String username, boolean[] holds) {
        if (holds == null || holds.length != 5)
            throw new IllegalArgumentException("holds doit contenir 5 valeurs");

        CardRound r = repo.findByUsernameAndGameAndActiveTrue(username, GAME)
                .orElseThrow(() -> new IllegalArgumentException("Aucune partie en cours"));

        List<Integer> hand = Cards.parse(r.getPlayerCards());
        List<Integer> deck = Cards.parse(r.getDeck());
        int next = r.getNextIndex();
        for (int i = 0; i < 5; i++) {
            if (!holds[i])
                hand.set(i, deck.get(next++));
        }
        r.setNextIndex(next);
        r.setPlayerCards(Cards.join(hand));

        String result = evaluate(hand);
        BigDecimal payout = r.getBet().multiply(BigDecimal.valueOf(PAYTABLE.getOrDefault(result, 0)))
                .setScale(2, RoundingMode.DOWN);
        if (payout.signum() > 0)
            walletService.credit(username, GAME, payout);

        r.setActive(false);
        r.setResult(result);
        r.setPayout(payout);
        return state(repo.save(r));
    }

    // Reconnaît la main (package-private pour les tests)
    static String evaluate(List<Integer> hand) {
        int[] rankCount = new int[13];
        int[] suitCount = new int[4];
        for (int c : hand) {
            rankCount[c % 13]++;
            suitCount[c / 13]++;
        }

        boolean flush = Arrays.stream(suitCount).anyMatch(n -> n == 5);
        int pairs = 0, three = 0, four = 0;
        for (int n : rankCount) {
            if (n == 2)
                pairs++;
            else if (n == 3)
                three++;
            else if (n == 4)
                four++;
        }

        boolean straight = false, royal = false;
        if (pairs == 0 && three == 0 && four == 0) { // 5 rangs différents
            int min = 13, max = -1;
            for (int r = 0; r < 13; r++)
                if (rankCount[r] > 0) {
                    min = Math.min(min, r);
                    max = Math.max(max, r);
                }
            straight = (max - min == 4); // inclut A-2-3-4-5
            boolean broadway = rankCount[0] > 0 && rankCount[9] > 0 && rankCount[10] > 0
                    && rankCount[11] > 0 && rankCount[12] > 0; // 10-J-Q-K-A
            if (broadway) {
                straight = true;
                royal = flush;
            }
        }

        if (royal)
            return "ROYAL_FLUSH";
        if (straight && flush)
            return "STRAIGHT_FLUSH";
        if (four == 1)
            return "FOUR_OF_A_KIND";
        if (three == 1 && pairs == 1)
            return "FULL_HOUSE";
        if (flush)
            return "FLUSH";
        if (straight)
            return "STRAIGHT";
        if (three == 1)
            return "THREE_OF_A_KIND";
        if (pairs == 2)
            return "TWO_PAIR";
        if (pairs == 1) {
            for (int r : new int[] { 0, 10, 11, 12 }) // A, J, Q, K
                if (rankCount[r] == 2)
                    return "JACKS_OR_BETTER";
        }
        return "NONE";
    }

    private State state(CardRound r) {
        String result = r.getResult();
        return new State(r.getId(), Cards.labels(Cards.parse(r.getPlayerCards())), r.isActive(),
                result, result == null ? 0 : PAYTABLE.getOrDefault(result, 0),
                r.getBet(), r.getPayout(), walletService.getBalance(r.getUsername()),
                r.getServerSeedHash(), r.getClientSeed(), r.getNonce());
    }
}