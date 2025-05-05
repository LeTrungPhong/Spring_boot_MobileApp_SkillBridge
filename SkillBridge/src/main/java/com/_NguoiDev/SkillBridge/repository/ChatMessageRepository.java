package com._NguoiDev.SkillBridge.repository;

import com._NguoiDev.SkillBridge.dto.response.ChatBoxResponse;
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
                                                 @Param("lastTime") LocalDateTime lastTime,
                                          Pageable pageable);

    @Query("SELECT m FROM ChatMessage m " +
            "WHERE ((m.sender.username = :sender AND m.receiver.username = :receiver) " +
            "   OR (m.sender.username = :receiver AND m.receiver.username = :sender)) " +
            "ORDER BY m.timestamp DESC")
    List<ChatMessage> getLastMessage(@Param("sender") String sender, @Param("receiver") String receiver);

    @Query("select cm from ChatMessage cm " +
            "where cm.id IN (" +
            "select MAX(cm2.id) from ChatMessage cm2 " +
            "where ((cm2.sender.username =:username AND cm2.receiver.username != :username)" +
            "   or (cm2.sender.username!=:username AND cm2.receiver.username = :username))" +
            "GROUP BY case " +
            "       when cm2.sender.username = :username then cm2.receiver.username" +
            "       else cm2.sender.username" +
            "   END " +
            ") ORDER BY cm.timestamp DESC ")
    List<ChatMessage> getAllChatBox(@Param("username") String username);
}
