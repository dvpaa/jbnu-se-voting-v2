package jbnu.se.api.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import jbnu.se.api.config.AuthProperties;
import jbnu.se.api.exception.InvalidTokenException;
import jbnu.se.api.security.UserPrincipal;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

import static java.lang.System.currentTimeMillis;

@RequiredArgsConstructor
public class JwtUtils {

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
     * @param secretKey      : test secret key
     * @param expirationTime : milliseconds
     * @return : JWT token
     */
    public String generateToken(UserPrincipal userPrincipal, SecretKey secretKey, Long expirationTime) {
        return Jwts.builder()
                .subject(userPrincipal.getUserId())
                .claim(USERNAME_PARAMETER, userPrincipal.getUsername())
                .claim(ROLE_PARAMETER, userPrincipal.getRole())
                .issuedAt(new Date(currentTimeMillis()))
                .expiration(new Date(currentTimeMillis() + expirationTime))
                .signWith(secretKey)
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

    public boolean isValidToken(String token) {
        try {
            final Date expiration = getAllClaimsFromToken(token).getExpiration();
            return !isExpired(expiration);
        } catch (InvalidTokenException e) {  // secret or expired
            return false;
        }
    }

    private boolean isExpired(Date expiration) {
        return expiration.before(new Date());
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        try {
            final Claims claims = getAllClaimsFromToken(token);
            return claimsResolver.apply(claims);
        } catch (JwtException e) {  // secret or expired
            throw new InvalidTokenException();
        }
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {  // secret key
            throw new InvalidTokenException();
        }
    }

    private SecretKey getSecretKey() {
        return authProperties.getJwtInfo().getSecretKey();
    }

    private Long getExpirationTime() {
        return authProperties.getJwtInfo().getExpirationTime();
    }
}
