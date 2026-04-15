package Muscle.common.exception;

import Muscle.common.dto.ResponseMessage;
import Muscle.common.exception.error.ExistingEmailException;
import Muscle.common.exception.error.ExistingNicknameException;
import Muscle.common.exception.error.RegisterFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RegisterFailedException.class)
    protected ResponseEntity<ResponseMessage> handleRegisterFailedException(RegisterFailedException e) {
        ErrorCode code = ErrorCode.AUTHENTICATION_CONFLICT;

        ResponseMessage response = ResponseMessage.builder()
                .message(code.getMessage())
                .data(code.getCode())
                .build();
        return new ResponseEntity<>(response, code.getHttpStatus());
    }

    @ExceptionHandler(ExistingEmailException.class)
    protected ResponseEntity<ResponseMessage> handleExistingEmailException(ExistingEmailException e) {
        ErrorCode code = ErrorCode.EXISTING_EMAIL;

        ResponseMessage response = ResponseMessage.builder()
                .message(code.getMessage())
                .data(code.getCode())
                .build();
        return new ResponseEntity<>(response, code.getHttpStatus());
    }

    @ExceptionHandler(ExistingNicknameException.class)
    protected ResponseEntity<ResponseMessage> handleExistingNicknameException(ExistingNicknameException e){
        ErrorCode code = ErrorCode.EXISTING_NICKNAME;

        ResponseMessage response = ResponseMessage.builder()
                .message(code.getMessage())
                .data(code.getCode())
                .build();
        return new ResponseEntity<>(response, code.getHttpStatus());
    }
}
