package com.cuackstore.commons.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ServicesException extends RuntimeException {
    private HttpStatus httpStatus;
    private String details;

    public ServicesException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ServicesException(String message, String details, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.details = details;
    }

}
