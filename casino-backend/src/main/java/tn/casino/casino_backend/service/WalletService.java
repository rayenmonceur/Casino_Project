package tn.casino.casino_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.casino.casino_backend.entity.GameTransaction;
import tn.casino.casino_backend.entity.TransactionType;
import tn.casino.casino_backend.entity.Wallet;
import tn.casino.casino_backend.repository.GameTransactionRepository;
import tn.casino.casino_backend.repository.WalletRepository;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletService {

    private static final BigDecimal MIN_BET = new BigDecimal("1");
    private static final BigDecimal MAX_BET = new BigDecimal("500");

    private final WalletRepository walletRepository;
    private final GameTransactionRepository transactionRepository;

    @Transactional(readOnly = true)
    public BigDecimal getBalance(String username) {
        return walletRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Wallet introuvable"))
                .getBalance();
    }

    // Verifi elli el mise s7i7a
    public void validateBet(BigDecimal bet) {
        if (bet == null || bet.compareTo(MIN_BET) < 0 || bet.compareTo(MAX_BET) > 0)
            throw new IllegalArgumentException("La mise doit être entre 1 et 500 jetons");
    }

    // Ya5ou el mise w ya3ti el gain (payout) fi transaction wa7da:
    // ken fama erreur, kol chay yetraja3 (ma yibka 7ata solde ghalet)
    @Transactional
    public BigDecimal settle(String username, String game, BigDecimal bet, BigDecimal payout) {
        Wallet wallet = walletRepository.findByUsernameForUpdate(username)
                .orElseThrow(() -> new IllegalArgumentException("Wallet introuvable"));

        if (wallet.getBalance().compareTo(bet) < 0)
            throw new IllegalArgumentException("Solde insuffisant");

        // 1) N5asmou el mise
        wallet.setBalance(wallet.getBalance().subtract(bet));
        transactionRepository.save(GameTransaction.builder()
                .sender(username).receiver("CASINO")
                .amount(bet).type(TransactionType.BET).game(game).build());

        // 2) Ken rba7, nzidou el gain
        if (payout.signum() > 0) {
            wallet.setBalance(wallet.getBalance().add(payout));
            transactionRepository.save(GameTransaction.builder()
                    .sender("CASINO").receiver(username)
                    .amount(payout).type(TransactionType.WIN).game(game).build());
        }

        walletRepository.save(wallet);
        return wallet.getBalance();
    }
}