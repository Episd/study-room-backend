package com.nbucs.studyroombackend.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final Integer code;
    private final String message;
    public ServiceException(Integer code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }
}
