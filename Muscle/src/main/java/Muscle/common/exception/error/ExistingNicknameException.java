package Muscle.common.exception.error;

import Muscle.common.exception.ErrorCode;

public class ExistingNicknameException extends RuntimeException{
    public ExistingNicknameException(){
        super(ErrorCode.EXISTING_NICKNAME.getMessage());
    }
}
