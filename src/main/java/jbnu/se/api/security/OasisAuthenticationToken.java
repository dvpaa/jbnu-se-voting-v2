package jbnu.se.api.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Objects;

public class OasisAuthenticationToken extends AbstractAuthenticationToken {

    private final Object principal;

    private Object credentials;

    public OasisAuthenticationToken(Object principal, Object credentials) {
        super(null);
        this.principal = principal;
        this.credentials = credentials;
        this.setAuthenticated(false);
    }

    public OasisAuthenticationToken(Object principal, Object credentials, Object details, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        super.setDetails(details);
        super.setAuthenticated(true);
    }

    public static OasisAuthenticationToken unauthenticated(Object principal, Object credentials) {
        return new OasisAuthenticationToken(principal, credentials);
    }

    public static OasisAuthenticationToken authenticated(Object principal, Object credentials, Object details, Collection<? extends GrantedAuthority> authorities) {
        return new OasisAuthenticationToken(principal, credentials, details, authorities);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

    @Override
    public void eraseCredentials() {
        super.eraseCredentials();
        this.credentials = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        OasisAuthenticationToken that = (OasisAuthenticationToken) o;
        return Objects.equals(getPrincipal(), that.getPrincipal()) && Objects.equals(getCredentials(), that.getCredentials());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPrincipal(), getCredentials());
    }
}