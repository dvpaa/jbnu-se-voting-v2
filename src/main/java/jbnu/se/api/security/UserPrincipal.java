package jbnu.se.api.security;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
}

