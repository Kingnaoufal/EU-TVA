package com.euvatease.controller;

import com.euvatease.dto.VatAnalysisResult;
import com.euvatease.dto.VatValidationResult;
import com.euvatease.entity.Order;
import com.euvatease.entity.Shop;
import com.euvatease.entity.VatAlert;
import com.euvatease.repository.*;
import com.euvatease.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur principal pour le dashboard et l'analyse TVA
 */
@RestController
@RequestMapping("/vat")
public class VatController {

    private static final Logger log = LoggerFactory.getLogger(VatController.class);

    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;
    private final VatAlertRepository vatAlertRepository;
    private final VatValidationRepository vatValidationRepository;
    private final VatCalculationService vatCalculationService;
    private final ViesValidationService viesValidationService;
    private final ShopifyService shopifyService;
    private final ShopifyBillingService billingService;

    public VatController(ShopRepository shopRepository, OrderRepository orderRepository,
                         VatAlertRepository vatAlertRepository, VatValidationRepository vatValidationRepository,
                         VatCalculationService vatCalculationService, ViesValidationService viesValidationService,
                         ShopifyService shopifyService, ShopifyBillingService billingService) {
        this.shopRepository = shopRepository;
        this.orderRepository = orderRepository;
        this.vatAlertRepository = vatAlertRepository;
        this.vatValidationRepository = vatValidationRepository;
        this.vatCalculationService = vatCalculationService;
        this.viesValidationService = viesValidationService;
        this.shopifyService = shopifyService;
        this.billingService = billingService;
    }

