package com.cuackstore.commons;

import com.cuackstore.commons.dto.ApiResponseDTO;
import com.cuackstore.commons.exceptions.BusinessException;
import com.cuackstore.commons.exceptions.ServicesException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ApiExceptionHandler {

    @ExceptionHandler(ServicesException.class)
    public ResponseEntity<Object> handleBusinessException(ServicesException ex) {
        log.info("ServicesException {} {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponseDTO.handleBuild(null, ex.getMessage()), ex.getHttpStatus());
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleBusinessException(BusinessException ex) {
        log.info("BusinessException {} {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponseDTO.handleBuild(null, ex.getMessage()), ex.getHttpStatus());
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleException(Exception ex){
        log.error("Exception {}", ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(ApiResponseDTO.handleBuild(null, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Object> handleException(RuntimeException ex){
        log.error("RuntimeException {}", ex.getMessage());
        ex.printStackTrace();
        return new ResponseEntity<>(ApiResponseDTO.handleBuild(null, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
