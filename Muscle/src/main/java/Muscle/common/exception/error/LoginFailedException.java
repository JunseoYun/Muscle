package Muscle.common.exception.error;

import Muscle.common.exception.ErrorCode;

public class LoginFailedException extends RuntimeException{
    public LoginFailedException(){
        super(ErrorCode.LOGIN_FAILED.getMessage());
    }
}
