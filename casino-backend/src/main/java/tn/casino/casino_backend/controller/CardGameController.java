package tn.casino.casino_backend.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.casino.casino_backend.game.BlackjackService;
import tn.casino.casino_backend.game.PokerService;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class CardGameController {

    private final BlackjackService blackjack;
    private final PokerService poker;

    public record BetRequest(@NotNull BigDecimal amount) {
    }

    public record HoldRequest(@NotNull boolean[] holds) {
    }

    // ---------- Blackjack ----------
    @GetMapping("/blackjack/current")
    public ResponseEntity<BlackjackService.State> bjCurrent(Authentication auth) {
        return blackjack.current(auth.getName()).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/blackjack/start")
    public BlackjackService.State bjStart(@Valid @RequestBody BetRequest req, Authentication auth) {
        return blackjack.start(auth.getName(), req.amount());
    }

    @PostMapping("/blackjack/hit")
    public BlackjackService.State bjHit(Authentication auth) {
        return blackjack.hit(auth.getName());
    }

    @PostMapping("/blackjack/stand")
    public BlackjackService.State bjStand(Authentication auth) {
        return blackjack.stand(auth.getName());
    }

    @PostMapping("/blackjack/double")
    public BlackjackService.State bjDouble(Authentication auth) {
        return blackjack.doubleDown(auth.getName());
    }

    // ---------- Poker ----------
    @GetMapping("/poker/current")
    public ResponseEntity<PokerService.State> pokerCurrent(Authentication auth) {
        return poker.current(auth.getName()).map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/poker/deal")
    public PokerService.State pokerDeal(@Valid @RequestBody BetRequest req, Authentication auth) {
        return poker.deal(auth.getName(), req.amount());
    }

    @PostMapping("/poker/draw")
    public PokerService.State pokerDraw(@Valid @RequestBody HoldRequest req, Authentication auth) {
        return poker.draw(auth.getName(), req.holds());
    }
}