package jbnu.se.api.config;

import jbnu.se.api.util.JwtUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static jbnu.se.api.config.SampleAuthInfo.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JwtUserRequestPostProcessor implements RequestPostProcessor {
    private final JwtUtils jwtUtils;

    public JwtUserRequestPostProcessor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
        String token = this.jwtUtils.generateToken(USER_ID, USER_NAME, USER_ROLE);
        request.addHeader(AUTHORIZATION, "Bearer " + token);
        return request;
    }

    public static JwtUserRequestPostProcessor jwtUser(JwtUtils jwtUtils) {
        return new JwtUserRequestPostProcessor(jwtUtils);
    }
}
