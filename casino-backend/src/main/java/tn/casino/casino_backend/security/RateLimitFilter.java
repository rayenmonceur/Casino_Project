package tn.casino.casino_backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tn.casino.casino_backend.service.LoginAttemptService;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

// Max 120 requêtes par minute par IP, sinon 429
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int MAX_PER_MINUTE = 120;
    private final LoginAttemptService attemptService;

    // ip -> [compteur, début de la fenêtre]
    private final ConcurrentHashMap<String, long[]> counters = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String ip = req.getRemoteAddr();
        long now = System.currentTimeMillis();

        long[] c = counters.computeIfAbsent(ip, k -> new long[]{0, now});
        boolean exceeded;
        synchronized (c) {
            if (now - c[1] > 60_000) { c[0] = 0; c[1] = now; }
            c[0]++;
            exceeded = c[0] > MAX_PER_MINUTE;
            if (c[0] == MAX_PER_MINUTE + 1)
                attemptService.log("RATE_LIMIT", ip, null, "Plus de " + MAX_PER_MINUTE + " requêtes/min");
        }

        if (exceeded) {
            res.setStatus(429);
            res.setContentType("application/json");
            res.getWriter().write("{\"error\":\"Trop de requêtes\"}");
            return;
        }
        chain.doFilter(req, res);
    }
}