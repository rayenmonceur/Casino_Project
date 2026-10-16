package tn.casino.casino_backend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// Kol 7aja mochbouha (brute force, spam...) tetsajjel hna
@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SecurityEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type;      // LOGIN_FAILED, LOGIN_BLOCKED, RATE_LIMIT
    private String ip;
    private String username;
    private String details;
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() { createdAt = LocalDateTime.now(); }
}