package com.cuackstore.commons.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServicesException extends RuntimeException {
    private HttpStatus httpStatus;

    public ServicesException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ServicesException(String message, Throwable cause) {
        super(message,cause);
    }

}
