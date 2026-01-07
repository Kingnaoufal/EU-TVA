package com.euvatease.service;

import com.euvatease.entity.AuditLog;
import com.euvatease.entity.Shop;
import com.euvatease.repository.ShopRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Service de gestion des abonnements via Shopify Billing API.
 */
@Service
public class ShopifyBillingService {

    private static final Logger log = LoggerFactory.getLogger(ShopifyBillingService.class);

    private final ShopRepository shopRepository;
    private final AuditLogService auditLogService;
    private final ObjectMapper objectMapper;

    public ShopifyBillingService(ShopRepository shopRepository, AuditLogService auditLogService,
                                 ObjectMapper objectMapper) {
        this.shopRepository = shopRepository;
        this.auditLogService = auditLogService;
        this.objectMapper = objectMapper;
    }

    @Value("${shopify.app-url}")
    private String appUrl;

    @Value("${billing.monthly-price:99.00}")
    private BigDecimal monthlyPrice;

    @Value("${billing.yearly-price:999.00}")
    private BigDecimal yearlyPrice;

    @Value("${billing.trial-days:14}")
    private int trialDays;

    private static final String SHOPIFY_API_VERSION = "2024-01";

    /**
     * Crée un abonnement mensuel
     */
    @Transactional
    public String createMonthlySubscription(Shop shop) throws Exception {
        return createRecurringCharge(shop, "EU VAT Ease - Mensuel", monthlyPrice, 30);
    }

    /**
     * Crée un abonnement annuel
     */
    @Transactional
    public String createYearlySubscription(Shop shop) throws Exception {
        return createRecurringCharge(shop, "EU VAT Ease - Annuel", yearlyPrice, 365);
    }

