package jbnu.se.api.config;

import jbnu.se.api.util.JwtUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import static jbnu.se.api.config.SampleAuthInfo.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class JwtAdminRequestPostProcessor implements RequestPostProcessor {

    private final JwtUtils jwtUtils;

    public JwtAdminRequestPostProcessor(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
        String token = this.jwtUtils.generateToken(ADMIN_ID, ADMIN_NAME, ADMIN_ROLE);
        request.addHeader(AUTHORIZATION, "Bearer " + token);
        return request;
    }

    public static JwtAdminRequestPostProcessor jwtAdmin(JwtUtils jwtUtils) {
        return new JwtAdminRequestPostProcessor(jwtUtils);
    }
}
