package com.euvatease.controller;

import com.euvatease.service.ShopifyService;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * Contrôleur pour les webhooks Shopify
 */
@RestController
@RequestMapping("/shopify/webhooks")
public class ShopifyWebhookController {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ----------------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ShopifyWebhookController.class);

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    private final ShopifyService shopifyService;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public ShopifyWebhookController(@Nonnull ShopifyService shopifyService) {
        this.shopifyService = Objects.requireNonNull(shopifyService, "shopifyService must not be null");
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Webhook: App désinstallée
     */
    @Nonnull
    @PostMapping("/app-uninstalled")
    public ResponseEntity<?> appUninstalled(
            @Nonnull @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @Nonnull @RequestHeader("X-Shopify-Hmac-Sha256") String hmac,
            @Nonnull @RequestBody String payload) {

        log.info("Webhook app/uninstalled reçu de: {}", shopDomain);

        if (!shopifyService.verifyWebhookSignature(payload, hmac)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        shopifyService.processUninstall(shopDomain);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint de test pour vérifier que les webhooks fonctionnent
     */
    @Nonnull
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "healthy", "message", "Webhooks endpoint actif"));
    }

    /**
     * Webhook: Nouvelle commande
     */
    @Nonnull
    @PostMapping("/orders-create")
    public ResponseEntity<?> ordersCreate(
            @Nonnull @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @Nonnull @RequestHeader("X-Shopify-Hmac-Sha256") String hmac,
            @Nonnull @RequestBody String payload) {

        log.info("Webhook orders/create reçu de: {}", shopDomain);

        if (!shopifyService.verifyWebhookSignature(payload, hmac)) {
            log.warn("Signature webhook invalide pour: {}", shopDomain);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        shopifyService.processOrderWebhook(shopDomain, payload);
        return ResponseEntity.ok().build();
    }

    /**
     * Webhook: Commande payée
     */
    @Nonnull
    @PostMapping("/orders-paid")
    public ResponseEntity<?> ordersPaid(
            @Nonnull @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @Nonnull @RequestHeader("X-Shopify-Hmac-Sha256") String hmac,
            @Nonnull @RequestBody String payload) {

        log.info("Webhook orders/paid reçu de: {}", shopDomain);

        if (!shopifyService.verifyWebhookSignature(payload, hmac)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        shopifyService.processOrderWebhook(shopDomain, payload);
        return ResponseEntity.ok().build();
    }

    /**
     * Webhook: Commande mise à jour
     */
    @Nonnull
    @PostMapping("/orders-updated")
    public ResponseEntity<?> ordersUpdated(
            @Nonnull @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @Nonnull @RequestHeader("X-Shopify-Hmac-Sha256") String hmac,
            @Nonnull @RequestBody String payload) {

        log.info("Webhook orders/updated reçu de: {}", shopDomain);

        if (!shopifyService.verifyWebhookSignature(payload, hmac)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        shopifyService.processOrderWebhook(shopDomain, payload);
        return ResponseEntity.ok().build();
    }

    /**
     * Webhook: Remboursement créé
     */
    @Nonnull
    @PostMapping("/refunds-create")
    public ResponseEntity<?> refundsCreate(
            @Nonnull @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @Nonnull @RequestHeader("X-Shopify-Hmac-Sha256") String hmac,
            @Nonnull @RequestBody String payload) {

        log.info("Webhook refunds/create reçu de: {}", shopDomain);

        if (!shopifyService.verifyWebhookSignature(payload, hmac)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        shopifyService.processRefundWebhook(shopDomain, payload);
        return ResponseEntity.ok().build();
    }
}
