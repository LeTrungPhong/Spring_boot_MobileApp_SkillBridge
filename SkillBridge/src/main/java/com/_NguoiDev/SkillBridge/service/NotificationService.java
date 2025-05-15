package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.dto.response.NotificationResponse;
import com._NguoiDev.SkillBridge.entity.MyNotification;
import com._NguoiDev.SkillBridge.entity.User;
import com._NguoiDev.SkillBridge.entity.UserDeviceToken;
import com._NguoiDev.SkillBridge.exception.AppException;
import com._NguoiDev.SkillBridge.exception.ErrorCode;
import com._NguoiDev.SkillBridge.mapper.NotificationMapper;
import com._NguoiDev.SkillBridge.repository.*;
import com.google.firebase.messaging.FirebaseMessagingException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    UserRepository userRepository;
    UserDeviceTokenRepository userDeviceTokenRepository;
    FirebaseMessagingService firebaseMessagingService;
    NotificationMapper notificationMapper;

    public void notify(MyNotification notification, int classId) throws FirebaseMessagingException {
        notificationRepository.save(notification);
        List<User> recipients = userRepository.findUserByclassId(classId);
        for (User recipient : recipients) {
            String username = recipient.getUsername();
            List<UserDeviceToken> userDeviceTokens = userDeviceTokenRepository.findByUserUsername(username);
            if (!userDeviceTokens.isEmpty()) {
                for (UserDeviceToken deviceToken : userDeviceTokens) {
                    String fcmToken  = deviceToken.getFcmToken();
                    firebaseMessagingService.sendNotification(fcmToken, notification);
                }
            }

            Set<MyNotification> notificationSet = recipient.getNotifications();
            notificationSet.add(notification);
            recipient.setNotifications(notificationSet);
            userRepository.save(recipient);
        }
    }

    public List<NotificationResponse> getAllNotifications() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User u =userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return u.getNotifications().stream()
                .sorted(Comparator.comparing(MyNotification::getCreatedAt).reversed())
                .map(notificationMapper::toNotificationResponse).collect(Collectors.toList());
    }

}
