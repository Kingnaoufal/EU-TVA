package com.euvatease.config;

import com.euvatease.security.JwtAuthenticationFilter;
import com.euvatease.security.ShopifyHmacFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ShopifyHmacFilter shopifyHmacFilter;
    
    @Value("${app.dev-mode:false}")
    private boolean devMode;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ShopifyHmacFilter shopifyHmacFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.shopifyHmacFilter = shopifyHmacFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        
        if (devMode) {
            // DEV MODE: Allow all requests but still use JWT filter to inject dev shop
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        } else {
            // PRODUCTION MODE: Require authentication for most endpoints
            http.authorizeHttpRequests(auth -> auth
                    // Endpoints publics
                    .requestMatchers("/shopify/install", "/shopify/callback", "/shopify/auth").permitAll()
                    .requestMatchers("/shopify/webhooks/**").permitAll()
                    .requestMatchers("/billing/callback").permitAll()
                    .requestMatchers("/health", "/actuator/**").permitAll()
                    .requestMatchers("/landing/**", "/early-access/**").permitAll()
                    // Tous les autres endpoints nécessitent une authentification
                    .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(shopifyHmacFilter, JwtAuthenticationFilter.class);
        }

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(
            "https://app.euvatease.com",
            "https://euvatease.com",
            "http://localhost:3000",
            "http://localhost:5173"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
