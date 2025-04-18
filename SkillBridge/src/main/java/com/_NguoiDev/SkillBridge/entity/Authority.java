package com._NguoiDev.SkillBridge.entity;

import com._NguoiDev.SkillBridge.embedded.AuthorityId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Authority {
    @EmbeddedId
    private AuthorityId authorityId;

    @MapsId("username")
    @ManyToOne
    @JoinColumn(name = "username")
    private User user;
}
