package com.promoit.otp.dao;

import com.promoit.otp.model.OtpCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Optional;

@Repository
public class OtpCodeDao {
    private final DataSource dataSource;

    @Autowired
    public OtpCodeDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public OtpCode save(OtpCode code) throws SQLException {
        String sql = "INSERT INTO otp_codes (user_id, operation_id, code, status, created_at, expires_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, code.getUserId());
            stmt.setString(2, code.getOperationId());
            stmt.setString(3, code.getCode());
            stmt.setString(4, code.getStatus());
            stmt.setTimestamp(5, Timestamp.valueOf(code.getCreatedAt()));
            stmt.setTimestamp(6, Timestamp.valueOf(code.getExpiresAt()));
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    code.setId(rs.getLong(1));
                }
            }
            return code;
        }
    }

    public Optional<OtpCode> findByOperationIdAndCode(String operationId, String code) throws SQLException {
        String sql = "SELECT id, user_id, operation_id, code, status, created_at, expires_at FROM otp_codes WHERE operation_id = ? AND code = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, operationId);
            stmt.setString(2, code);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        }
        return Optional.empty();
    }

    public void updateStatus(Long id, String status) throws SQLException {
        String sql = "UPDATE otp_codes SET status = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    public int markExpiredCodes() throws SQLException {
        String sql = "UPDATE otp_codes SET status = 'EXPIRED' WHERE status = 'ACTIVE' AND expires_at < CURRENT_TIMESTAMP";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            return stmt.executeUpdate();
        }
    }

    private OtpCode mapRow(ResultSet rs) throws SQLException {
        return new OtpCode(
                rs.getLong("id"),
                rs.getLong("user_id"),
                rs.getString("operation_id"),
                rs.getString("code"),
                rs.getString("status"),
                rs.getTimestamp("created_at").toLocalDateTime(),
                rs.getTimestamp("expires_at").toLocalDateTime()
        );
    }
}
