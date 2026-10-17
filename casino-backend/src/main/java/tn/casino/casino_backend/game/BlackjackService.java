package tn.casino.casino_backend.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.casino.casino_backend.entity.CardRound;
import tn.casino.casino_backend.repository.CardRoundRepository;
import tn.casino.casino_backend.service.WalletService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlackjackService {

    private static final String GAME = "BLACKJACK";

    private final FairService fairService;
    private final WalletService walletService;
    private final CardRoundRepository repo;

    // Ce que le client reçoit (jamais le paquet, ni la carte cachée du croupier)
    public record State(Long id, List<String> player, List<String> dealer,
            int playerTotal, Integer dealerTotal,
            boolean active, boolean canDouble, String result,
            BigDecimal bet, BigDecimal payout, BigDecimal balance,
            String serverSeedHash, String clientSeed, long nonce) {
    }

    @Transactional(readOnly = true)
    public Optional<State> current(String username) {
        return repo.findByUsernameAndGameAndActiveTrue(username, GAME).map(r -> state(r));
    }

    @Transactional
    public State start(String username, BigDecimal bet) {
        walletService.validateBet(bet);
        if (repo.findByUsernameAndGameAndActiveTrue(username, GAME).isPresent())
            throw new IllegalArgumentException("Une partie est déjà en cours");

        walletService.debit(username, GAME, bet);

        // Mélange fair: 1 nonce par partie
        FairService.Draw draw = fairService.draw(username, Cards.SHUFFLE_BOUNDS);
        int[] deck = Cards.shuffle(draw.values());

        // Distribution: joueur, croupier, joueur, croupier
        CardRound r = CardRound.builder()
                .username(username).game(GAME)
                .deck(Cards.join(deck)).nextIndex(4)
                .playerCards(deck[0] + "," + deck[2])
                .dealerCards(deck[1] + "," + deck[3])
                .bet(bet).payout(BigDecimal.ZERO).active(true)
                .serverSeedHash(draw.serverSeedHash()).clientSeed(draw.clientSeed()).nonce(draw.nonce())
                .build();

        // Blackjack naturel (joueur ou croupier): la partie se termine direct
        boolean playerBj = Cards.blackjackTotal(Cards.parse(r.getPlayerCards())) == 21;
        boolean dealerBj = Cards.blackjackTotal(Cards.parse(r.getDealerCards())) == 21;
        if (playerBj || dealerBj)
            finish(r, playerBj && dealerBj ? "PUSH" : playerBj ? "BLACKJACK" : "LOSE");

        return state(repo.save(r));
    }

    @Transactional
    public State hit(String username) {
        CardRound r = active(username);
        List<Integer> player = Cards.parse(r.getPlayerCards());
        player.add(nextCard(r));
        r.setPlayerCards(Cards.join(player));

        int total = Cards.blackjackTotal(player);
        if (total > 21)
            finish(r, "LOSE"); // bust: le croupier ne joue pas
        else if (total == 21)
            dealerPlays(r); // 21: on s'arrête automatiquement
        return state(repo.save(r));
    }

    @Transactional
    public State stand(String username) {
        CardRound r = active(username);
        dealerPlays(r);
        return state(repo.save(r));
    }

    @Transactional
    public State doubleDown(String username) {
        CardRound r = active(username);
        List<Integer> player = Cards.parse(r.getPlayerCards());
        if (player.size() != 2)
            throw new IllegalArgumentException("Double possible seulement avec 2 cartes");

        walletService.debit(username, GAME, r.getBet()); // 2ème mise
        r.setBet(r.getBet().multiply(BigDecimal.valueOf(2)));

        player.add(nextCard(r)); // une seule carte
        r.setPlayerCards(Cards.join(player));

        if (Cards.blackjackTotal(player) > 21)
            finish(r, "LOSE");
        else
            dealerPlays(r);
        return state(repo.save(r));
    }

    // Le croupier tire jusqu'à 17, puis on compare
    private void dealerPlays(CardRound r) {
        List<Integer> dealer = Cards.parse(r.getDealerCards());
        while (Cards.blackjackTotal(dealer) < 17)
            dealer.add(nextCard(r));
        r.setDealerCards(Cards.join(dealer));

        int d = Cards.blackjackTotal(dealer);
        int p = Cards.blackjackTotal(Cards.parse(r.getPlayerCards()));
        finish(r, (d > 21 || p > d) ? "WIN" : p == d ? "PUSH" : "LOSE");
    }

    // Calcule le gain, crédite le wallet, clôture la partie
    private void finish(CardRound r, String result) {
        BigDecimal payout = switch (result) {
            case "BLACKJACK" -> r.getBet().multiply(new BigDecimal("2.5")); // 3:2
            case "WIN" -> r.getBet().multiply(BigDecimal.valueOf(2));
            case "PUSH" -> r.getBet(); // mise rendue
            default -> BigDecimal.ZERO;
        };
        payout = payout.setScale(2, RoundingMode.DOWN);
        if (payout.signum() > 0)
            walletService.credit(r.getUsername(), GAME, payout);

        r.setActive(false);
        r.setResult(result);
        r.setPayout(payout);
    }

    private int nextCard(CardRound r) {
        int card = Cards.parse(r.getDeck()).get(r.getNextIndex());
        r.setNextIndex(r.getNextIndex() + 1);
        return card;
    }

    private CardRound active(String username) {
        return repo.findByUsernameAndGameAndActiveTrue(username, GAME)
                .orElseThrow(() -> new IllegalArgumentException("Aucune partie en cours"));
    }

    private State state(CardRound r) {
        List<Integer> player = Cards.parse(r.getPlayerCards());
        List<Integer> dealer = Cards.parse(r.getDealerCards());
        boolean active = r.isActive();

        // Tant que la partie est active, la 2ème carte du croupier reste cachée ("??")
        List<String> dealerView = active
                ? List.of(Cards.label(dealer.get(0)), "??")
                : Cards.labels(dealer);

        return new State(r.getId(), Cards.labels(player), dealerView,
                Cards.blackjackTotal(player), active ? null : Cards.blackjackTotal(dealer),
                active, active && player.size() == 2, r.getResult(),
                r.getBet(), r.getPayout(), walletService.getBalance(r.getUsername()),
                r.getServerSeedHash(), r.getClientSeed(), r.getNonce());
    }
}