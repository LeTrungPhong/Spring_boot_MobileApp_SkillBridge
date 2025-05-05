package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    public boolean existsByUsername(String username);

    Optional<User> findByUsername(String username);

    List<User> findByUsernameNot(String username);

    @Query("SELECT DISTINCT u FROM User u " +
            "WHERE u.username <> :username AND (" +
            "EXISTS (SELECT m FROM ChatMessage m WHERE m.sender = u AND m.receiver.username = :username) " +
            "OR EXISTS (SELECT m FROM ChatMessage m WHERE m.receiver = u AND m.sender.username = :username))")
    List<User> findUsersChattedWith(@Param("username") String username);
}
