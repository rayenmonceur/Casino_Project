package tn.casino.casino_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// Seed actif mte3 kol joueur (wala 9dim ba3d rotation)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FairSeed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    // Secret: ma yetb3athch lel client 9bal el rotation
    @Column(nullable = false)
    private String serverSeed;

    // SHA-256 mte3 serverSeed: hedha elli yetb3ath lel client
    @Column(nullable = false)
    private String serverSeedHash;

    @Column(nullable = false)
    private String clientSeed;

    private long nonce;

    private boolean active;

    // Ken 2 le3bet jew fi nafs el wa9t, el thanya tdhrab (409)
    @Version
    private Long version;

    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }
}