    /**
     * Tableau de bord principal
     * GET /api/vat/dashboard
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@RequestAttribute("shop") Shop shop) {
        try {
            Map<String, Object> dashboard = new HashMap<>();

            // Période actuelle (trimestre en cours)
            LocalDateTime now = LocalDateTime.now();
            int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
            LocalDateTime quarterStart = LocalDateTime.of(now.getYear(), (currentQuarter - 1) * 3 + 1, 1, 0, 0);
            LocalDateTime quarterEnd = now;

            // Statistiques des commandes
            long totalOrders = orderRepository.countOrdersInPeriod(shop, quarterStart, quarterEnd);
            long vatErrors = orderRepository.countVatErrors(shop);
            var salesByCountry = orderRepository.getSalesByCountry(shop, quarterStart, quarterEnd);

            // Alertes non lues
            long unreadAlerts = vatAlertRepository.countUnreadAlerts(shop);
            List<VatAlert> criticalAlerts = vatAlertRepository.findCriticalUnresolvedAlerts(shop);

            // Vérification du seuil OSS
            Map<String, Object> ossThreshold = vatCalculationService.checkOssThreshold(shop);

            // Statut de l'abonnement
            boolean hasSubscription = billingService.hasActiveSubscription(shop);

            // Shop info - use HashMap to allow nulls
            Map<String, Object> shopInfo = new HashMap<>();
            shopInfo.put("name", shop.getShopName());
            shopInfo.put("domain", shop.getShopifyDomain());
            shopInfo.put("country", shop.getCountryCode());
            shopInfo.put("ossRegistered", shop.getOssRegistered());
            shopInfo.put("vatNumber", shop.getVatNumber() != null ? shop.getVatNumber() : "");
            dashboard.put("shop", shopInfo);

            // Subscription info - use HashMap to allow nulls
            Map<String, Object> subInfo = new HashMap<>();
            subInfo.put("status", shop.getSubscriptionStatus() != null ? shop.getSubscriptionStatus().name() : "TRIAL");
            subInfo.put("plan", shop.getSubscriptionPlan() != null ? shop.getSubscriptionPlan() : "trial");
            subInfo.put("active", hasSubscription);
            subInfo.put("trialEndsAt", shop.getTrialEndsAt());
            subInfo.put("expiresAt", shop.getSubscriptionEndsAt());
            dashboard.put("subscription", subInfo);

            dashboard.put("stats", Map.of(
                "totalOrders", totalOrders,
                "vatErrors", vatErrors,
                "errorRate", totalOrders > 0 ? String.format("%.1f%%", (vatErrors * 100.0 / totalOrders)) : "0%"
            ));

            dashboard.put("alerts", Map.of(
                "unreadCount", unreadAlerts,
                "critical", criticalAlerts.stream().map(this::mapAlert).collect(Collectors.toList())
            ));

            dashboard.put("ossThreshold", ossThreshold);

            dashboard.put("salesByCountry", salesByCountry.stream()
                .map(row -> Map.of(
                    "country", row[0],
                    "orderCount", row[1],
                    "totalSales", row[2],
                    "totalVat", row[3]
                ))
                .collect(Collectors.toList()));

            dashboard.put("period", Map.of(
                "quarter", currentQuarter,
                "year", now.getYear(),
                "start", quarterStart.toString(),
                "end", quarterEnd.toString()
            ));

            // Messages de statut pour l'utilisateur
            dashboard.put("statusMessage", generateStatusMessage(vatErrors, criticalAlerts.size(), ossThreshold));

            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            log.error("Error generating dashboard: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Dashboard error",
                "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }

    /**
     * Liste des commandes avec pagination et filtres
     * GET /api/vat/orders
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getOrders(
            @RequestAttribute("shop") Shop shop,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Boolean hasErrors,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate"));
            Page<Order> ordersPage;
            
            if (hasErrors != null && hasErrors) {
                ordersPage = orderRepository.findByShopAndHasVatErrorTrue(shop, pageRequest);
            } else {
                ordersPage = orderRepository.findByShop(shop, pageRequest);
            }
            
            List<Map<String, Object>> orders = ordersPage.getContent().stream()
                .map(this::mapOrder)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "content", orders,
                "totalElements", ordersPage.getTotalElements(),
                "totalPages", ordersPage.getTotalPages(),
                "number", ordersPage.getNumber(),
                "size", ordersPage.getSize()
            ));
        } catch (Exception e) {
            log.error("Error fetching orders: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of(
                "error", "Orders fetch error",
                "message", e.getMessage() != null ? e.getMessage() : "Unknown error"
            ));
        }
    }
    
    /**
     * Détail d'une commande
     * GET /api/vat/orders/{orderId}
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<?> getOrderDetail(
            @RequestAttribute("shop") Shop shop,
            @PathVariable String orderId) {
        
        return orderRepository.findByShopAndShopifyOrderId(shop, orderId)
            .map(order -> ResponseEntity.ok(mapOrder(order)))
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Analyse d'une commande spécifique
     * POST /api/vat/orders/{orderId}/analyze
     */
    @PostMapping("/orders/{orderId}/analyze")
    public ResponseEntity<?> analyzeOrder(
            @RequestAttribute("shop") Shop shop,
            @PathVariable String orderId) {
        
        return orderRepository.findByShopAndShopifyOrderId(shop, orderId)
            .map(order -> {
                VatAnalysisResult result = vatCalculationService.analyzeOrder(order);
                return ResponseEntity.ok(result);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Analyse TVA complète pour une période
     * POST /api/vat/analyze
     */
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeVat(
            @RequestAttribute("shop") Shop shop,
            @RequestBody Map<String, String> request) {

        LocalDateTime start = LocalDateTime.parse(request.get("startDate"));
        LocalDateTime end = LocalDateTime.parse(request.get("endDate"));

        Map<String, Object> analysis = vatCalculationService.analyzeShopVat(shop, start, end);

        return ResponseEntity.ok(analysis);
    }

    /**
     * Liste des commandes avec erreurs de TVA
     * GET /api/vat/errors
     */
    @GetMapping("/errors")
    public ResponseEntity<?> getVatErrors(
            @RequestAttribute("shop") Shop shop,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<Order> errors = orderRepository.findByShopAndHasVatErrorTrue(shop, 
            PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "orderDate")));

        return ResponseEntity.ok(Map.of(
            "content", errors.getContent().stream().map(this::mapOrder).collect(Collectors.toList()),
            "totalElements", errors.getTotalElements(),
            "totalPages", errors.getTotalPages(),
            "currentPage", page
        ));
    }

    /**
     * Valide un numéro de TVA
     * POST /api/vat/validate
     */
    @PostMapping("/validate")
    public ResponseEntity<?> validateVatNumber(
            @RequestAttribute("shop") Shop shop,
            @RequestBody Map<String, String> request) {

        String vatNumber = request.get("vatNumber");
        String orderId = request.get("orderId");

        if (vatNumber == null || vatNumber.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "Numéro de TVA requis"
            ));
        }

