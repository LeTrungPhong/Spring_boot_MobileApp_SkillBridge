package com._NguoiDev.SkillBridge.embedded;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class StudentClassId implements Serializable {
    private int studentId;
    private int classId;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentClassId that)) return false;
        return studentId == that.studentId && 
               classId == that.classId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, classId);
    }
} 