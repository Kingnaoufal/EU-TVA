package com.euvatease.security;

import com.euvatease.entity.Shop;
import com.euvatease.repository.ShopRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final ShopRepository shopRepository;

    public JwtAuthenticationFilter(ShopRepository shopRepository) {
        this.shopRepository = shopRepository;
    }

    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.dev-mode:false}")
    private boolean devMode;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Get path relative to context path (removes /api prefix)
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        
        // DEV MODE: Inject a test shop for development
        if (devMode && isDevEndpoint(path)) {
            injectDevShop(request);
            filterChain.doFilter(request, response);
            return;
        }
        
        // Skip les endpoints publics
        if (isPublicEndpoint(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = authHeader.substring(7);
            Claims claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

            String shopDomain = claims.getSubject();
            
            Shop shop = shopRepository.findByShopifyDomain(shopDomain)
                .orElseThrow(() -> new RuntimeException("Boutique non trouvée"));

            // Vérifier que la boutique est active
            if (!Boolean.TRUE.equals(shop.getIsActive())) {
                throw new RuntimeException("Boutique désactivée");
            }

            // Ajouter la boutique à la requête
            request.setAttribute("shop", shop);

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                shopDomain,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_SHOP"))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            log.error("Erreur authentification JWT: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
    
    private void injectDevShop(HttpServletRequest request) {
        // Try to get existing dev shop from DB, or create and save one
        Shop shop = shopRepository.findByShopifyDomain("dev-shop.myshopify.com")
            .orElseGet(() -> {
                log.info("DEV MODE: Creating test shop in database");
                Shop devShop = new Shop();
                devShop.setShopifyDomain("dev-shop.myshopify.com");
                devShop.setShopName("Dev Test Shop");
                devShop.setEmail("dev@test.com");
                devShop.setCountryCode("FR");
                devShop.setCurrency("EUR");
                devShop.setOssRegistered(true);
                devShop.setSubscriptionStatus(Shop.SubscriptionStatus.ACTIVE);
                devShop.setSubscriptionPlan("monthly");
                devShop.setIsActive(true);
                devShop.setTrialEndsAt(LocalDateTime.now().plusDays(14));
                devShop.setCreatedAt(LocalDateTime.now());
                // Save to database to get a valid ID
                return shopRepository.save(devShop);
            });
        
        request.setAttribute("shop", shop);
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            shop.getShopifyDomain(),
            null,
            List.of(new SimpleGrantedAuthority("ROLE_SHOP"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        log.debug("DEV MODE: Injected test shop {}", shop.getShopifyDomain());
    }
    
    private boolean isDevEndpoint(String path) {
        return path.startsWith("/vat") ||
               path.startsWith("/vies") ||
               path.startsWith("/oss") ||
               path.startsWith("/orders");
    }

    private boolean isPublicEndpoint(String path) {
        return path.startsWith("/shopify/install") ||
               path.startsWith("/shopify/callback") ||
               path.startsWith("/shopify/auth") ||
               path.startsWith("/shopify/webhooks") ||
               path.startsWith("/billing/callback") ||
               path.startsWith("/health") ||
               path.startsWith("/actuator") ||
               path.startsWith("/landing") ||
               path.startsWith("/early-access");
    }
}
