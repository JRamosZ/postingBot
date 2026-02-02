package com.smartecmx.postingbot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class MetaException extends PostingBotException {

    public MetaException(String message) {
        super(message);
    }
    
}
