package com.smartecmx.postingbot.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class NotFoundException extends PostingBotException{
    
    public NotFoundException(String message) {
        super(message);
    }

}
