package com.promoit.otp.model;

public class OtpConfig {
    private Long id;
    private int lifetimeSeconds;
    private int length;

    public OtpConfig() {}

    public OtpConfig(Long id, int lifetimeSeconds, int length) {
        this.id = id;
        this.lifetimeSeconds = lifetimeSeconds;
        this.length = length;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getLifetimeSeconds() { return lifetimeSeconds; }
    public void setLifetimeSeconds(int lifetimeSeconds) { this.lifetimeSeconds = lifetimeSeconds; }
    public int getLength() { return length; }
    public void setLength(int length) { this.length = length; }
}
