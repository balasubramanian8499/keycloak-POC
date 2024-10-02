package com.example.keycloak.keycloakconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(accessManagement -> accessManagement
                .requestMatchers("/api/getUserTokens", "/getAdminTokens", "/api/logout").permitAll()
                .requestMatchers("/api/getUserTokensFromRefreshToken", "/api/userSignup").permitAll()
                .requestMatchers("/swagger-resources/**", "/v2/api-docs", "/v3/api-docs/**", "/webjars/**", "/swagger-ui/**").permitAll()
                .anyRequest().authenticated()
        );
        http.oauth2ResourceServer(oauth2 -> oauth2.jwt((Customizer.withDefaults())));
        http.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

}