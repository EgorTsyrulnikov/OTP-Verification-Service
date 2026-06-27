package com.promoit.otp.service.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

@Service
public class TelegramNotificationService implements NotificationService {
    private static final Logger log = LoggerFactory.getLogger(TelegramNotificationService.class);

    private final String botToken;
    private final String telegramApiUrl;

    public TelegramNotificationService() {
        Properties config = loadConfig();
        this.botToken = config.getProperty("telegram.bot.token");
        this.telegramApiUrl = config.getProperty("telegram.api.url");
    }

    private Properties loadConfig() {
        try {
            Properties props = new Properties();
            props.load(TelegramNotificationService.class.getClassLoader()
                    .getResourceAsStream("telegram.properties"));
            return props;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load telegram configuration", e);
        }
    }

    @Override
    public void sendCode(String chatId, String code) {
        log.info("Sending OTP via Telegram to chat {}", chatId);
        String message = String.format("Your confirmation code is: %s", code);
        String url = String.format("%s%s/sendMessage?chat_id=%s&text=%s",
                telegramApiUrl,
                botToken,
                chatId,
                urlEncode(message));

        sendTelegramRequest(url);
    }

    private void sendTelegramRequest(String url) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );

            int statusCode = response.statusCode();
            if (statusCode != 200) {
                log.error("Telegram API error. Status code: {}, Response: {}", statusCode, response.body());
            } else {
                log.info("Telegram message sent successfully");
            }
        } catch (InterruptedException e) {
            log.error("Error sending Telegram message: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.error("Error sending Telegram message: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private static String urlEncode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    @Override
    public String getChannelName() {
        return "TELEGRAM";
    }
}
