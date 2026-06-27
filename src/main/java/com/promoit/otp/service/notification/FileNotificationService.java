package com.promoit.otp.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

@Service
public class FileNotificationService implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(FileNotificationService.class);
    
    private final Path otpFilePath;

    public FileNotificationService() {
        this.otpFilePath = Paths.get("otp_codes.txt");
    }

    @Override
    public void sendCode(String destination, String code) {
        log.info("Saving OTP to file for {}", destination);
        String entry = String.format("[%s] OTP for %s: %s%n", LocalDateTime.now(), destination, code);
        try {
            Files.writeString(otpFilePath, entry, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            log.info("OTP saved to file successfully.");
        } catch (IOException e) {
            log.error("Failed to write OTP to file", e);
            throw new RuntimeException("Failed to write OTP to file", e);
        }
    }

    @Override
    public String getChannelName() {
        return "FILE";
    }
}
