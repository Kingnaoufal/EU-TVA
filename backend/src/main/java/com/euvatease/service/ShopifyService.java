package com.euvatease.service;

import com.euvatease.entity.AuditLog;
import com.euvatease.entity.Order;
import com.euvatease.entity.Shop;
import com.euvatease.repository.OrderRepository;
import com.euvatease.repository.ShopRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service d'intégration Shopify.
 * Gère l'OAuth, les webhooks et les appels API.
 */
@Service
public class ShopifyService {

    //~ ------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ShopifyService.class);

    private static final String SHOPIFY_API_VERSION = "2024-01";

    //~ ------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ------------------------------------------------------------------------------------------------

    @Nonnull
    private final AuditLogService auditLogService;

    @Nonnull
    private final ObjectMapper objectMapper;

    @Nonnull
    private final OrderRepository orderRepository;

    @Nonnull
    private final ShopRepository shopRepository;

    @Nonnull
    private final VatCalculationService vatCalculationService;

    @Nonnull
    private final ViesValidationService viesValidationService;

    @Value("${shopify.api-key}")
    @Nonnull
    private String apiKey;

    @Value("${shopify.api-secret}")
    @Nonnull
    private String apiSecret;

    @Value("${shopify.redirect-uri}")
    @Nonnull
    private String redirectUri;

    @Value("${shopify.scopes}")
    @Nonnull
    private String scopes;

    @Value("${shopify.webhook-secret}")
    @Nonnull
    private String webhookSecret;

    //~ ------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ------------------------------------------------------------------------------------------------

    public ShopifyService(@Nonnull ShopRepository shopRepository,
                          @Nonnull OrderRepository orderRepository,
                          @Nonnull VatCalculationService vatCalculationService,
                          @Nonnull ViesValidationService viesValidationService,
                          @Nonnull AuditLogService auditLogService,
                          @Nonnull ObjectMapper objectMapper) {
        this.shopRepository = Objects.requireNonNull(shopRepository, "shopRepository must not be null");
        this.orderRepository = Objects.requireNonNull(orderRepository, "orderRepository must not be null");
        this.vatCalculationService = Objects.requireNonNull(vatCalculationService, "vatCalculationService must not be null");
        this.viesValidationService = Objects.requireNonNull(viesValidationService, "viesValidationService must not be null");
        this.auditLogService = Objects.requireNonNull(auditLogService, "auditLogService must not be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "objectMapper must not be null");
    }

    //~ ------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ------------------------------------------------------------------------------------------------

    /**
     * Échange le code d'autorisation contre un access token
     */
    @Transactional
    @Nonnull
    public Shop exchangeCodeForToken(@Nonnull String shopDomain,
                                     @Nonnull String code) throws Exception {
        String normalizedDomain = normalizeShopDomain(shopDomain);
        String url = String.format("https://%s/admin/oauth/access_token", normalizedDomain);

        Map<String, String> body = Map.of(
            "client_id", apiKey,
            "client_secret", apiSecret,
            "code", code
        );

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(body)));

            String response = client.execute(request, httpResponse ->
                new String(httpResponse.getEntity().getContent().readAllBytes()));

            JsonNode jsonResponse = objectMapper.readTree(response);
            String accessToken = jsonResponse.get("access_token").asText();
            String tokenScope = jsonResponse.has("scope") ? jsonResponse.get("scope").asText() : "";

            // Récupérer les informations de la boutique
            JsonNode shopInfo = getShopInfo(normalizedDomain, accessToken);

            // Créer ou mettre à jour la boutique
            Shop shop = shopRepository.findByShopifyDomain(normalizedDomain)
                .orElse(new Shop());

