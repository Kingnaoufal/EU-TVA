package com.euvatease.controller;

import com.euvatease.entity.Shop;
import com.euvatease.service.ShopifyBillingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Contrôleur pour la gestion des abonnements
 */
@RestController
@RequestMapping("/billing")
public class BillingController {

    private static final Logger log = LoggerFactory.getLogger(BillingController.class);

    private final ShopifyBillingService billingService;

    public BillingController(ShopifyBillingService billingService) {
        this.billingService = billingService;
    }

    @Value("${billing.monthly-price:99.00}")
    private String monthlyPrice;

    @Value("${billing.yearly-price:999.00}")
    private String yearlyPrice;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    /**
     * Obtient les plans disponibles
     * GET /api/billing/plans
     */
    @GetMapping("/plans")
    public ResponseEntity<?> getPlans() {
        return ResponseEntity.ok(Map.of(
            "monthly", Map.of(
                "id", "monthly",
                "name", "Mensuel",
                "price", monthlyPrice,
                "currency", "EUR",
                "interval", "mois",
                "description", "Facturation mensuelle, sans engagement"
            ),
            "yearly", Map.of(
                "id", "yearly",
                "name", "Annuel",
                "price", yearlyPrice,
                "currency", "EUR",
                "interval", "an",
                "description", "Économisez 2 mois avec l'abonnement annuel",
                "savings", "189€"
            )
        ));
    }

    /**
     * Crée un abonnement mensuel
     * POST /api/billing/subscribe/monthly
     */
    @PostMapping("/subscribe/monthly")
    public ResponseEntity<?> subscribeMonthly(@RequestAttribute("shop") Shop shop) {
        try {
            String confirmationUrl = billingService.createMonthlySubscription(shop);
            return ResponseEntity.ok(Map.of(
                "confirmationUrl", confirmationUrl,
                "message", "Redirection vers Shopify pour confirmer l'abonnement"
            ));
        } catch (Exception e) {
            log.error("Erreur création abonnement mensuel: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur lors de la création de l'abonnement"));
        }
    }

    /**
     * Crée un abonnement annuel
     * POST /api/billing/subscribe/yearly
     */
    @PostMapping("/subscribe/yearly")
    public ResponseEntity<?> subscribeYearly(@RequestAttribute("shop") Shop shop) {
        try {
            String confirmationUrl = billingService.createYearlySubscription(shop);
            return ResponseEntity.ok(Map.of(
                "confirmationUrl", confirmationUrl,
                "message", "Redirection vers Shopify pour confirmer l'abonnement"
            ));
        } catch (Exception e) {
            log.error("Erreur création abonnement annuel: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur lors de la création de l'abonnement"));
        }
    }

    /**
     * Callback après confirmation de l'abonnement
     * GET /api/billing/callback
     */
    @GetMapping("/callback")
    public ResponseEntity<?> billingCallback(
            @RequestParam("charge_id") String chargeId,
            @RequestAttribute("shop") Shop shop) {

        try {
            Shop updatedShop = billingService.activateSubscription(shop, chargeId);
            
            // Redirection vers le dashboard avec message de succès
            return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", frontendUrl + "/dashboard?subscription=activated")
                .build();
                
        } catch (Exception e) {
            log.error("Erreur activation abonnement: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", frontendUrl + "/billing?error=activation_failed")
                .build();
        }
    }

    /**
     * Statut de l'abonnement
     * GET /api/billing/status
     */
    @GetMapping("/status")
    public ResponseEntity<?> getStatus(@RequestAttribute("shop") Shop shop) {
        Shop.SubscriptionStatus status = billingService.checkSubscriptionStatus(shop);
        boolean active = billingService.hasActiveSubscription(shop);

        return ResponseEntity.ok(Map.of(
            "status", status,
            "active", active,
            "plan", shop.getSubscriptionPlan() != null ? shop.getSubscriptionPlan() : "trial",
            "trialEndsAt", shop.getTrialEndsAt(),
            "expiresAt", shop.getSubscriptionEndsAt(),
            "message", getStatusMessage(status, active)
        ));
    }

    /**
     * Annule l'abonnement
     * POST /api/billing/cancel
     */
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelSubscription(@RequestAttribute("shop") Shop shop) {
        try {
            billingService.cancelSubscription(shop);
            return ResponseEntity.ok(Map.of(
                "message", "Votre abonnement a été annulé. Vous conservez l'accès jusqu'à la fin de la période payée."
            ));
        } catch (Exception e) {
            log.error("Erreur annulation abonnement: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Erreur lors de l'annulation"));
        }
    }

    private String getStatusMessage(Shop.SubscriptionStatus status, boolean active) {
        return switch (status) {
            case TRIAL -> "Vous êtes en période d'essai. Profitez de toutes les fonctionnalités gratuitement.";
            case ACTIVE -> "Votre abonnement est actif. Merci de votre confiance !";
            case CANCELLED -> "Votre abonnement est annulé. Vous conservez l'accès jusqu'à la fin de la période.";
            case EXPIRED -> "Votre abonnement a expiré. Renouvelez pour continuer à utiliser EU VAT Ease.";
            case PENDING -> "Votre abonnement est en attente de validation.";
        };
    }
}
