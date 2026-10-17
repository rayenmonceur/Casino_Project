package tn.casino.casino_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Partie de cartes en cours ou terminée (Blackjack ou Poker)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardRound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String game; // BLACKJACK ou POKER

    // SECRET: ordre du paquet (ma yetb3athch abadan lel client)
    @Column(length = 400)
    private String deck;
    private int nextIndex; // prochaine carte à tirer

    private String playerCards; // "0,14"
    private String dealerCards; // Blackjack barka

    private BigDecimal bet;
    private BigDecimal payout;
    private boolean active;
    private String result; // WIN, LOSE, PUSH, BLACKJACK, ROYAL_FLUSH...

    // Preuve provably fair
    private String serverSeedHash;
    private String clientSeed;
    private long nonce;

    // Ykhalli 2 requêtes fi nafs el wa9t ma ykharbou el partie
    @Version
    private Long version;

    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}