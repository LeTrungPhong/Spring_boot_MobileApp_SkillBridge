package com._NguoiDev.SkillBridge.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    ROLE_INVALID(1005, "You cant be Teacher and Student", HttpStatus.BAD_REQUEST),
    DOB_NULL(1008, "Your age must not be null", HttpStatus.BAD_REQUEST),
    UNKNOWN_ERROR(9999, "Unknown error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(9998, "Invalid message key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001, "User existed", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1002, "User existed", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not existed", HttpStatus.NOT_FOUND),
    TEACHER_NOT_EXISTED(1005, "Teacher not existed", HttpStatus.NOT_FOUND),
    USER_NOT_FOUND(1006, "User not found", HttpStatus.NOT_FOUND),
    UNAUTHENTICATED(1007, "Unauthenticated", HttpStatus.UNAUTHORIZED),
    USERNAME_INVALID(1003, "Username must be at least {min} characters long", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least {min} characters long", HttpStatus.BAD_REQUEST),
    ACCESS_DENIED(1005, "Access denied", HttpStatus.FORBIDDEN),
    STUDENT_NOT_EXISTED(1005, "Student not existed", HttpStatus.NOT_FOUND),
    LESSON_NOT_FOUND(1005, "Lesson not found", HttpStatus.NOT_FOUND),
    CLASS_NOT_FOUND(1010, "Class not existed", HttpStatus.NOT_FOUND),
    ASSIGNMENT_NOT_FOUND(1011, "Assignment not found", HttpStatus.NOT_FOUND),
    SUBMISSION_NOT_FOUND(1012, "Submission not found", HttpStatus.NOT_FOUND),
    FILE_NOT_FOUND(1012, "File not found", HttpStatus.NOT_FOUND),
    SEND_MESSAGE_FAILED(1013, "Send message failed", HttpStatus.FORBIDDEN),
    POST_NOT_FOUND(1014, "Post not found", HttpStatus.NOT_FOUND),
    COMMENT_NOT_FOUND(1015, "Comment not found", HttpStatus.NOT_FOUND),
    DELETE_FCMTOKEN_FAILED(1016, "Delete FCMtoken failed", HttpStatus.INTERNAL_SERVER_ERROR),;

    private int code;
    private String message;
    private HttpStatusCode httpStatusCode;

     ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

}
