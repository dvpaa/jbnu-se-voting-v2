package jbnu.se.api.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jbnu.se.api.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION);

        if (!isHeaderContainToken(authorization)) {
            filterChain.doFilter(request, response);

            return;
        }

        String token = authorization.split(" ")[1];

        if (isInvalidToken(token)) {
            filterChain.doFilter(request, response);

            return;
        }

        UserPrincipal userPrincipal = getUserPrincipalFromToken(token);

        OasisAuthenticationToken authenticated = OasisAuthenticationToken.authenticated(userPrincipal.getUserId(), null, userPrincipal, userPrincipal.getAuthority());

        SecurityContextHolder.getContext().setAuthentication(authenticated);

        filterChain.doFilter(request, response);
    }

    private boolean isHeaderContainToken(String authorization) {
        return authorization != null && authorization.startsWith("Bearer ");
    }

    private boolean isInvalidToken(String token) {
        return token == null || jwtUtils.isValidToken(token);
    }

    private UserPrincipal getUserPrincipalFromToken(String token) {
        String userId = jwtUtils.getUserIdFromToken(token);
        String username = jwtUtils.getUsernameFromToken(token);
        String role = jwtUtils.getUserRoleFromToken(token);

        return UserPrincipal.builder()
                .userId(userId)
                .username(username)
                .role(role)
                .build();

    }
}
