package jbnu.se.api.config;

import java.util.Collections;
import jbnu.se.api.security.OasisAuthenticationToken;
import jbnu.se.api.security.UserPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public class MockSecurity {

    public static final String USER_ID = "201917696";
    public static final String USER_NAME = "김건우";
    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";

    public static OasisAuthenticationToken makeUserToken() {
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId(USER_ID)
                .username(USER_NAME)
                .role(ROLE_USER)
                .build();

        return new OasisAuthenticationToken(
                "test",
                null,
                userPrincipal,
                Collections.singleton(new SimpleGrantedAuthority(ROLE_USER))
        );
    }

    public static OasisAuthenticationToken makeAdminToken() {
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId(USER_ID)
                .username(USER_NAME)
                .role(ROLE_ADMIN)
                .build();

        return new OasisAuthenticationToken(
                "test",
                null,
                userPrincipal,
                Collections.singleton(new SimpleGrantedAuthority(ROLE_ADMIN))
        );
    }
}
