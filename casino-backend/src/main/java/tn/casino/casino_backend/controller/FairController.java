package tn.casino.casino_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.casino.casino_backend.entity.FairSeed;
import tn.casino.casino_backend.game.FairService;

import java.util.Map;

@RestController
@RequestMapping("/api/fair")
@RequiredArgsConstructor
public class FairController {

    private final FairService fairService;

    public record ClientSeedRequest(String clientSeed) {
    }

    public record VerifyRequest(String serverSeed, String serverSeedHash,
            String clientSeed, long nonce, int[] bounds) {
    }

    // Hash mte3 el seed el actif (el secret ma yetb3athch)
    @GetMapping("/current")
    public Map<String, Object> current(Authentication auth) {
        return info(fairService.current(auth.getName()));
    }

    @PostMapping("/client-seed")
    public Map<String, Object> clientSeed(@RequestBody ClientSeedRequest req, Authentication auth) {
        return info(fairService.setClientSeed(auth.getName(), req.clientSeed()));
    }

    // Rotation: yekchef el secret el 9dim
    @PostMapping("/rotate")
    public FairService.Reveal rotate(Authentication auth) {
        return fairService.rotate(auth.getName());
    }

    // Outil de vérification: y3awed el 7sab bel formule
    @PostMapping("/verify")
    public Map<String, Object> verify(@RequestBody VerifyRequest req) {
        if (req.bounds() == null || req.bounds().length == 0 || req.bounds().length > 5)
            throw new IllegalArgumentException("bounds invalide");
        for (int b : req.bounds())
            if (b < 1 || b > 1000)
                throw new IllegalArgumentException("bounds invalide");

        int[] values = new int[req.bounds().length];
        for (int i = 0; i < values.length; i++)
            values[i] = FairService.compute(req.serverSeed(), req.clientSeed(), req.nonce(), i, req.bounds()[i]);

        boolean hashMatches = FairService.sha256(req.serverSeed()).equalsIgnoreCase(req.serverSeedHash());
        return Map.of("hashMatches", hashMatches, "values", values);
    }

    private Map<String, Object> info(FairSeed s) {
        return Map.of("serverSeedHash", s.getServerSeedHash(),
                "clientSeed", s.getClientSeed(), "nonce", s.getNonce());
    }
}