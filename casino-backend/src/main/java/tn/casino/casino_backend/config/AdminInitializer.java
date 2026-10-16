package tn.casino.casino_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import tn.casino.casino_backend.entity.*;
import tn.casino.casino_backend.repository.UserRepository;

import java.math.BigDecimal;

// Yetexecuta mara wa7da 3and el démarrage: yasna3 el admin ken mouch mawjoud
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}") private String adminUsername;
    @Value("${app.admin.password}") private String adminPassword;
    @Value("${app.admin.email}") private String adminEmail;

    @Override
    public void run(String... args) {
        if (userRepository.existsByUsername(adminUsername)) return;

        User admin = User.builder()
                .username(adminUsername)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .build();
        admin.setWallet(Wallet.builder().balance(BigDecimal.ZERO).user(admin).build());
        userRepository.save(admin);
        System.out.println(">>> Compte admin créé: " + adminUsername);
    }
}