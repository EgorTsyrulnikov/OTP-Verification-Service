package com.promoit.otp.model;

import java.time.LocalDateTime;

public class OtpCode {
    private Long id;
    private Long userId;
    private String operationId;
    private String code;
    private String status; // "ACTIVE", "EXPIRED", "USED"
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public OtpCode() {}

    public OtpCode(Long id, Long userId, String operationId, String code, String status, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.id = id;
        this.userId = userId;
        this.operationId = operationId;
        this.code = code;
        this.status = status;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getOperationId() { return operationId; }
    public void setOperationId(String operationId) { this.operationId = operationId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
}
