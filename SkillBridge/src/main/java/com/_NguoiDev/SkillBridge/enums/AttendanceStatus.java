package com._NguoiDev.SkillBridge.enums;

/**
 * Enum representing the attendance status of a student for a lesson
 * PRESENT: Student attended the lesson on time
 * LATE: Student attended the lesson but was late (more than 30 minutes after start time)
 * ABSENT: Student did not attend the lesson
 * EXCUSED: Student did not attend but had a valid excuse (e.g., medical, family emergency)
 */
public enum AttendanceStatus {
    PRESENT("Present - Attended on time"),
    LATE("Late - Arrived more than 30 minutes after the start time"),
    ABSENT("Absent - Did not attend"),
    EXCUSED("Excused - Absent with a valid reason");
    
    private final String description;
    
    AttendanceStatus(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
} 