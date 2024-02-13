package jbnu.se.api.security;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import jbnu.se.api.config.AppConfig;
import jbnu.se.api.config.AuthProperties;
import jbnu.se.api.exception.TokenExpiredException;
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
    private JwtUtil jwtUtil;

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
        String secretString = "fYDvRN4fpGM5fOES3e/V3sY7uiKJu97HXVd9u9I0RgM=";
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId("id")
                .username("name")
                .role("role")
                .build();

        // when
        String token = jwtUtil.generateToken(userPrincipal, 10000L);
        String userIdFromToken = jwtUtil.getUserIdFromToken(token);
        String usernameFromToken = jwtUtil.getUsernameFromToken(token);
        String roleFromToken = jwtUtil.getUserRoleFromToken(token);

        // then
        assertThat(userIdFromToken).isEqualTo(userPrincipal.getUserId());
        assertThat(usernameFromToken).isEqualTo(userPrincipal.getUsername());
        assertThat(roleFromToken).isEqualTo(userPrincipal.getRole());
    }

    @Test
    @DisplayName("만료된 토큰은 사용할 수 없다.")
    void tokenExpiredTest() {
        //given
        String secretString = "fYDvRN4fpGM5fOES3e/V3sY7uiKJu97HXVd9u9I0RgM=";
        SecretKey secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
        UserPrincipal userPrincipal = UserPrincipal.builder()
                .userId("id")
                .username("name")
                .role("role")
                .build();

        String token = jwtUtil.generateToken(userPrincipal, -1000L);

        // when
        Boolean tokenExpired = jwtUtil.isTokenExpired(token);

        // then
        assertThat(tokenExpired).isTrue();

        // expected
        assertThatThrownBy(() -> jwtUtil.getUserIdFromToken(token))
                .isInstanceOf(TokenExpiredException.class);
    }
}