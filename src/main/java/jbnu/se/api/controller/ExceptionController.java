package jbnu.se.api.controller;

import jakarta.servlet.http.HttpServletResponse;
import jbnu.se.api.exception.ApplicationException;
import jbnu.se.api.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler(ApplicationException.class)
    public ErrorResponse applicationException(HttpServletResponse response, ApplicationException e) {
        response.setStatus(e.getStatusCode());

        return ErrorResponse.builder()
                .code(String.valueOf(e.getStatusCode()))
                .message(e.getMessage())
                .validation(e.getValidation())
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse exception(HttpServletResponse response, Exception e) {

        log.error("예외 발생", e);

        response.setStatus(500);

        return ErrorResponse.builder()
                .code("500")
                .message(e.getMessage())
                .build();
    }
}
