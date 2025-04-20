package com._NguoiDev.SkillBridge.exception;

import com._NguoiDev.SkillBridge.dto.response.ApiResponse;
import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final String MIN_ATTRIBUTE = "min";
    private static final String MAX_ATTRIBUTE = "max";

    @ExceptionHandler(value = AppException.class)
    public ResponseEntity<ApiResponse> HandlingRunTimeException(AppException e){
        ErrorCode errorCode = e.getErrorCode();
        ApiResponse<String> apiReponse = new ApiResponse<String>();
        apiReponse.setMessage(errorCode.getMessage());
        apiReponse.setCode(errorCode.getCode());
        return ResponseEntity.
                status(errorCode.getHttpStatusCode())
                .body(apiReponse);
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> HandlingMethodArgumentNotValidException(MethodArgumentNotValidException e){
        String enum_key = Objects.requireNonNull(e.getFieldError()).getDefaultMessage();
        System.out.println(e.getFieldError().getDefaultMessage());
        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        Map attribute = null;
        try{
            errorCode = ErrorCode.valueOf(enum_key);

            ConstraintViolation constraintViolation= e.getBindingResult()
                                                        .getAllErrors()
                                                        .getFirst()
                                                        .unwrap(ConstraintViolation.class);
            attribute =  constraintViolation.getConstraintDescriptor().getAttributes();
            log.info(attribute.toString());
        }catch (IllegalArgumentException ex){

        }

        ApiResponse apiReponse = new ApiResponse();
        apiReponse.setMessage(Objects.nonNull(attribute)? MapAttribute(errorCode.getMessage(), attribute): errorCode.getMessage());
        apiReponse.setCode(errorCode.getCode());
        return ResponseEntity.
                status(errorCode.getHttpStatusCode())
                .body(apiReponse);
    }

    private String MapAttribute(String message, Map<String, Object> attribute) {
        String minValue = String.valueOf(attribute.get(MIN_ATTRIBUTE));
        String maxValue = String.valueOf(attribute.get(MAX_ATTRIBUTE));
        if (!minValue.equals("null")) message = message.replace("{"+ MIN_ATTRIBUTE + "}", minValue);
        if (!maxValue.equals("null")) message = message.replace("{"+ MAX_ATTRIBUTE + "}", maxValue);
        return message;
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse> HandlingAccessDeniedException(AccessDeniedException e){
        ErrorCode errorCode = ErrorCode.ACCESS_DENIED;
        ApiResponse apiReponse = ApiResponse
                .builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(apiReponse);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ApiResponse> HandlingException(Exception e){
        e.printStackTrace();
        ApiResponse<String> apiReponse = new ApiResponse<String>();
        apiReponse.setMessage("lỗi : "+e.getMessage());
        ErrorCode errorCode = ErrorCode.UNKNOWN_ERROR;
        apiReponse.setCode(errorCode.getCode());
        return ResponseEntity.
                status(errorCode.getHttpStatusCode())
                .body(apiReponse);
    }



}
