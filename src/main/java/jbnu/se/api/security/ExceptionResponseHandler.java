package jbnu.se.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import jbnu.se.api.exception.CustomAuthenticationException;
import jbnu.se.api.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
public class ExceptionResponseHandler {

    private final ObjectMapper objectMapper;

    public void configErrorResponse(HttpServletResponse response, RuntimeException exception) throws IOException {
        if (exception instanceof AccessDeniedException) {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .code(String.valueOf(FORBIDDEN.value()))
                    .message(exception.getMessage())
                    .build();

            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(UTF_8.name());
            response.setStatus(FORBIDDEN.value());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            response.getWriter().flush();
        } else if (exception instanceof CustomAuthenticationException failed) {

            ErrorResponse errorResponse = ErrorResponse.builder()
                    .code(String.valueOf(failed.getStatusCode()))
                    .message(failed.getMessage())
                    .build();

            response.setStatus(failed.getStatusCode());
            response.setContentType(APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(UTF_8.name());
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            response.getWriter().flush();
        }
    }
}
