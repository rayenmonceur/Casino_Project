// UserRepository.java
package tn.casino.casino_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.casino.casino_backend.entity.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}