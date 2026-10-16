package tn.casino.casino_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.casino.casino_backend.dto.*;
import tn.casino.casino_backend.entity.*;
import tn.casino.casino_backend.repository.*;
import tn.casino.casino_backend.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;


import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final GameTransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginAttemptService loginAttemptService;

    // Jetons virtuels gratuits lel user jdid
    private static final BigDecimal WELCOME_BONUS = new BigDecimal("1000");

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("Username déjà utilisé");
        if (userRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email déjà utilisé");

        // Nasn3ou el user (mot de passe crypté)
        User user = User.builder()
                .username(req.getUsername())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .role(Role.USER)
                .build();

        // Nasn3ou el wallet b bonus de bienvenue
        Wallet wallet = Wallet.builder()
                .balance(WELCOME_BONUS)
                .user(user)
                .build();
        user.setWallet(wallet);
        userRepository.save(user);

        // Nsajlou el bonus fel historique
        transactionRepository.save(GameTransaction.builder()
                .sender("CASINO")
                .receiver(user.getUsername())
                .amount(WELCOME_BONUS)
                .type(TransactionType.BONUS)
                .game("WELCOME")
                .build());

        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest req) {
        // Spring yetaked men el mot de passe (yelzem ykoun s7i7 wala yethrow exception)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));

        User user = userRepository.findByUsername(req.getUsername()).orElseThrow();
        String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }
    public AuthResponse login(LoginRequest req, String ip) {
    if (loginAttemptService.isBlocked(ip, req.getUsername()))
        throw new IllegalStateException("Trop de tentatives. Réessayez dans 15 minutes");

    try {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
    } catch (BadCredentialsException e) {
        loginAttemptService.failed(ip, req.getUsername());
        throw e;
    }
    loginAttemptService.succeeded(ip, req.getUsername());

    User user = userRepository.findByUsername(req.getUsername()).orElseThrow();
    String token = jwtService.generateToken(user.getUsername(), user.getRole().name());
    return new AuthResponse(token, user.getUsername(), user.getRole().name());
}
}