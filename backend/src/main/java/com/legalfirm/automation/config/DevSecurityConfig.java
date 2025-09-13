package com.legalfirm.automation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Development-only security configuration that bypasses authentication
 * WARNING: Only use this for local development!
 */
@Configuration
@EnableWebSecurity
@Profile("dev-no-auth") // Only active when this profile is enabled
public class DevSecurityConfig {

    @Bean
    public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll() // Allow all requests without authentication
            )
            .headers(headers -> headers
                .frameOptions().disable() // Allow H2 console if using H2 database
            );

        return http.build();
    }
}