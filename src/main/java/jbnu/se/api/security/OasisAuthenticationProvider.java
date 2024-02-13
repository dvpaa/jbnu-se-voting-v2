package jbnu.se.api.security;

import jbnu.se.api.config.AuthProperties;
import jbnu.se.api.request.OasisApiRequest;
import jbnu.se.api.response.OasisApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class OasisAuthenticationProvider implements AuthenticationProvider {

    private final AuthProperties authProperties;

    private final WebClient webClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        if (isAdmin(username, password)) {
            UserPrincipal adminPrincipal = makeAdminPrincipal();

            return OasisAuthenticationToken.authenticated(username, password, adminPrincipal, getSingleAuthority("ROLE_ADMIN"));
        }

        OasisApiRequest oasisApiRequest = makeApiRequest(username, password);
        OasisApiResponse oasisApiResponse = callOasisApi(oasisApiRequest);

        if (oasisApiResponse == null || oasisApiResponse.getUsers() == null) {
            throw new RuntimeException("api 호출 실패");
        }

        if (oasisApiResponse.getUsers().isEmpty()) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다.");
        }

        if (!oasisApiResponse.getUsers().get(0).getStudentId().equals(username)) {
            throw new BadCredentialsException("인증실패");
        }

        UserPrincipal userPrincipal = makeUserInfo(oasisApiResponse);

        return OasisAuthenticationToken.authenticated(username, password, userPrincipal, getSingleAuthority("ROLE_USER"));
    }

    private List<SimpleGrantedAuthority> getSingleAuthority(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    private UserPrincipal makeUserInfo(OasisApiResponse oasisApiResponse) {
        return UserPrincipal.builder()
                .userId(oasisApiResponse.getUsers().get(0).getStudentId())
                .username(oasisApiResponse.getUsers().get(0).getStudentName())
                .role("ROLE_USER")
                .build();
    }

    private OasisApiResponse callOasisApi(OasisApiRequest oasisApiRequest) {
        return webClient.post()
                .body(Mono.just(oasisApiRequest), OasisApiRequest.class)
                .retrieve()
                .bodyToMono(OasisApiResponse.class)
                .block();
    }

    private OasisApiRequest makeApiRequest(String username, String password) {
        return OasisApiRequest.builder()
                .userNo(username)
                .userPwd(password)
                .build();
    }

    private UserPrincipal makeAdminPrincipal() {
        return UserPrincipal.builder()
                .userId(authProperties.getAdmin().getId())
                .username(authProperties.getAdmin().getName())
                .role("ROLE_ADMIN")
                .build();
    }

    private boolean isAdmin(String username, String password) {
        return authProperties.getAdmin().isAdmin(username, password);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OasisAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
