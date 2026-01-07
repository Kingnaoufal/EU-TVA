package com.euvatease.config;

import com.euvatease.security.JwtAuthenticationFilter;
import com.euvatease.security.ShopifyHmacFilter;
import jakarta.annotation.Nonnull;
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
import java.util.Objects;

/**
 * Configuration de sécurité Spring Security pour l'application EU VAT Ease.
 * Gère l'authentification JWT, les filtres HMAC Shopify et la configuration CORS.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Nonnull
    private final ShopifyHmacFilter shopifyHmacFilter;

    @Value("${app.dev-mode:false}")
    private boolean devMode;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructeur avec injection des filtres de sécurité.
     *
     * @param jwtAuthenticationFilter le filtre d'authentification JWT (non null)
     * @param shopifyHmacFilter       le filtre de vérification HMAC Shopify (non null)
     */
    public SecurityConfig(@Nonnull JwtAuthenticationFilter jwtAuthenticationFilter,
                          @Nonnull ShopifyHmacFilter shopifyHmacFilter) {
        this.jwtAuthenticationFilter = Objects.requireNonNull(jwtAuthenticationFilter,
                "jwtAuthenticationFilter must not be null");
        this.shopifyHmacFilter = Objects.requireNonNull(shopifyHmacFilter,
                "shopifyHmacFilter must not be null");
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Configure la chaîne de filtres de sécurité.
     *
     * @param http la configuration HttpSecurity
     * @return la chaîne de filtres configurée
     * @throws Exception en cas d'erreur de configuration
     */
    @Bean
    @Nonnull
    public SecurityFilterChain filterChain(@Nonnull HttpSecurity http) throws Exception {
        Objects.requireNonNull(http, "http must not be null");

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

    /**
     * Configure les règles CORS pour l'application.
     *
     * @return la source de configuration CORS
     */
    @Bean
    @Nonnull
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
