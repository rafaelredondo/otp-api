package com.otp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Configuração básica
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/otp/**").permitAll()
                .requestMatchers("/h2-console/**").denyAll() // Bloqueia acesso ao console H2 em produção
                .anyRequest().authenticated()
            )
            
            // Proteção CSRF
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringRequestMatchers("/api/otp/**") // OTP endpoints são stateless
            )
            
            // Headers de Segurança
            .headers(headers -> headers
                .defaultsDisabled()
                .frameOptions(frame -> frame.deny())
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'")
                )
                .httpStrictTransportSecurity(hsts -> hsts
                    .includeSubDomains(true)
                    .maxAgeInSeconds(31536000)
                )
                .cacheControl(cache -> cache.disable())
            )
            
            // Rate Limiting será implementado via bucket4j
            
            // Desabilita sessão já que é uma API stateless
            .sessionManagement(session -> session
                .disable()
            );

        return http.build();
    }
} 