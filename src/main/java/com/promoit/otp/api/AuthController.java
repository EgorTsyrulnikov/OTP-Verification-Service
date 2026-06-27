package com.promoit.otp.api;

import com.promoit.otp.model.User;
import com.promoit.otp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) throws SQLException {
        log.info("Registration request for login: {}", request.login());
        if (request.login() == null || request.password() == null || request.role() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Login, password and role are required"));
        }
        
        User user = userService.registerUser(request.login(), request.password(), request.role().toUpperCase());
        return ResponseEntity.ok(Map.of("id", user.getId(), "login", user.getLogin(), "role", user.getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) throws SQLException {
        log.info("Login request for login: {}", request.login());
        if (request.login() == null || request.password() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Login and password are required"));
        }

        String token = userService.login(request.login(), request.password());
        return ResponseEntity.ok(Map.of("token", token));
    }

    public record RegisterRequest(String login, String password, String role) {}
    public record LoginRequest(String login, String password) {}
}
