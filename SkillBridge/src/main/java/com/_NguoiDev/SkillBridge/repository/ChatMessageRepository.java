package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.entity.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    @Query("select m from ChatMessage m " +
            "where ((m.sender.username= :sender and m.receiver.username= :receiver)" +
            "or  (m.sender.username= :receiver and m.receiver.username= :sender))" +
            "and m.timestamp<:lastTime " +
            "order by m.timestamp DESC ")
    List<ChatMessage> getPreviousMessages(@Param("sender") String sender,
                                                 @Param("receiver") String receiver,
                                                 @Param("lastTime") LocalDateTime lastTime);
}
