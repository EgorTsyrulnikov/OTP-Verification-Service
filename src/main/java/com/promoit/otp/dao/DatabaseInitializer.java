package com.promoit.otp.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
public class DatabaseInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DatabaseInitializer.class);

    private final DataSource dataSource;

    @Autowired
    public DatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Initializing database schema...");
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY, " +
                    "login VARCHAR(255) UNIQUE NOT NULL, " +
                    "password_hash VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(50) NOT NULL" +
                    ")";
            stmt.execute(createUsersTable);

            String createOtpConfigTable = "CREATE TABLE IF NOT EXISTS otp_config (" +
                    "id SERIAL PRIMARY KEY, " +
                    "lifetime_seconds INT NOT NULL, " +
                    "length INT NOT NULL" +
                    ")";
            stmt.execute(createOtpConfigTable);

            String createOtpCodesTable = "CREATE TABLE IF NOT EXISTS otp_codes (" +
                    "id SERIAL PRIMARY KEY, " +
                    "user_id BIGINT REFERENCES users(id) ON DELETE CASCADE, " +
                    "operation_id VARCHAR(255) NOT NULL, " +
                    "code VARCHAR(50) NOT NULL, " +
                    "status VARCHAR(50) NOT NULL, " +
                    "created_at TIMESTAMP NOT NULL, " +
                    "expires_at TIMESTAMP NOT NULL" +
                    ")";
            stmt.execute(createOtpCodesTable);

            // Initialize default OTP config if empty
            String checkConfig = "SELECT COUNT(*) FROM otp_config";
            var rs = stmt.executeQuery(checkConfig);
            if (rs.next() && rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO otp_config (lifetime_seconds, length) VALUES (300, 6)");
                log.info("Inserted default OTP configuration (300s, 6 digits).");
            }
            
            log.info("Database schema initialized.");
        } catch (Exception e) {
            log.error("Failed to initialize database schema", e);
            throw e;
        }
    }
}
