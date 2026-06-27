package com.promoit.otp.api;

import com.promoit.otp.model.User;
import com.promoit.otp.service.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.Map;

@RestController
@RequestMapping("/api/otp")
public class OtpController {
    private static final Logger log = LoggerFactory.getLogger(OtpController.class);
    
    private final OtpService otpService;

    @Autowired
    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateOtp(@RequestBody GenerateRequest request, HttpServletRequest servletRequest) throws SQLException {
        log.info("OTP generation request for operation: {}, channel: {}", request.operationId(), request.channel());
        if (request.operationId() == null || request.channel() == null || request.destination() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "operationId, channel, and destination are required"));
        }

        User user = (User) servletRequest.getAttribute("user");
        otpService.generateAndSendOtp(user.getId(), request.operationId(), request.channel(), request.destination());
        
        return ResponseEntity.ok(Map.of("message", "OTP generated and sent successfully"));
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validateOtp(@RequestBody ValidateRequest request) throws SQLException {
        log.info("OTP validation request for operation: {}", request.operationId());
        if (request.operationId() == null || request.code() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "operationId and code are required"));
        }

        boolean isValid = otpService.validateOtp(request.operationId(), request.code());
        return ResponseEntity.ok(Map.of("isValid", isValid));
    }

    public record GenerateRequest(String operationId, String channel, String destination) {}
    public record ValidateRequest(String operationId, String code) {}
}
