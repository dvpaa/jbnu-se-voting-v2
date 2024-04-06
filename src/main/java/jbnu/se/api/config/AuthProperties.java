package jbnu.se.api.config;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;

@ConfigurationProperties(prefix = "auth")
@Getter
@Setter
public class AuthProperties {

    private OasisApi oasisApi;

    private JwtInfo jwtInfo;

    private Admin admin;

    @Getter
    @Setter
    public static class OasisApi {

        private String url;
    }

    @Getter
    public static class JwtInfo {

        private SecretKey secretKey;

        @Setter
        private Long expirationTime;

        public void setSecretKey(String secretKey) {
            this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        }
    }

    @Getter
    @Setter
    public static class Admin {

        private String id;

        private String password;

        private String name;

        public boolean isAdmin(String id, String password) {
            return (this.id.equals(id) && this.password.equals(password));
        }
    }
}
