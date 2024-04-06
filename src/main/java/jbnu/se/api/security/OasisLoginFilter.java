package jbnu.se.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbnu.se.api.exception.InvalidRequestException;
import jbnu.se.api.util.JwtUtils;
import org.springframework.lang.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class OasisLoginFilter extends AbstractAuthenticationProcessingFilter {

    private final JwtUtils jwtUtils;
    private final ExceptionResponseHandler exceptionResponseHandler;

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/login", "POST");
    private static final String USERNAME_PARAMETER = "username";
    private static final String PASSWORD_PARAMETER = "password";
    private static final boolean POST_ONLY = true;

    public OasisLoginFilter(JwtUtils jwtUtils, ExceptionResponseHandler exceptionResponseHandler) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.jwtUtils = jwtUtils;
        this.exceptionResponseHandler = exceptionResponseHandler;
    }

    public OasisLoginFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils, ExceptionResponseHandler exceptionResponseHandler) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.jwtUtils = jwtUtils;
        this.exceptionResponseHandler = exceptionResponseHandler;
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
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
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        UserPrincipal userPrincipal = (UserPrincipal) authResult.getDetails();
        String token = jwtUtils.generateToken(userPrincipal);
        response.addHeader(AUTHORIZATION, "Bearer " + token);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        exceptionResponseHandler.configErrorResponse(response, failed);
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
