package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.MyNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<MyNotification, Integer> {
}
