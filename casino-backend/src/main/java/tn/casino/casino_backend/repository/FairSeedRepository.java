package tn.casino.casino_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.casino.casino_backend.entity.FairSeed;
import java.util.Optional;

public interface FairSeedRepository extends JpaRepository<FairSeed, Long> {
    Optional<FairSeed> findByUsernameAndActiveTrue(String username);
}