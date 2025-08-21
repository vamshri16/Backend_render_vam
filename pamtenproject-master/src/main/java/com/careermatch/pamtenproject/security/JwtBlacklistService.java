package com.careermatch.pamtenproject.security;

import org.springframework.stereotype.Service;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class JwtBlacklistService {

    private final ConcurrentHashMap<String, Long> blacklistedTokens = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public JwtBlacklistService() {
        // Clean up expired blacklisted tokens every hour
        scheduler.scheduleAtFixedRate(this::cleanupExpiredTokens, 1, 1, TimeUnit.HOURS);
    }

    public void blacklistToken(String token, long expirationTime) {
        blacklistedTokens.put(token, expirationTime);
    }

    public boolean isBlacklisted(String token) {
        return blacklistedTokens.containsKey(token);
    }

    private void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue() < currentTime);
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}