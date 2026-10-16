package tn.casino.casino_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Solde en jetons virtuels. BigDecimal bech ma n5asrouch fel 7sab
    @Column(nullable = false)
    private BigDecimal balance;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Bech ma ysirch 2 le3b fi nafs el wa9t ykharbou solde (concurrence)
    @Version
    private Long version;
}