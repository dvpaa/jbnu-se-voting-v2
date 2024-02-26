package jbnu.se.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbnu.se.api.exception.CustomAuthenticationException;
import jbnu.se.api.exception.InvalidRequestException;
import jbnu.se.api.response.ErrorResponse;
import jbnu.se.api.util.JwtUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class OasisLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/login", "POST");
    private static final String USERNAME_PARAMETER = "username";
    private static final String PASSWORD_PARAMETER = "password";
    private static final boolean POST_ONLY = true;

    public OasisLoginFilter(JwtUtils jwtUtils, ObjectMapper objectMapper) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
    }

    public OasisLoginFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils, ObjectMapper objectMapper) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.jwtUtils = jwtUtils;
        this.objectMapper = objectMapper;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (POST_ONLY && !request.getMethod().equals("POST")) {
            throw new InvalidRequestException(request.getMethod());
        }

        String username = this.obtainUsername(request);
        username = username != null ? username.trim() : "";

        String password = this.obtainPassword(request);
        password = password != null ? password : "";

        Authentication requestAuthentication = OasisAuthenticationToken.unauthenticated(username, password);

        return this.getAuthenticationManager().authenticate(requestAuthentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserPrincipal userPrincipal = (UserPrincipal) authResult.getDetails();
        String token = jwtUtils.generateToken(userPrincipal);
        response.addHeader(AUTHORIZATION, "Bearer " + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        CustomAuthenticationException exception = (CustomAuthenticationException) failed;

        ErrorResponse errorResponse = ErrorResponse.builder()
                .code(String.valueOf(exception.getStatusCode()))
                .message(exception.getMessage())
                .build();

        response.setStatus(exception.getStatusCode());
        response.setContentType(APPLICATION_JSON_VALUE);
        response.setContentType("charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    @Nullable
    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(USERNAME_PARAMETER);
    }

    @Nullable
    protected String obtainPassword(HttpServletRequest request) {
        return request.getParameter(PASSWORD_PARAMETER);
    }
}
