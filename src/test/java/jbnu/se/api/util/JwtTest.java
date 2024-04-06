package jbnu.se.api.util;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jbnu.se.api.config.AppConfig;
import jbnu.se.api.config.AuthProperties;
import jbnu.se.api.exception.InvalidTokenException;
import jbnu.se.api.security.UserPrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ContextConfiguration(classes = AppConfig.class)
@EnableConfigurationProperties(AuthProperties.class)
class JwtTest {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthProperties authProperties;

    @Test
    @DisplayName("시크릿 키 암호와 및 복호와는 정상적으로 작동한다.")
    void secreteKeyTest() {
        // given
        String secretString = "fYDvRN4fpGM5fOES3e/V3sY7uiKJu97HXVd9u9I0RgM=";

        // when
        SecretKey decodedKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
        String encodedKey = Encoders.BASE64.encode(decodedKey.getEncoded());

        // then
        assertThat(encodedKey).isEqualTo(secretString);
    }

    @Test
    @DisplayName("토큰에 저장된 값을 꺼내올 수 있다.")
    void getClaimFromToken() {
        // given
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId("id")
                .username("name")
                .role("role")
                .build();

        // when
        String token = jwtUtils.generateToken(userPrincipal, authProperties.getJwtInfo().getSecretKey(), 10000L);
        String userIdFromToken = jwtUtils.getUserIdFromToken(token);
        String usernameFromToken = jwtUtils.getUsernameFromToken(token);
        String roleFromToken = jwtUtils.getUserRoleFromToken(token);

        // then
        assertThat(userIdFromToken).isEqualTo(userPrincipal.getUserId());
        assertThat(usernameFromToken).isEqualTo(userPrincipal.getUsername());
        assertThat(roleFromToken).isEqualTo(userPrincipal.getRole());
    }

    @Test
    @DisplayName("만료된 토큰은 사용할 수 없다.")
    void tokenExpiredTest() {
        //given
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId("id")
                .username("name")
                .role("role")
                .build();

        String token = jwtUtils.generateToken(userPrincipal, authProperties.getJwtInfo().getSecretKey(), -1000L);

        // when
        boolean validToken = jwtUtils.isValidToken(token);

        // then
        assertThat(validToken).isFalse();
    }

    @Test
    @DisplayName("만료된 토큰을 사용하려 하면 에러를 반환한다.")
    void expiredTokenUsingTest() {
        //given
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId("id")
                .username("name")
                .role("role")
                .build();

        String token = jwtUtils.generateToken(userPrincipal, authProperties.getJwtInfo().getSecretKey(), -1000L);

        // expected
        assertThatThrownBy(() -> jwtUtils.getUserIdFromToken(token))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    @DisplayName("시크릿키가 다르면 에러를 반환한다")
    void inValidSecretKeyTest() {
        // given
        String secretString = "fYDvRN4fpGM5fOES3e/V3sY7uiKJu97HXVd9u9I0RgM=";
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));

        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId("id")
                .username("name")
                .role("role")
                .build();

        String token = jwtUtils.generateToken(userPrincipal, secretKey, 10000L);

        // expected
        assertThatThrownBy(() -> jwtUtils.getUserIdFromToken(token))
                .isInstanceOf(InvalidTokenException.class);
    }
}