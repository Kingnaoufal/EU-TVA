package com.euvatease.controller;

import com.euvatease.entity.Shop;
import com.euvatease.service.AuditLogService;
import com.euvatease.service.ShopifyBillingService;
import com.euvatease.service.ShopifyService;
import com.euvatease.entity.AuditLog;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Contrôleur pour l'authentification OAuth Shopify
 */
@RestController
@RequestMapping("/shopify")
public class ShopifyAuthController {

    private static final Logger log = LoggerFactory.getLogger(ShopifyAuthController.class);

    private final ShopifyService shopifyService;
    private final ShopifyBillingService billingService;
    private final AuditLogService auditLogService;

    public ShopifyAuthController(ShopifyService shopifyService, ShopifyBillingService billingService,
                                  AuditLogService auditLogService) {
        this.shopifyService = shopifyService;
        this.billingService = billingService;
        this.auditLogService = auditLogService;
    }

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpiration;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    private final Map<String, String> stateStore = new HashMap<>();

    /**
     * Point d'entrée pour l'installation de l'app
     * GET /api/shopify/install?shop=example.myshopify.com
     */
    @GetMapping("/install")
    public ResponseEntity<?> install(@RequestParam String shop) {
        log.info("Demande d'installation pour: {}", shop);

        // Générer un state unique pour la sécurité
        String state = UUID.randomUUID().toString();
        stateStore.put(state, shop);

        String authUrl = shopifyService.getAuthorizationUrl(shop, state);

        return ResponseEntity.ok(Map.of(
            "authUrl", authUrl,
            "message", "Redirection vers Shopify pour autorisation"
        ));
    }

    /**
     * Callback OAuth après autorisation Shopify
     * GET /api/shopify/callback?code=xxx&shop=xxx&state=xxx
     */
    @GetMapping("/callback")
    public ResponseEntity<?> callback(
            @RequestParam String code,
            @RequestParam String shop,
            @RequestParam String state,
            @RequestParam(required = false) String hmac,
            HttpServletRequest request) {

        log.info("Callback OAuth reçu pour: {}", shop);

        // Vérifier le state
        String storedShop = stateStore.remove(state);
        if (storedShop == null) {
            log.warn("State invalide pour: {}", shop);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "État de sécurité invalide"));
        }

        try {
            // Échanger le code contre un token
            Shop installedShop = shopifyService.exchangeCodeForToken(shop, code);

            // Générer un JWT pour le frontend
            String token = generateJwtToken(installedShop);

            // Logger la connexion
            auditLogService.logWithRequest(installedShop, AuditLog.ActionType.LOGIN,
                "Installation et connexion réussie",
                request.getRemoteAddr(),
                request.getHeader("User-Agent"));

            // Redirection vers le frontend avec le token
            String redirectUrl = String.format("%s/auth/callback?token=%s&shop=%s",
                frontendUrl, token, installedShop.getShopifyDomain());

            return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", redirectUrl)
                .build();

        } catch (Exception e) {
            log.error("Erreur callback OAuth: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur lors de l'installation: " + e.getMessage()));
        }
    }

    /**
     * Endpoint pour la redirection embedded app
     */
    @GetMapping("/auth")
    public ResponseEntity<?> embeddedAuth(
            @RequestParam String shop,
            @RequestParam(required = false) String host,
            @RequestParam(required = false) String embedded) {

        log.info("Auth embedded pour: {}", shop);

        String state = UUID.randomUUID().toString();
        stateStore.put(state, shop);

        String authUrl = shopifyService.getAuthorizationUrl(shop, state);

        // Pour les apps embedded, on redirige directement
        return ResponseEntity.status(HttpStatus.FOUND)
            .header("Location", authUrl)
            .build();
    }

    /**
     * Génère un token JWT pour la session
     */
    private String generateJwtToken(Shop shop) {
        return Jwts.builder()
            .subject(shop.getShopifyDomain())
            .claim("shopId", shop.getId())
            .claim("shopName", shop.getShopName())
            .claim("email", shop.getEmail())
            .claim("subscriptionStatus", shop.getSubscriptionStatus().name())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
            .compact();
    }

    /**
     * Vérifie l'état de la session
     */
    @GetMapping("/session")
    public ResponseEntity<?> checkSession(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.replace("Bearer ", "");
            var claims = Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();

            return ResponseEntity.ok(Map.of(
                "valid", true,
                "shop", claims.getSubject(),
                "shopId", claims.get("shopId"),
                "shopName", claims.get("shopName"),
                "subscriptionStatus", claims.get("subscriptionStatus")
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("valid", false, "error", "Session invalide"));
        }
    }

    /**
     * Déconnexion
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        // Le JWT est stateless, donc on retourne juste OK
        // Le frontend doit supprimer le token côté client
        return ResponseEntity.ok(Map.of("message", "Déconnexion réussie"));
    }
}
