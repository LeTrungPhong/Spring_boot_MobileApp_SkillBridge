package com._NguoiDev.SkillBridge.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_username", referencedColumnName = "username")
    @JsonIgnoreProperties({"authorities","assignments"})

    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_username", referencedColumnName = "username")
    @JsonIgnoreProperties({"authorities","assignments"})
    private User receiver;

    private LocalDateTime timestamp;
    private String message;
}
