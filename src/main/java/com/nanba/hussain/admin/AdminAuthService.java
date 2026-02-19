package com.nanba.hussain.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdminAuthService {

    private final String adminUsername;
    private final String adminPassword;
    private final Map<String, Instant> tokenStore = new ConcurrentHashMap<>();

    public AdminAuthService(
            @Value("${app.admin.username:admin}") String adminUsername,
            @Value("${app.admin.password:admin123}") String adminPassword) {
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    public String login(String username, String password) {
        if (!adminUsername.equals(username) || !adminPassword.equals(password)) {
            return null;
        }
        String token = UUID.randomUUID().toString();
        tokenStore.put(token, Instant.now().plusSeconds(60L * 60L * 12L));
        return token;
    }

    public boolean isValid(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        Instant expiry = tokenStore.get(token);
        if (expiry == null) {
            return false;
        }
        if (Instant.now().isAfter(expiry)) {
            tokenStore.remove(token);
            return false;
        }
        return true;
    }

    public void logout(String token) {
        if (token != null) {
            tokenStore.remove(token);
        }
    }
}
