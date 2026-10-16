package tn.casino.casino_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.casino.casino_backend.entity.Wallet;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // Bla lock pessimiste (MariaDB ma yfhemhouch). El @Version fel Wallet yhmi men concurrence
    @Query("select w from Wallet w where w.user.username = :username")
    Optional<Wallet> findByUsernameForUpdate(@Param("username") String username);

    @Query("select w from Wallet w where w.user.username = :username")
    Optional<Wallet> findByUsername(@Param("username") String username);
}