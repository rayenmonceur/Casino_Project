package tn.casino.casino_backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.casino.casino_backend.dto.GameResult;
import tn.casino.casino_backend.dto.RouletteRequest;
import tn.casino.casino_backend.dto.SlotsRequest;
import tn.casino.casino_backend.game.RouletteService;
import tn.casino.casino_backend.game.SlotsService;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final RouletteService rouletteService;
    private final SlotsService slotsService;

    // auth.getName() = esm el user elli 3andou token s7i7
    @PostMapping("/roulette")
    public ResponseEntity<GameResult> roulette(@Valid @RequestBody RouletteRequest req, Authentication auth) {
        return ResponseEntity.ok(rouletteService.play(auth.getName(), req));
    }

    @PostMapping("/slots")
    public ResponseEntity<GameResult> slots(@Valid @RequestBody SlotsRequest req, Authentication auth) {
        return ResponseEntity.ok(slotsService.play(auth.getName(), req));
    }
}