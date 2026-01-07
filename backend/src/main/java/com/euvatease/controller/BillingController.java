package com.euvatease.controller;

import com.euvatease.entity.Shop;
import com.euvatease.service.ShopifyBillingService;
import jakarta.annotation.Nonnull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

/**
 * Contrôleur pour la gestion des abonnements
 */
@RestController
@RequestMapping("/billing")
public class BillingController {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ----------------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(BillingController.class);

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    private final ShopifyBillingService billingService;

    @Nonnull
    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Nonnull
    @Value("${billing.monthly-price:99.00}")
    private String monthlyPrice;

    @Nonnull
    @Value("${billing.yearly-price:999.00}")
    private String yearlyPrice;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public BillingController(@Nonnull ShopifyBillingService billingService) {
        this.billingService = Objects.requireNonNull(billingService, "billingService must not be null");
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Callback après confirmation de l'abonnement
     * GET /api/billing/callback
     */
    @Nonnull
    @GetMapping("/callback")
    public ResponseEntity<?> billingCallback(
            @Nonnull @RequestParam("charge_id") String chargeId,
            @Nonnull @RequestAttribute("shop") Shop shop) {

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
     * Annule l'abonnement
     * POST /api/billing/cancel
     */
    @Nonnull
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelSubscription(@Nonnull @RequestAttribute("shop") Shop shop) {
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

    /**
     * Obtient les plans disponibles
     * GET /api/billing/plans
     */
    @Nonnull
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
     * Statut de l'abonnement
     * GET /api/billing/status
     */
    @Nonnull
    @GetMapping("/status")
    public ResponseEntity<?> getStatus(@Nonnull @RequestAttribute("shop") Shop shop) {
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
     * Crée un abonnement mensuel
     * POST /api/billing/subscribe/monthly
     */
    @Nonnull
    @PostMapping("/subscribe/monthly")
    public ResponseEntity<?> subscribeMonthly(@Nonnull @RequestAttribute("shop") Shop shop) {
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
    @Nonnull
    @PostMapping("/subscribe/yearly")
    public ResponseEntity<?> subscribeYearly(@Nonnull @RequestAttribute("shop") Shop shop) {
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

    @Nonnull
    private String getStatusMessage(@Nonnull Shop.SubscriptionStatus status, boolean active) {
        return switch (status) {
            case TRIAL -> "Vous êtes en période d'essai. Profitez de toutes les fonctionnalités gratuitement.";
            case ACTIVE -> "Votre abonnement est actif. Merci de votre confiance !";
            case CANCELLED -> "Votre abonnement est annulé. Vous conservez l'accès jusqu'à la fin de la période.";
            case EXPIRED -> "Votre abonnement a expiré. Renouvelez pour continuer à utiliser EU VAT Ease.";
            case PENDING -> "Votre abonnement est en attente de validation.";
        };
    }
}
