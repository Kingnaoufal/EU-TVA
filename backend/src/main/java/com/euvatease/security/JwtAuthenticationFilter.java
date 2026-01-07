package com.euvatease.security;

import com.euvatease.entity.Shop;
import com.euvatease.repository.ShopRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
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
import java.util.Objects;

/**
 * Filtre d'authentification JWT pour valider les tokens et établir le contexte de sécurité.
 * Gère également l'injection d'une boutique de test en mode développement.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ----------------------------------------------------------------------------------------------------------------

    private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String DEV_SHOP_DOMAIN = "dev-shop.myshopify.com";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = 7;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    private final ShopRepository shopRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${app.dev-mode:false}")
    private boolean devMode;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructeur avec injection du repository de boutiques.
     *
     * @param shopRepository le repository pour accéder aux boutiques (non null)
     */
    public JwtAuthenticationFilter(@Nonnull ShopRepository shopRepository) {
        this.shopRepository = Objects.requireNonNull(shopRepository, "shopRepository must not be null");
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(response, "response must not be null");
        Objects.requireNonNull(filterChain, "filterChain must not be null");

        String path = extractPath(request);

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

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        processJwtAuthentication(request, authHeader);
        filterChain.doFilter(request, response);
    }

    /**
     * Extrait le chemin de la requête en retirant le context path.
     *
     * @param request la requête HTTP
     * @return le chemin relatif
     */
    @Nonnull
    private String extractPath(@Nonnull HttpServletRequest request) {
        String path = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }
        return path;
    }

    /**
     * Traite l'authentification JWT à partir du header Authorization.
     *
     * @param request    la requête HTTP
     * @param authHeader le header d'autorisation
     */
    private void processJwtAuthentication(@Nonnull HttpServletRequest request,
                                          @Nonnull String authHeader) {
        try {
            String token = authHeader.substring(BEARER_PREFIX_LENGTH);
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
            LOG.error("Erreur authentification JWT: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Injecte une boutique de test pour le mode développement.
     *
     * @param request la requête HTTP
     */
    private void injectDevShop(@Nonnull HttpServletRequest request) {
        Shop shop = shopRepository.findByShopifyDomain(DEV_SHOP_DOMAIN)
            .orElseGet(this::createDevShop);

        request.setAttribute("shop", shop);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            shop.getShopifyDomain(),
            null,
            List.of(new SimpleGrantedAuthority("ROLE_SHOP"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        LOG.debug("DEV MODE: Injected test shop {}", shop.getShopifyDomain());
    }

    /**
     * Crée une nouvelle boutique de développement.
     *
     * @return la boutique créée et sauvegardée
     */
    @Nonnull
    private Shop createDevShop() {
        LOG.info("DEV MODE: Creating test shop in database");
        Shop devShop = new Shop();
        devShop.setShopifyDomain(DEV_SHOP_DOMAIN);
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
        return shopRepository.save(devShop);
    }

    /**
     * Vérifie si le chemin correspond à un endpoint de développement.
     *
     * @param path le chemin à vérifier
     * @return true si c'est un endpoint de développement
     */
    private boolean isDevEndpoint(@Nullable String path) {
        if (path == null) {
            return false;
        }
        return path.startsWith("/vat") ||
               path.startsWith("/vies") ||
               path.startsWith("/oss") ||
               path.startsWith("/orders");
    }

    /**
     * Vérifie si le chemin correspond à un endpoint public.
     *
     * @param path le chemin à vérifier
     * @return true si c'est un endpoint public
     */
    private boolean isPublicEndpoint(@Nullable String path) {
        if (path == null) {
            return false;
        }
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
