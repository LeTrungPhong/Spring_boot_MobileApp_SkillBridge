package com._NguoiDev.SkillBridge.service;

import com._NguoiDev.SkillBridge.entity.MyNotification;
import com._NguoiDev.SkillBridge.entity.User;
import com._NguoiDev.SkillBridge.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class NotificationService {
    NotificationRepository notificationRepository;
    UserRepository userRepository;

    public void notify(MyNotification notification, int classId) {
        notificationRepository.save(notification);
        List<User> recipients = userRepository.findUserByclassId(classId);
        for (User recipient : recipients) {
            Set<MyNotification> notificationSet = recipient.getNotifications();
            notificationSet.add(notification);
            recipient.setNotifications(notificationSet);
            userRepository.save(recipient);
        }
    }

}
