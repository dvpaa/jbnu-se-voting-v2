package jbnu.se.api.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jbnu.se.api.config.AuthProperties;
import jbnu.se.api.exception.TokenExpiredException;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

import static java.lang.System.currentTimeMillis;

@RequiredArgsConstructor
public class JwtUtil {

    private final AuthProperties authProperties;
    public static final String USERNAME_PARAMETER = "username";
    public static final String ROLE_PARAMETER = "role";

    public String generateToken(String userId, String username, String role) {
        return Jwts.builder()
                .subject(userId)
                .claim(USERNAME_PARAMETER, username)
                .claim(ROLE_PARAMETER, role)
                .issuedAt(new Date(currentTimeMillis()))
                .expiration(new Date(currentTimeMillis() + getExpirationTime()))
                .signWith(getSecretKey())
                .compact();
    }

    public String generateToken(UserPrincipal userPrincipal) {
        return Jwts.builder()
                .subject(userPrincipal.getUserId())
                .claim(USERNAME_PARAMETER, userPrincipal.getUsername())
                .claim(ROLE_PARAMETER, userPrincipal.getRole())
                .issuedAt(new Date(currentTimeMillis()))
                .expiration(new Date(currentTimeMillis() + getExpirationTime()))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * It's only for test code
     * @param userPrincipal  : test user principal
     * @param expirationTime : milliseconds
     * @return : JWT token
     */
    public String generateToken(UserPrincipal userPrincipal, Long expirationTime) {
        return Jwts.builder()
                .subject(userPrincipal.getUserId())
                .claim(USERNAME_PARAMETER, userPrincipal.getUsername())
                .claim(ROLE_PARAMETER, userPrincipal.getRole())
                .issuedAt(new Date(currentTimeMillis()))
                .expiration(new Date(currentTimeMillis() + expirationTime))
                .signWith(getSecretKey())
                .compact();
    }

    public String getUserIdFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get(USERNAME_PARAMETER, String.class));
    }

    public String getUserRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get(ROLE_PARAMETER, String.class));
    }

    public Boolean isTokenExpired(String token) {
        try {
            final Date expiration = getAllClaimsFromToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            return claimsResolver.apply(claims);
        } catch (ExpiredJwtException e) {
            throw new TokenExpiredException();
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        return authProperties.getJwtInfo().getSecretKey();
    }

    private Long getExpirationTime() {
        return authProperties.getJwtInfo().getExpirationTime();
    }
}
