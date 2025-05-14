package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Integer> {
    Optional<UserDeviceToken> findByUserUsername(String username);
}
