package jbnu.se.api.controller;

import jakarta.servlet.http.HttpServletResponse;
import jbnu.se.api.exception.ApplicationException;
import jbnu.se.api.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse invalidRequestHandler(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(String.valueOf(BAD_REQUEST.value()))
                .message("잘못된 요청입니다.")
                .build();

        for (FieldError fieldError : e.getFieldErrors()) {
            errorResponse.addValidation(fieldError.getField(), fieldError.getDefaultMessage());
        }

        return errorResponse;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(BAD_REQUEST)
    public ErrorResponse httpMessageNotReadableException(HttpMessageNotReadableException e) {
        return ErrorResponse.builder()
                .code(String.valueOf(BAD_REQUEST.value()))
                .message("잘못된 형식의 데이터 입니다.")
                .build();
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse exception(HttpServletResponse response, Exception e) {
        log.error("예외 발생", e);

        response.setStatus(500);

        return ErrorResponse.builder()
                .code("500")
                .message("잘못된 형식의 데이터 입니다.")
                .build();
    }
}
