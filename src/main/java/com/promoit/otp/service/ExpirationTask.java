package com.promoit.otp.service;

import com.promoit.otp.dao.OtpCodeDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class ExpirationTask {
    private static final Logger log = LoggerFactory.getLogger(ExpirationTask.class);

    private final OtpCodeDao otpCodeDao;

    @Autowired
    public ExpirationTask(OtpCodeDao otpCodeDao) {
        this.otpCodeDao = otpCodeDao;
    }

    @Scheduled(fixedRate = 60000) // run every 60 seconds
    public void markExpiredOtps() {
        try {
            int updated = otpCodeDao.markExpiredCodes();
            if (updated > 0) {
                log.info("Marked {} OTP codes as EXPIRED", updated);
            }
        } catch (SQLException e) {
            log.error("Failed to execute expiration task", e);
        }
    }
}