    /**
     * Crée une charge récurrente via Shopify Billing API
     */
    private String createRecurringCharge(Shop shop, String name, BigDecimal price, int intervalDays) throws Exception {
        String url = String.format("https://%s/admin/api/%s/recurring_application_charges.json",
            shop.getShopifyDomain(), SHOPIFY_API_VERSION);

        // Calculer la période d'essai restante
        int remainingTrial = 0;
        if (shop.getTrialEndsAt() != null && shop.getTrialEndsAt().isAfter(LocalDateTime.now())) {
            remainingTrial = (int) java.time.temporal.ChronoUnit.DAYS.between(
                LocalDateTime.now(), shop.getTrialEndsAt());
        }

        Map<String, Object> chargeData = Map.of(
            "recurring_application_charge", Map.of(
                "name", name,
                "price", price.toString(),
                "return_url", appUrl + "/api/billing/callback",
                "trial_days", remainingTrial > 0 ? remainingTrial : trialDays,
                "test", false // Mettre true pour les tests
            )
        );

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.setHeader("X-Shopify-Access-Token", shop.getAccessToken());
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(chargeData)));

            String response = client.execute(request, httpResponse ->
                new String(httpResponse.getEntity().getContent().readAllBytes()));

            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode charge = jsonResponse.get("recurring_application_charge");

            if (charge != null && charge.has("confirmation_url")) {
                String chargeId = charge.get("id").asText();
                shop.setShopifyChargeId(chargeId);
                shopRepository.save(shop);

                return charge.get("confirmation_url").asText();
            }

            throw new RuntimeException("Erreur création abonnement: " + response);
        }
    }

    /**
     * Active un abonnement après confirmation
     */
    @Transactional
    public Shop activateSubscription(Shop shop, String chargeId) throws Exception {
        String url = String.format("https://%s/admin/api/%s/recurring_application_charges/%s.json",
            shop.getShopifyDomain(), SHOPIFY_API_VERSION, chargeId);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("X-Shopify-Access-Token", shop.getAccessToken());

            String response = client.execute(request, httpResponse ->
                new String(httpResponse.getEntity().getContent().readAllBytes()));

            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode charge = jsonResponse.get("recurring_application_charge");

            if (charge != null) {
                String status = charge.get("status").asText();
                
                if ("active".equals(status)) {
                    shop.setSubscriptionStatus(Shop.SubscriptionStatus.ACTIVE);
                    shop.setShopifyChargeId(chargeId);
                    
                    // Déterminer le plan basé sur le prix
                    BigDecimal price = new BigDecimal(charge.get("price").asText());
                    if (price.compareTo(yearlyPrice) >= 0) {
                        shop.setSubscriptionPlan("yearly");
                        shop.setSubscriptionEndsAt(LocalDateTime.now().plusYears(1));
                    } else {
                        shop.setSubscriptionPlan("monthly");
                        shop.setSubscriptionEndsAt(LocalDateTime.now().plusMonths(1));
                    }

                    shop = shopRepository.save(shop);

                    auditLogService.log(shop, AuditLog.ActionType.SUBSCRIPTION_STARTED, "Shop", shop.getId(),
                        "Abonnement activé: " + shop.getSubscriptionPlan());

                    log.info("Abonnement activé: shop={}, plan={}", shop.getShopifyDomain(), shop.getSubscriptionPlan());
                } else if ("declined".equals(status)) {
                    log.warn("Abonnement refusé pour {}", shop.getShopifyDomain());
                }
            }

            return shop;
        }
    }

    /**
     * Annule un abonnement
     */
    @Transactional
    public Shop cancelSubscription(Shop shop) throws Exception {
        if (shop.getShopifyChargeId() == null) {
            throw new RuntimeException("Aucun abonnement actif");
        }

        String url = String.format("https://%s/admin/api/%s/recurring_application_charges/%s.json",
            shop.getShopifyDomain(), SHOPIFY_API_VERSION, shop.getShopifyChargeId());

        final String shopDomain = shop.getShopifyDomain();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            org.apache.hc.client5.http.classic.methods.HttpDelete request = 
                new org.apache.hc.client5.http.classic.methods.HttpDelete(url);
            request.setHeader("X-Shopify-Access-Token", shop.getAccessToken());

            client.execute(request, httpResponse -> {
                int statusCode = httpResponse.getCode();
                if (statusCode >= 200 && statusCode < 300) {
                    log.info("Abonnement annulé: {}", shopDomain);
                }
                return null;
            });

            shop.setSubscriptionStatus(Shop.SubscriptionStatus.CANCELLED);
            Shop savedShop = shopRepository.save(shop);

            auditLogService.log(savedShop, AuditLog.ActionType.SUBSCRIPTION_CANCELLED, "Shop", savedShop.getId(),
                "Abonnement annulé");

            return savedShop;
        }
    }

    /**
     * Vérifie le statut de l'abonnement
     */
    public Shop.SubscriptionStatus checkSubscriptionStatus(Shop shop) {
        // Vérifier si l'essai ou l'abonnement est expiré
        LocalDateTime now = LocalDateTime.now();

        if (shop.getSubscriptionStatus() == Shop.SubscriptionStatus.TRIAL) {
            if (shop.getTrialEndsAt() != null && shop.getTrialEndsAt().isBefore(now)) {
                shop.setSubscriptionStatus(Shop.SubscriptionStatus.EXPIRED);
                shopRepository.save(shop);
            }
        } else if (shop.getSubscriptionStatus() == Shop.SubscriptionStatus.ACTIVE) {
            if (shop.getSubscriptionEndsAt() != null && shop.getSubscriptionEndsAt().isBefore(now)) {
                shop.setSubscriptionStatus(Shop.SubscriptionStatus.EXPIRED);
                shopRepository.save(shop);
            }
        }

        return shop.getSubscriptionStatus();
    }

    /**
     * Vérifie si la boutique a accès aux fonctionnalités
     */
    public boolean hasActiveSubscription(Shop shop) {
        Shop.SubscriptionStatus status = checkSubscriptionStatus(shop);
        return status == Shop.SubscriptionStatus.TRIAL || status == Shop.SubscriptionStatus.ACTIVE;
    }
}
