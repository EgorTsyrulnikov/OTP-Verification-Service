package com.promoit.otp.security;

import com.promoit.otp.model.User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenService {
    // Simple in-memory token store for demonstration
    // Maps Token -> TokenInfo
    private final Map<String, TokenInfo> tokenStore = new ConcurrentHashMap<>();

    private static class TokenInfo {
        User user;
        LocalDateTime expiresAt;

        TokenInfo(User user, LocalDateTime expiresAt) {
            this.user = user;
            this.expiresAt = expiresAt;
        }
    }

    public String generateToken(User user) {
        String token = UUID.randomUUID().toString();
        // Token valid for 24 hours
        tokenStore.put(token, new TokenInfo(user, LocalDateTime.now().plusHours(24)));
        return token;
    }

    public User validateTokenAndGetUser(String token) {
        if (token == null) return null;
        
        TokenInfo info = tokenStore.get(token);
        if (info == null) return null;

        if (LocalDateTime.now().isAfter(info.expiresAt)) {
            tokenStore.remove(token);
            return null;
        }

        return info.user;
    }
    
    public void invalidateToken(String token) {
        if (token != null) {
            tokenStore.remove(token);
        }
    }
}