        VatValidationResult result = viesValidationService.validateVatNumber(shop, vatNumber, orderId);

        return ResponseEntity.ok(result);
    }

    /**
     * Historique des validations TVA
     * GET /api/vat/validations
     */
    @GetMapping("/validations")
    public ResponseEntity<?> getValidations(
            @RequestAttribute("shop") Shop shop,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        var validations = vatValidationRepository.findRecentValidations(shop,
            PageRequest.of(page, size));

        return ResponseEntity.ok(Map.of(
            "content", validations.getContent().stream().map(v -> Map.of(
                "id", v.getId(),
                "vatNumber", v.getVatNumber(),
                "countryCode", v.getCountryCode(),
                "status", v.getValidationStatus(),
                "companyName", v.getCompanyName() != null ? v.getCompanyName() : "",
                "validationDate", v.getValidationDate(),
                "userMessage", v.getUserFriendlyMessage(),
                "isLegalProof", v.isLegalProof()
            )).collect(Collectors.toList()),
            "totalElements", validations.getTotalElements(),
            "totalPages", validations.getTotalPages()
        ));
    }

    /**
     * Liste des alertes
     * GET /api/vat/alerts
     */
    @GetMapping("/alerts")
    public ResponseEntity<?> getAlerts(
            @RequestAttribute("shop") Shop shop,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        var alerts = vatAlertRepository.findByShopOrderByCreatedAtDesc(shop,
            PageRequest.of(page, size));

        return ResponseEntity.ok(Map.of(
            "content", alerts.getContent().stream().map(this::mapAlert).collect(Collectors.toList()),
            "totalElements", alerts.getTotalElements(),
            "totalPages", alerts.getTotalPages(),
            "unreadCount", vatAlertRepository.countUnreadAlerts(shop)
        ));
    }

    /**
     * Marque toutes les alertes comme lues
     * POST /api/vat/alerts/read-all
     */
    @PostMapping("/alerts/read-all")
    public ResponseEntity<?> markAlertsAsRead(@RequestAttribute("shop") Shop shop) {
        int updated = vatAlertRepository.markAllAsRead(shop);
        return ResponseEntity.ok(Map.of("updated", updated));
    }

    /**
     * Résout une alerte
     * POST /api/vat/alerts/{id}/resolve
     */
    @PostMapping("/alerts/{id}/resolve")
    public ResponseEntity<?> resolveAlert(
            @RequestAttribute("shop") Shop shop,
            @PathVariable Long id) {

        return vatAlertRepository.findById(id)
            .filter(a -> a.getShop().getId().equals(shop.getId()))
            .map(alert -> {
                alert.setIsResolved(true);
                alert.setResolvedAt(LocalDateTime.now());
                vatAlertRepository.save(alert);
                return ResponseEntity.ok(Map.of("message", "Alerte résolue"));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Taux de TVA UE
     * GET /api/vat/rates
     */
    @GetMapping("/rates")
    public ResponseEntity<?> getVatRates() {
        return ResponseEntity.ok(vatCalculationService.getAllEuVatRates());
    }

    /**
     * Synchronise les commandes depuis Shopify
     * POST /api/vat/sync
     */
    @PostMapping("/sync")
    public ResponseEntity<?> syncOrders(
            @RequestAttribute("shop") Shop shop,
            @RequestBody(required = false) Map<String, String> request) {

        LocalDateTime since = null;
        if (request != null && request.containsKey("since")) {
            since = LocalDateTime.parse(request.get("since"));
        } else {
            // Par défaut, synchroniser les 90 derniers jours
            since = LocalDateTime.now().minusDays(90);
        }

        int synced = shopifyService.syncOrders(shop, since);

        return ResponseEntity.ok(Map.of(
            "synced", synced,
            "message", String.format("%d commandes synchronisées", synced)
        ));
    }

    /**
     * Met à jour les paramètres de la boutique
     * PUT /api/vat/settings
     */
    @PutMapping("/settings")
    public ResponseEntity<?> updateSettings(
            @RequestAttribute("shop") Shop shop,
            @RequestBody Map<String, Object> settings) {

        if (settings.containsKey("vatNumber")) {
            shop.setVatNumber((String) settings.get("vatNumber"));
        }
        if (settings.containsKey("ossRegistered")) {
            shop.setOssRegistered((Boolean) settings.get("ossRegistered"));
        }
        if (settings.containsKey("ossCountryCode")) {
            shop.setOssCountryCode((String) settings.get("ossCountryCode"));
        }
        if (settings.containsKey("alertEmailEnabled")) {
            shop.setAlertEmailEnabled((Boolean) settings.get("alertEmailEnabled"));
        }

        shopRepository.save(shop);

        return ResponseEntity.ok(Map.of("message", "Paramètres mis à jour"));
    }

    // Méthodes utilitaires

    private Map<String, Object> mapOrder(Order order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("shopifyOrderId", order.getShopifyOrderId());
        map.put("orderNumber", order.getOrderNumber());
        map.put("orderDate", order.getOrderDate());
        map.put("customerCountry", order.getCustomerCountryCode());
        map.put("totalAmount", order.getTotalAmount());
        map.put("taxAmount", order.getTaxAmount());
        map.put("appliedVatRate", order.getAppliedVatRate());
        map.put("expectedVatRate", order.getExpectedVatRate());
        map.put("vatDifference", order.getVatDifference());
        map.put("hasVatError", order.getHasVatError());
        map.put("vatErrorType", order.getVatErrorType());
        map.put("isB2b", order.getIsB2b());
        map.put("vatExempt", order.getVatExempt());
        return map;
    }

    private Map<String, Object> mapAlert(VatAlert alert) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", alert.getId());
        map.put("type", alert.getAlertType());
        map.put("severity", alert.getSeverity());
        map.put("title", alert.getTitle());
        map.put("message", alert.getMessage());
        map.put("actionRequired", alert.getActionRequired());
        map.put("isRead", alert.getIsRead());
        map.put("isResolved", alert.getIsResolved());
        map.put("createdAt", alert.getCreatedAt());
        map.put("icon", alert.getSeverityIcon());
        return map;
    }

    private Map<String, Object> generateStatusMessage(long vatErrors, int criticalAlerts, Map<String, Object> ossThreshold) {
        Map<String, Object> status = new HashMap<>();

        if (criticalAlerts > 0) {
            status.put("type", "warning");
            status.put("title", "Action requise");
            status.put("message", String.format("%d alerte(s) critique(s) nécessitent votre attention", criticalAlerts));
        } else if (vatErrors > 0) {
            status.put("type", "info");
            status.put("title", "Vérifications recommandées");
            status.put("message", String.format("%d commande(s) avec des écarts de TVA détectés", vatErrors));
        } else if ((Boolean) ossThreshold.get("thresholdExceeded")) {
            status.put("type", "warning");
            status.put("title", "Seuil OSS dépassé");
            status.put("message", "Vos ventes UE dépassent 10 000€. Inscription OSS recommandée.");
        } else {
            status.put("type", "success");
            status.put("title", "Tout est en ordre");
            status.put("message", "Aucune action requise. Vous êtes conforme.");
        }

        return status;
    }
}
