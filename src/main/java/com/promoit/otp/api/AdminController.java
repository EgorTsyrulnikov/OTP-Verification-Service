package com.promoit.otp.api;

import com.promoit.otp.model.User;
import com.promoit.otp.service.OtpService;
import com.promoit.otp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);

    private final OtpService otpService;
    private final UserService userService;

    @Autowired
    public AdminController(OtpService otpService, UserService userService) {
        this.otpService = otpService;
        this.userService = userService;
    }

    @PutMapping("/config")
    public ResponseEntity<?> updateConfig(@RequestBody ConfigRequest request) throws SQLException {
        log.info("Admin updating OTP config: lifetime={}, length={}", request.lifetimeSeconds(), request.length());
        if (request.lifetimeSeconds() <= 0 || request.length() <= 0) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid configuration values"));
        }
        
        otpService.updateConfig(request.lifetimeSeconds(), request.length());
        return ResponseEntity.ok(Map.of("message", "Configuration updated successfully"));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() throws SQLException {
        log.info("Admin fetching non-admin users");
        List<User> users = userService.getAllNonAdmins();
        List<UserDto> dtos = users.stream()
                .map(u -> new UserDto(u.getId(), u.getLogin(), u.getRole()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) throws SQLException {
        log.info("Admin deleting user ID: {}", id);
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    public record ConfigRequest(int lifetimeSeconds, int length) {}
    public record UserDto(Long id, String login, String role) {}
}