            shop.setShopifyDomain(normalizedDomain);
            shop.setAccessToken(accessToken);
            shop.setShopifyShopId(shopInfo.get("id").asText());
            shop.setShopName(shopInfo.get("name").asText());
            shop.setEmail(shopInfo.get("email").asText());
            shop.setCountryCode(shopInfo.get("country_code").asText());
            shop.setCurrency(shopInfo.get("currency").asText());
            shop.setIsActive(true);
            shop.setInstalledAt(LocalDateTime.now());
            shop.setSubscriptionStatus(Shop.SubscriptionStatus.TRIAL);
            shop.setTrialEndsAt(LocalDateTime.now().plusDays(14));

            shop = shopRepository.save(shop);

            // Configurer les webhooks
            setupWebhooks(shop);

            auditLogService.log(shop, AuditLog.ActionType.SHOP_INSTALLED, "Shop", shop.getId(),
                "Application installée sur " + normalizedDomain);

            log.info("Boutique installée: {}", normalizedDomain);
            return shop;
        }
    }

    /**
     * Génère l'URL d'autorisation OAuth Shopify
     */
    @Nonnull
    public String getAuthorizationUrl(@Nonnull String shopDomain,
                                      @Nonnull String state) {
        String normalizedDomain = normalizeShopDomain(shopDomain);

        return String.format(
            "https://%s/admin/oauth/authorize?client_id=%s&scope=%s&redirect_uri=%s&state=%s",
            normalizedDomain,
            apiKey,
            URLEncoder.encode(scopes, StandardCharsets.UTF_8),
            URLEncoder.encode(redirectUri, StandardCharsets.UTF_8),
            state
        );
    }

    /**
     * Récupère les informations de la boutique
     */
    @Nonnull
    public JsonNode getShopInfo(@Nonnull String shopDomain,
                                @Nonnull String accessToken) throws Exception {
        String url = String.format("https://%s/admin/api/%s/shop.json", shopDomain, SHOPIFY_API_VERSION);

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            request.setHeader("X-Shopify-Access-Token", accessToken);

            String response = client.execute(request, httpResponse ->
                new String(httpResponse.getEntity().getContent().readAllBytes()));

            return objectMapper.readTree(response).get("shop");
        }
    }

    /**
     * Traite une commande Shopify
     */
    @Transactional
    @Nonnull
    public Order processOrder(@Nonnull Shop shop,
                              @Nonnull JsonNode orderJson) {
        String shopifyOrderId = orderJson.get("id").asText();

        // Vérifier si la commande existe déjà
        Optional<Order> existing = orderRepository.findByShopAndShopifyOrderId(shop, shopifyOrderId);
        Order order = existing.orElse(new Order());

        order.setShop(shop);
        order.setShopifyOrderId(shopifyOrderId);
        order.setOrderNumber(orderJson.has("order_number") ? orderJson.get("order_number").asText() : null);

        // Date de commande
        if (orderJson.has("created_at")) {
            order.setOrderDate(parseShopifyDate(orderJson.get("created_at").asText()));
        }

        // Informations client
        JsonNode billingAddress = orderJson.get("billing_address");
        if (billingAddress != null && !billingAddress.isNull()) {
            order.setCustomerCountryCode(billingAddress.has("country_code") ?
                billingAddress.get("country_code").asText() : null);
            order.setCustomerCountryName(billingAddress.has("country") ?
                billingAddress.get("country").asText() : null);
        }

        if (orderJson.has("email")) {
            order.setCustomerEmail(orderJson.get("email").asText());
        }

        // Numéro de TVA client (si présent dans les notes ou attributs)
        String customerVat = extractCustomerVatNumber(orderJson);
        order.setCustomerVatNumber(customerVat);
        order.setIsB2b(customerVat != null && !customerVat.isEmpty());

        // Montants
        order.setTotalAmount(parseDecimal(orderJson, "total_price"));
        order.setSubtotalAmount(parseDecimal(orderJson, "subtotal_price"));
        order.setTaxAmount(parseDecimal(orderJson, "total_tax"));
        order.setCurrency(orderJson.has("currency") ? orderJson.get("currency").asText() : "EUR");

        // Calcul du taux de TVA appliqué
        if (orderJson.has("tax_lines") && orderJson.get("tax_lines").isArray() && orderJson.get("tax_lines").size() > 0) {
            JsonNode firstTaxLine = orderJson.get("tax_lines").get(0);
            if (firstTaxLine.has("rate")) {
                BigDecimal rate = new BigDecimal(firstTaxLine.get("rate").asText()).multiply(BigDecimal.valueOf(100));
                order.setAppliedVatRate(rate);
            }
        }

        // Livraison
        order.setShippingAmount(parseDecimal(orderJson, "total_shipping_price_set", "shop_money", "amount"));

        // Statuts
        if (orderJson.has("financial_status")) {
            try {
                order.setFinancialStatus(Order.FinancialStatus.valueOf(
                    orderJson.get("financial_status").asText().toUpperCase()));
            } catch (IllegalArgumentException e) {
                order.setFinancialStatus(Order.FinancialStatus.PENDING);
            }
        }

        // Données brutes pour audit
        order.setRawData(orderJson.toString());

        order = orderRepository.save(order);

        // Valider le numéro de TVA si B2B
        if (Boolean.TRUE.equals(order.getIsB2b()) && customerVat != null) {
            viesValidationService.validateVatNumberAsync(shop, customerVat, shopifyOrderId);
        }

        // Détecter les erreurs de TVA
        vatCalculationService.detectVatErrors(order);

        log.debug("Commande traitée: {} - {}", shopifyOrderId, order.getOrderNumber());
        return order;
    }

    /**
     * Traite un webhook de commande
     */
    @Async
    @Transactional
    public void processOrderWebhook(@Nonnull String shopDomain,
                                    @Nonnull String payload) {
        try {
            JsonNode orderJson = objectMapper.readTree(payload);
            Shop shop = shopRepository.findByShopifyDomain(shopDomain)
                .orElseThrow(() -> new RuntimeException("Boutique non trouvée: " + shopDomain));

            processOrder(shop, orderJson);
        } catch (Exception e) {
            log.error("Erreur traitement webhook commande: {}", e.getMessage());
        }
    }

    /**
     * Traite un remboursement
     */
    @Async
    @Transactional
    public void processRefundWebhook(@Nonnull String shopDomain,
                                     @Nonnull String payload) {
        try {
            JsonNode refundJson = objectMapper.readTree(payload);
            Shop shop = shopRepository.findByShopifyDomain(shopDomain)
                .orElseThrow(() -> new RuntimeException("Boutique non trouvée: " + shopDomain));

            String orderId = refundJson.get("order_id").asText();
            orderRepository.findByShopAndShopifyOrderId(shop, orderId).ifPresent(order -> {
                order.setIsRefunded(true);

                // Calculer le montant remboursé
                BigDecimal refundAmount = BigDecimal.ZERO;
                if (refundJson.has("transactions") && refundJson.get("transactions").isArray()) {
                    for (JsonNode transaction : refundJson.get("transactions")) {
                        if (transaction.has("amount")) {
                            refundAmount = refundAmount.add(new BigDecimal(transaction.get("amount").asText()));
                        }
                    }
                }
                order.setRefundAmount(refundAmount);

                orderRepository.save(order);

                auditLogService.log(shop, AuditLog.ActionType.ORDER_REFUNDED, "Order", order.getId(),
                    String.format("Remboursement de %.2f€", refundAmount));

                log.info("Remboursement traité: order={}, montant={}", orderId, refundAmount);
            });
        } catch (Exception e) {
            log.error("Erreur traitement webhook remboursement: {}", e.getMessage());
        }
    }

    /**
     * Traite une désinstallation
     */
    @Transactional
    public void processUninstall(@Nonnull String shopDomain) {
        shopRepository.findByShopifyDomain(shopDomain).ifPresent(shop -> {
            shop.setIsActive(false);
            shop.setUninstalledAt(LocalDateTime.now());
            shop.setAccessToken(null);
            shopRepository.save(shop);

            auditLogService.log(shop, AuditLog.ActionType.SHOP_UNINSTALLED, "Shop", shop.getId(),
                "Application désinstallée");

            log.info("Boutique désinstallée: {}", shopDomain);
        });
    }

    /**
     * Synchronise les commandes d'une boutique
     */
    @Transactional
    public int syncOrders(@Nonnull Shop shop,
                          @Nullable LocalDateTime since) {
        log.info("Synchronisation des commandes pour {} depuis {}", shop.getShopifyDomain(), since);

        int syncedCount = 0;
        String pageInfo = null;

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            do {
                String url = buildOrdersUrl(shop, since, pageInfo);
                HttpGet request = new HttpGet(url);
                request.setHeader("X-Shopify-Access-Token", shop.getAccessToken());

                var response = client.execute(request, httpResponse -> {
                    String body = new String(httpResponse.getEntity().getContent().readAllBytes());
                    String linkHeader = httpResponse.getFirstHeader("Link") != null ?
                        httpResponse.getFirstHeader("Link").getValue() : null;
                    return Map.of("body", body, "link", linkHeader != null ? linkHeader : "");
                });

                JsonNode ordersJson = objectMapper.readTree(response.get("body")).get("orders");

                if (ordersJson != null && ordersJson.isArray()) {
                    for (JsonNode orderJson : ordersJson) {
                        processOrder(shop, orderJson);
                        syncedCount++;
                    }
                }

                pageInfo = extractNextPageInfo(response.get("link"));

            } while (pageInfo != null);

            auditLogService.log(shop, AuditLog.ActionType.ORDERS_SYNCED, "Shop", shop.getId(),
                String.format("%d commandes synchronisées", syncedCount));

            log.info("Synchronisation terminée: {} commandes", syncedCount);

        } catch (Exception e) {
            log.error("Erreur synchronisation commandes: {}", e.getMessage());
        }

        return syncedCount;
    }

    /**
     * Vérifie la signature d'un webhook
     */
    public boolean verifyWebhookSignature(@Nonnull String body,
                                          @Nonnull String hmacHeader) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(body.getBytes());
            String calculatedHmac = Base64.getEncoder().encodeToString(hash);
            return calculatedHmac.equals(hmacHeader);
        } catch (Exception e) {
            log.error("Erreur vérification signature webhook: {}", e.getMessage());
            return false;
        }
    }

    @Nonnull
    private String buildOrdersUrl(@Nonnull Shop shop,
                                  @Nullable LocalDateTime since,
                                  @Nullable String pageInfo) {
        String baseUrl = String.format("https://%s/admin/api/%s/orders.json",
            shop.getShopifyDomain(), SHOPIFY_API_VERSION);

        if (pageInfo != null) {
            return baseUrl + "?page_info=" + pageInfo + "&limit=250";
        }

        StringBuilder url = new StringBuilder(baseUrl);
        url.append("?status=any&limit=250");

        if (since != null) {
            url.append("&created_at_min=").append(since.format(DateTimeFormatter.ISO_DATE_TIME));
        }

        return url.toString();
    }

    /**
     * Crée un webhook Shopify
     */
    private void createWebhook(@Nonnull Shop shop,
                               @Nonnull String topic) throws Exception {
        String url = String.format("https://%s/admin/api/%s/webhooks.json",
            shop.getShopifyDomain(), SHOPIFY_API_VERSION);

        String callbackUrl = redirectUri.replace("/callback", "/webhooks/" + topic.replace("/", "-"));

        Map<String, Object> webhookData = Map.of(
            "webhook", Map.of(
                "topic", topic,
                "address", callbackUrl,
                "format", "json"
            )
        );

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(url);
            request.setHeader("X-Shopify-Access-Token", shop.getAccessToken());
            request.setHeader("Content-Type", "application/json");
            request.setEntity(new StringEntity(objectMapper.writeValueAsString(webhookData)));

            client.execute(request, httpResponse -> {
                int statusCode = httpResponse.getCode();
                if (statusCode >= 200 && statusCode < 300) {
                    log.info("Webhook créé: {} pour {}", topic, shop.getShopifyDomain());
                } else {
                    log.warn("Erreur création webhook {}: status {}", topic, statusCode);
                }
                return null;
            });
        }
    }

    @Nullable
    private String extractCustomerVatNumber(@Nonnull JsonNode orderJson) {
        // Chercher dans les notes
        if (orderJson.has("note") && !orderJson.get("note").isNull()) {
            String note = orderJson.get("note").asText();
            if (note.matches(".*[A-Z]{2}[0-9A-Z]+.*")) {
                // Pattern simple de détection TVA
                return extractVatFromText(note);
            }
        }

        // Chercher dans les attributs personnalisés
        if (orderJson.has("note_attributes") && orderJson.get("note_attributes").isArray()) {
            for (JsonNode attr : orderJson.get("note_attributes")) {
                String name = attr.has("name") ? attr.get("name").asText().toLowerCase() : "";
                if (name.contains("vat") || name.contains("tva") || name.contains("tax")) {
                    return attr.has("value") ? attr.get("value").asText() : null;
                }
            }
        }

        // Chercher dans les métafields client
        if (orderJson.has("customer") && !orderJson.get("customer").isNull()) {
            JsonNode customer = orderJson.get("customer");
            if (customer.has("metafields") && customer.get("metafields").isArray()) {
                for (JsonNode mf : customer.get("metafields")) {
                    String key = mf.has("key") ? mf.get("key").asText().toLowerCase() : "";
                    if (key.contains("vat") || key.contains("tva")) {
                        return mf.has("value") ? mf.get("value").asText() : null;
                    }
                }
            }
        }

        return null;
    }

    @Nullable
    private String extractNextPageInfo(@Nullable String linkHeader) {
        if (linkHeader == null || linkHeader.isEmpty()) {
            return null;
        }

        String[] links = linkHeader.split(",");
        for (String link : links) {
            if (link.contains("rel=\"next\"")) {
                int start = link.indexOf("page_info=") + 10;
                int end = link.indexOf(">", start);
                if (start > 10 && end > start) {
                    return link.substring(start, end);
                }
            }
        }
        return null;
    }

    @Nullable
    private String extractVatFromText(@Nonnull String text) {
        // Pattern basique pour extraire un numéro de TVA UE
        Pattern pattern = Pattern.compile("([A-Z]{2}[0-9A-Z]{8,12})");
        Matcher matcher = pattern.matcher(text.toUpperCase());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    @Nonnull
    private String normalizeShopDomain(@Nullable String shopDomain) {
        if (shopDomain == null) {
            return "";
        }
        shopDomain = shopDomain.toLowerCase().trim();
        if (!shopDomain.endsWith(".myshopify.com")) {
            shopDomain = shopDomain + ".myshopify.com";
        }
        return shopDomain;
    }

    @Nonnull
    private BigDecimal parseDecimal(@Nonnull JsonNode json,
                                    @Nonnull String... path) {
        JsonNode node = json;
        for (String key : path) {
            if (node == null || !node.has(key)) {
                return BigDecimal.ZERO;
            }
            node = node.get(key);
        }
        try {
            return new BigDecimal(node.asText());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    @Nonnull
    private LocalDateTime parseShopifyDate(@Nonnull String dateStr) {
        try {
            return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_DATE_TIME);
        } catch (Exception e) {
            try {
                return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
            } catch (Exception e2) {
                return LocalDateTime.now();
            }
        }
    }

    /**
     * Configure les webhooks Shopify
     */
    private void setupWebhooks(@Nonnull Shop shop) {
        List<String> topics = List.of(
            "orders/create",
            "orders/updated",
            "orders/paid",
            "refunds/create",
            "app/uninstalled"
        );

        for (String topic : topics) {
            try {
                createWebhook(shop, topic);
            } catch (Exception e) {
                log.error("Erreur création webhook {}: {}", topic, e.getMessage());
            }
        }
    }
}
