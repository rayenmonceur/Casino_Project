package tn.casino.casino_backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.casino.casino_backend.entity.SecurityEvent;
import tn.casino.casino_backend.repository.SecurityEventRepository;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;            // 5 tentatives
    private static final long BLOCK_MS = 15 * 60 * 1000;  // blocage 15 minutes

    private final SecurityEventRepository eventRepository;

    // clé = ip:username  ->  [nombre d'échecs, wa9t el blocage]
    private final ConcurrentHashMap<String, long[]> attempts = new ConcurrentHashMap<>();

    public boolean isBlocked(String ip, String username) {
        long[] data = attempts.get(ip + ":" + username);
        if (data == null) return false;
        if (data[1] > Instant.now().toEpochMilli()) return true;
        if (data[1] != 0) attempts.remove(ip + ":" + username); // el blocage tfa
        return false;
    }

    public void failed(String ip, String username) {
        long[] data = attempts.computeIfAbsent(ip + ":" + username, k -> new long[2]);
        data[0]++;
        log("LOGIN_FAILED", ip, username, "Tentative n°" + data[0]);
        if (data[0] >= MAX_ATTEMPTS) {
            data[1] = Instant.now().toEpochMilli() + BLOCK_MS;
            log("LOGIN_BLOCKED", ip, username, "Bloqué 15 min après " + MAX_ATTEMPTS + " échecs");
        }
    }

    public void succeeded(String ip, String username) {
        attempts.remove(ip + ":" + username);
    }

    public void log(String type, String ip, String username, String details) {
        eventRepository.save(SecurityEvent.builder()
                .type(type).ip(ip).username(username).details(details).build());
    }
}