package jbnu.se.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbnu.se.api.exception.InvalidRequestException;
import jbnu.se.api.exception.UnauthorizedException;
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

    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/login", "POST");
    private static final String USERNAME_PARAMETER = "username";
    private static final String PASSWORD_PARAMETER = "password";
    private static final boolean POST_ONLY = true;

    public OasisLoginFilter(JwtUtils jwtUtils) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.jwtUtils = jwtUtils;
    }

    public OasisLoginFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.jwtUtils = jwtUtils;
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
        throw new UnauthorizedException();
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
