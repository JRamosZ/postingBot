package com.smartecmx.postingbot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class GoogleException extends PostingBotException {

    public GoogleException(String message) {
        super(message);
    }
    
}