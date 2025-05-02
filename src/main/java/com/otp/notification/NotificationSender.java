package com.otp.notification;

public interface NotificationSender {
    boolean send(String to, String message);
} 