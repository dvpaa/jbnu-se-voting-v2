package jbnu.se.api.security;

import java.util.Collections;
import java.util.List;
import jbnu.se.api.config.AuthProperties;
import jbnu.se.api.exception.FailureApiCallException;
import jbnu.se.api.exception.UnauthorizedException;
import jbnu.se.api.request.OasisApiRequest;
import jbnu.se.api.response.OasisApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.client.RestClient;

@RequiredArgsConstructor
public class OasisAuthenticationProvider implements AuthenticationProvider {

    private final AuthProperties authProperties;

    private final RestClient restClient;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();

        if (isAdmin(username, password)) {
            UserPrincipal adminPrincipal = makeAdminPrincipal();

            return OasisAuthenticationToken.authenticated(username, password, adminPrincipal,
                    getSingleAuthority("ROLE_ADMIN"));
        }

        OasisApiRequest oasisApiRequest = makeApiRequest(username, password);
        OasisApiResponse oasisApiResponse = callOasisApi(oasisApiRequest);

        if (!isSuccessfulApiCall(oasisApiResponse)) {
            throw new FailureApiCallException();
        }

        if (!isValidUser(oasisApiResponse)) {
            throw new UnauthorizedException();
        }

        UserPrincipal userPrincipal = makeUserInfo(oasisApiResponse);

        return OasisAuthenticationToken.authenticated(username, password, userPrincipal,
                getSingleAuthority("ROLE_USER"));
    }

    private boolean isValidUser(OasisApiResponse oasisApiResponse) {
        return !oasisApiResponse.getUsers().isEmpty();
    }

    private boolean isSuccessfulApiCall(OasisApiResponse oasisApiResponse) {
        return oasisApiResponse != null && oasisApiResponse.getUsers() != null;
    }

    private List<SimpleGrantedAuthority> getSingleAuthority(String role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }

    private UserPrincipal makeUserInfo(OasisApiResponse oasisApiResponse) {
        return UserPrincipal.builder()
                .userId(oasisApiResponse.getUsers().getFirst().getStudentId())
                .username(oasisApiResponse.getUsers().getFirst().getStudentName())
                .role("ROLE_USER")
                .build();
    }

    private OasisApiResponse callOasisApi(OasisApiRequest oasisApiRequest) {
        return restClient.post()
                .uri("")
                .body(oasisApiRequest)
                .retrieve()
                .body(OasisApiResponse.class);
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
