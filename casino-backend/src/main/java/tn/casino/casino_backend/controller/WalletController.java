package tn.casino.casino_backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.casino.casino_backend.service.WalletService;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/balance")
    public Map<String, BigDecimal> balance(Authentication auth) {
        return Map.of("balance", walletService.getBalance(auth.getName()));
    }
}