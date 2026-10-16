// GameTransactionRepository.java
package tn.casino.casino_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.casino.casino_backend.entity.GameTransaction;
import java.util.List;
import tn.casino.casino_backend.entity.TransactionType;

public interface GameTransactionRepository extends JpaRepository<GameTransaction, Long> {
    // Lel admin dashboard: les dernières transactions
    List<GameTransaction> findTop50ByOrderByCreatedAtDesc();
    @org.springframework.data.jpa.repository.Query(
        "select coalesce(sum(t.amount),0) from GameTransaction t where t.type = :type")
    java.math.BigDecimal sumByType(@org.springframework.data.repository.query.Param("type") TransactionType type);
}
