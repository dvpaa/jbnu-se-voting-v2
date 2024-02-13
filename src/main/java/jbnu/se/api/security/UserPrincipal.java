package jbnu.se.api.security;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Getter
@Setter
public class UserPrincipal {

    private String userId;

    private String username;

    private String role;

    @Builder
    public UserPrincipal(String userId, String username, String role) {
        this.userId = userId;
        this.username = username;
        this.role = role;
    }

    public Collection<SimpleGrantedAuthority> getAuthority() {
        return Collections.singletonList(new SimpleGrantedAuthority(this.role));
    }
}

