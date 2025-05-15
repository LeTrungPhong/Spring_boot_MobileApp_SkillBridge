package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.entity.MyNotification;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FirebaseMessagingService {
    @Autowired
    FirebaseMessaging firebaseMessaging;
    public String sendNotification(String fcmToken, MyNotification notification) throws FirebaseMessagingException {
        Notification notification1 = Notification.builder()
                .setTitle(notification.getTitle())
                .setBody(notification.getBody())
                .build();

        Message message = Message.builder()
                .setNotification(notification1)
                .setToken(fcmToken)
                .build();

        return FirebaseMessaging.getInstance(FirebaseApp.getInstance("skill-bridge")).send(message);
    }
}
