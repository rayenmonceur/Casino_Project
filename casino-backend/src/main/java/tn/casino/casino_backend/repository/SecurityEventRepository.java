package tn.casino.casino_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.casino.casino_backend.entity.SecurityEvent;
import java.util.List;

public interface SecurityEventRepository extends JpaRepository<SecurityEvent, Long> {
    List<SecurityEvent> findTop100ByOrderByCreatedAtDesc();
}