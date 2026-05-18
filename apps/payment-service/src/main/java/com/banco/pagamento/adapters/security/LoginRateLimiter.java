package com.banco.pagamento.adapters.security;

import com.banco.pagamento.adapters.config.LoginRateLimitProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class LoginRateLimiter {

    private final LoginRateLimitProperties properties;
    private final Clock clock = Clock.systemUTC();
    private final Map<String, AttemptWindow> attempts = new ConcurrentHashMap<>();

    public boolean permite(String email, String ip) {
        if (!properties.enabled()) {
            return true;
        }
        AttemptWindow window = attempts.computeIfAbsent(key(email, ip), ignored -> new AttemptWindow());
        synchronized (window) {
            long now = now();
            evictExpired(window, now);
            return window.blockedUntil <= now;
        }
    }

    public void registrarFalha(String email, String ip) {
        if (!properties.enabled()) {
            return;
        }
        AttemptWindow window = attempts.computeIfAbsent(key(email, ip), ignored -> new AttemptWindow());
        synchronized (window) {
            long now = now();
            evictExpired(window, now);
            window.failures.addLast(now);
            if (window.failures.size() >= properties.maxAttempts()) {
                window.blockedUntil = now + properties.blockSeconds();
                window.failures.clear();
            }
        }
    }

    public void registrarSucesso(String email, String ip) {
        attempts.remove(key(email, ip));
    }

    private void evictExpired(AttemptWindow window, long now) {
        long threshold = now - properties.windowSeconds();
        while (!window.failures.isEmpty() && window.failures.peekFirst() < threshold) {
            window.failures.removeFirst();
        }
    }

    private long now() {
        return clock.instant().getEpochSecond();
    }

    private String key(String email, String ip) {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(Locale.ROOT);
        String normalizedIp = ip == null ? "" : ip.trim();
        return normalizedEmail + "|" + normalizedIp;
    }

    private static final class AttemptWindow {
        private final Deque<Long> failures = new ArrayDeque<>();
        private long blockedUntil;
    }
}
