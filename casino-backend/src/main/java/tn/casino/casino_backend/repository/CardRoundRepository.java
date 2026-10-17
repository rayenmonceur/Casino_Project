package tn.casino.casino_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.casino.casino_backend.entity.CardRound;

import java.util.Optional;

public interface CardRoundRepository extends JpaRepository<CardRound, Long> {
    Optional<CardRound> findByUsernameAndGameAndActiveTrue(String username, String game);
}