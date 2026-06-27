package com.promoit.otp.service.notification;

public interface NotificationService {
    void sendCode(String destination, String code);
    String getChannelName();
}
