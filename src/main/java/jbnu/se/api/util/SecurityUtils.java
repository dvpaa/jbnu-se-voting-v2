package jbnu.se.api.util;

import jbnu.se.api.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    private SecurityUtils() {

    }
    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private static UserPrincipal getUserPrincipal() {
        return (UserPrincipal) getAuthentication().getDetails();
    }

    public static String getUserId() {
        return getUserPrincipal().getUserId();
    }

    public static String getUsername() {
        return getUserPrincipal().getUsername();
    }

    public static String getRole() {
        return getUserPrincipal().getRole();
    }
}
