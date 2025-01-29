package jbnu.se.api.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;
import static org.springframework.http.HttpMethod.POST;

import jbnu.se.api.security.ExceptionResponseHandler;
import jbnu.se.api.security.JwtFilter;
import jbnu.se.api.security.OasisAccessDeniedHandler;
import jbnu.se.api.security.OasisAuthenticationProvider;
import jbnu.se.api.security.OasisLoginFilter;
import jbnu.se.api.util.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestClient;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthProperties authProperties;

    private final RestClient restClient;

    private final AuthenticationConfiguration authenticationConfiguration;

    private final JwtUtils jwtUtils;

    private final OasisAccessDeniedHandler oasisAccessDeniedHandler;

    private final ExceptionResponseHandler exceptionResponseHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/favicon.ico")
                .requestMatchers("/error")
                .requestMatchers(toH2Console());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .addFilterAt(oasisLoginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter(), OasisLoginFilter.class)

                .authenticationProvider(authenticationProvider())

                .authorizeHttpRequests(request -> request
                        .requestMatchers(POST, "/api/login").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())

                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .accessDeniedHandler(oasisAccessDeniedHandler));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        return new OasisAuthenticationProvider(authProperties, restClient);
    }

    @Bean
    public OasisLoginFilter oasisLoginFilter() throws Exception {
        return new OasisLoginFilter(authenticationConfiguration.getAuthenticationManager(), jwtUtils, exceptionResponseHandler);
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtils);
    }
}
