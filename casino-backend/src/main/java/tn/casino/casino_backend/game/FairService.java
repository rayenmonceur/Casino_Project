package tn.casino.casino_backend.game;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.casino.casino_backend.entity.FairSeed;
import tn.casino.casino_backend.repository.FairSeedRepository;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
public class FairService {

    // Natija mte3 tirage + les preuves elli yetb3athou lel joueur
    public record Draw(int[] values, String serverSeedHash, String clientSeed, long nonce) {
    }

    // El secret elli yetkchef ba3d rotation
    public record Reveal(String serverSeed, String serverSeedHash, String clientSeed, long lastNonce) {
    }

    private final FairSeedRepository repository;
    private final SecureRandom random = new SecureRandom();

    // Seed el actif (yetsna3 wa7dou awwel marra)
    @Transactional
    public FairSeed current(String username) {
        return repository.findByUsernameAndActiveTrue(username)
                .orElseGet(() -> create(username, randomHex(8)));
    }

    // Yetsna3 tirage: bounds = [37] lel roulette, [5,5,5] lel slots
    @Transactional
    public Draw draw(String username, int... bounds) {
        FairSeed seed = current(username);
        long nonce = seed.getNonce();

        int[] values = new int[bounds.length];
        for (int i = 0; i < bounds.length; i++) {
            values[i] = compute(seed.getServerSeed(), seed.getClientSeed(), nonce, i, bounds[i]);
        }

        seed.setNonce(nonce + 1);
        repository.save(seed);
        return new Draw(values, seed.getServerSeedHash(), seed.getClientSeed(), nonce);
    }

    @Transactional
    public FairSeed setClientSeed(String username, String clientSeed) {
        if (clientSeed == null || clientSeed.isBlank() || clientSeed.length() > 64)
            throw new IllegalArgumentException("Client seed invalide (1 à 64 caractères)");
        FairSeed seed = current(username);
        seed.setClientSeed(clientSeed.trim());
        return repository.save(seed);
    }

    // Rotation: nkechfou el secret el 9dim w nasn3ou wa7ed jdid
    @Transactional
    public Reveal rotate(String username) {
        FairSeed old = current(username);
        old.setActive(false);
        repository.save(old);
        create(username, old.getClientSeed());
        return new Reveal(old.getServerSeed(), old.getServerSeedHash(), old.getClientSeed(), old.getNonce() - 1);
    }

    // Nafs el formule elli yest3mlouha el joueur bech yetaked
    public static int compute(String serverSeed, String clientSeed, long nonce, int index, int bound) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(serverSeed.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] h = mac.doFinal((clientSeed + ":" + nonce + ":" + index).getBytes(StandardCharsets.UTF_8));

            // Na5dhou 8 bytes ka nombre, ba3d modulo el bound
            long v = 0;
            for (int i = 0; i < 8; i++)
                v = (v << 8) | (h[i] & 0xFF);
            return (int) Long.remainderUnsigned(v, bound);
        } catch (Exception e) {
            throw new IllegalStateException("Erreur HMAC", e);
        }
    }

    public static String sha256(String text) {
        try {
            return HexFormat.of().formatHex(
                    MessageDigest.getInstance("SHA-256").digest(text.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Erreur SHA-256", e);
        }
    }

    private FairSeed create(String username, String clientSeed) {
        String serverSeed = randomHex(32);
        return repository.save(FairSeed.builder()
                .username(username)
                .serverSeed(serverSeed)
                .serverSeedHash(sha256(serverSeed))
                .clientSeed(clientSeed)
                .nonce(0)
                .active(true)
                .build());
    }

    private String randomHex(int bytes) {
        byte[] b = new byte[bytes];
        random.nextBytes(b);
        return HexFormat.of().formatHex(b);
    }
}