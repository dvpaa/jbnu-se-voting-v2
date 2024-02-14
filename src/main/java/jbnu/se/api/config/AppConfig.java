package jbnu.se.api.config;

import jbnu.se.api.util.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@Configuration
public class AppConfig {

    @Bean
    public AuthProperties authProperties() {
        return new AuthProperties();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .baseUrl(authProperties().getOasisApi().getUrl())
                .defaultHeader(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils(authProperties());
    }
}
