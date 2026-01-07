package com.euvatease.controller;

import com.euvatease.service.ShopifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur pour les webhooks Shopify
 */
@RestController
@RequestMapping("/shopify/webhooks")
public class ShopifyWebhookController {

    private static final Logger log = LoggerFactory.getLogger(ShopifyWebhookController.class);

    private final ShopifyService shopifyService;

    public ShopifyWebhookController(ShopifyService shopifyService) {
        this.shopifyService = shopifyService;
    }

    /**
     * Webhook: Nouvelle commande
     */
    @PostMapping("/orders-create")
    public ResponseEntity<?> ordersCreate(
            @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmac,
            @RequestBody String payload) {

        log.info("Webhook orders/create reçu de: {}", shopDomain);

        if (!shopifyService.verifyWebhookSignature(payload, hmac)) {
            log.warn("Signature webhook invalide pour: {}", shopDomain);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        shopifyService.processOrderWebhook(shopDomain, payload);
        return ResponseEntity.ok().build();
    }

    /**
     * Webhook: Commande mise à jour
     */
    @PostMapping("/orders-updated")
    public ResponseEntity<?> ordersUpdated(
            @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmac,
            @RequestBody String payload) {

        log.info("Webhook orders/updated reçu de: {}", shopDomain);

        if (!shopifyService.verifyWebhookSignature(payload, hmac)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        shopifyService.processOrderWebhook(shopDomain, payload);
        return ResponseEntity.ok().build();
    }

    /**
     * Webhook: Commande payée
     */
    @PostMapping("/orders-paid")
    public ResponseEntity<?> ordersPaid(
            @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmac,
            @RequestBody String payload) {

        log.info("Webhook orders/paid reçu de: {}", shopDomain);

        if (!shopifyService.verifyWebhookSignature(payload, hmac)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        shopifyService.processOrderWebhook(shopDomain, payload);
        return ResponseEntity.ok().build();
    }

    /**
     * Webhook: Remboursement créé
     */
    @PostMapping("/refunds-create")
    public ResponseEntity<?> refundsCreate(
            @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmac,
            @RequestBody String payload) {

        log.info("Webhook refunds/create reçu de: {}", shopDomain);

        if (!shopifyService.verifyWebhookSignature(payload, hmac)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        shopifyService.processRefundWebhook(shopDomain, payload);
        return ResponseEntity.ok().build();
    }

    /**
     * Webhook: App désinstallée
     */
    @PostMapping("/app-uninstalled")
    public ResponseEntity<?> appUninstalled(
            @RequestHeader("X-Shopify-Shop-Domain") String shopDomain,
            @RequestHeader("X-Shopify-Hmac-Sha256") String hmac,
            @RequestBody String payload) {

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
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("status", "healthy", "message", "Webhooks endpoint actif"));
    }
}
