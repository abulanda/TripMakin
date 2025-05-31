package com.tripmakin.service;

import com.tripmakin.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenService {
    private final Map<String, String> tokens = new ConcurrentHashMap<>();

    public String generateRefreshToken(User user) {
        String token = UUID.randomUUID().toString();
        tokens.put(token, user.getEmail());
        return token;
    }

    public boolean isValid(String token) {
        return tokens.containsKey(token);
    }

    public String getUsername(String token) {
        return tokens.get(token);
    }

    public void invalidate(String token) {
        tokens.remove(token);
    }
}
