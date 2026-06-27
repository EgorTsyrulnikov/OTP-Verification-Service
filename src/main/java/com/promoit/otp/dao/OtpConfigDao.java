package com.promoit.otp.dao;

import com.promoit.otp.model.OtpConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class OtpConfigDao {
    private final DataSource dataSource;

    @Autowired
    public OtpConfigDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public OtpConfig getConfig() throws SQLException {
        String sql = "SELECT id, lifetime_seconds, length FROM otp_config LIMIT 1";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return new OtpConfig(rs.getLong("id"), rs.getInt("lifetime_seconds"), rs.getInt("length"));
            }
        }
        return new OtpConfig(null, 300, 6); // default fallback if empty for some reason
    }

    public void updateConfig(int lifetimeSeconds, int length) throws SQLException {
        String sql = "UPDATE otp_config SET lifetime_seconds = ?, length = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, lifetimeSeconds);
            stmt.setInt(2, length);
            stmt.executeUpdate();
        }
    }
}
