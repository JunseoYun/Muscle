package Muscle.common.exception.error;

import Muscle.common.exception.ErrorCode;

public class ExistingEmailException extends RuntimeException{
    public ExistingEmailException(){
        super(ErrorCode.EXISTING_EMAIL.getMessage());
    }
}
