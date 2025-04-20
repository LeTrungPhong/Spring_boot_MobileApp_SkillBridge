package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.InvalidToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvalidTokenRepository extends JpaRepository<InvalidToken, String> {
}
