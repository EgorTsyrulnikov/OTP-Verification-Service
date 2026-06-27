package com.promoit.otp.service;

import com.promoit.otp.dao.OtpCodeDao;
import com.promoit.otp.dao.OtpConfigDao;
import com.promoit.otp.model.OtpCode;
import com.promoit.otp.model.OtpConfig;
import com.promoit.otp.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class OtpService {
    private final OtpConfigDao configDao;
    private final OtpCodeDao codeDao;
    private final Map<String, NotificationService> notificationServices;
    private final Random random = new Random();

    @Autowired
    public OtpService(OtpConfigDao configDao, OtpCodeDao codeDao, List<NotificationService> services) {
        this.configDao = configDao;
        this.codeDao = codeDao;
        this.notificationServices = services.stream()
                .collect(Collectors.toMap(NotificationService::getChannelName, Function.identity()));
    }

    public OtpConfig getConfig() throws SQLException {
        return configDao.getConfig();
    }

    public void updateConfig(int lifetimeSeconds, int length) throws SQLException {
        configDao.updateConfig(lifetimeSeconds, length);
    }

    public void generateAndSendOtp(Long userId, String operationId, String channel, String destination) throws SQLException {
        OtpConfig config = configDao.getConfig();
        String codeStr = generateRandomCode(config.getLength());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(config.getLifetimeSeconds());

        OtpCode otpCode = new OtpCode(null, userId, operationId, codeStr, "ACTIVE", now, expiresAt);
        codeDao.save(otpCode);

        NotificationService ns = notificationServices.get(channel.toUpperCase());
        if (ns == null) {
            throw new IllegalArgumentException("Unsupported channel: " + channel);
        }
        
        ns.sendCode(destination, codeStr);
    }

    public boolean validateOtp(String operationId, String code) throws SQLException {
        Optional<OtpCode> otpOpt = codeDao.findByOperationIdAndCode(operationId, code);
        if (otpOpt.isEmpty()) {
            return false;
        }
        
        OtpCode otp = otpOpt.get();
        if (!"ACTIVE".equals(otp.getStatus())) {
            return false;
        }

        if (LocalDateTime.now().isAfter(otp.getExpiresAt())) {
            codeDao.updateStatus(otp.getId(), "EXPIRED");
            return false;
        }

        codeDao.updateStatus(otp.getId(), "USED");
        return true;
    }

    private String generateRandomCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10)); // 0-9
        }
        return sb.toString();
    }
}
