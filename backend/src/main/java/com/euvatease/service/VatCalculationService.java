package com.euvatease.service;

import com.euvatease.dto.VatAnalysisResult;
import com.euvatease.dto.VatValidationResult;
import com.euvatease.entity.*;
import com.euvatease.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Service de calcul et d'analyse de la TVA UE.
 * Gère la détection d'erreurs et le calcul des taux corrects.
 */
@Service
public class VatCalculationService {

    private static final Logger log = LoggerFactory.getLogger(VatCalculationService.class);

    private final OrderRepository orderRepository;
    private final EuVatRateRepository euVatRateRepository;
    private final VatValidationRepository vatValidationRepository;
    private final VatAlertRepository vatAlertRepository;
    private final AuditLogService auditLogService;

    public VatCalculationService(OrderRepository orderRepository, EuVatRateRepository euVatRateRepository,
                                 VatValidationRepository vatValidationRepository, VatAlertRepository vatAlertRepository,
                                 AuditLogService auditLogService) {
        this.orderRepository = orderRepository;
        this.euVatRateRepository = euVatRateRepository;
        this.vatValidationRepository = vatValidationRepository;
        this.vatAlertRepository = vatAlertRepository;
        this.auditLogService = auditLogService;
    }

    // Pays UE
    private static final Set<String> EU_COUNTRIES = Set.of(
        "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR",
        "DE", "GR", "HU", "IE", "IT", "LV", "LT", "LU", "MT", "NL",
        "PL", "PT", "RO", "SK", "SI", "ES", "SE"
    );

    /**
     * Analyse une commande et calcule la TVA attendue
     */
    @Transactional
    public VatAnalysisResult analyzeOrder(Order order) {
        VatAnalysisResult result = new VatAnalysisResult();
        result.setOrderId(order.getId());
        result.setShopifyOrderId(order.getShopifyOrderId());

        String customerCountry = order.getCustomerCountryCode();
        String shopCountry = order.getShop().getCountryCode();
        boolean isOssRegistered = Boolean.TRUE.equals(order.getShop().getOssRegistered());

        // Déterminer si c'est une vente UE
        boolean isEuSale = EU_COUNTRIES.contains(customerCountry);
        boolean isSameCountry = customerCountry != null && customerCountry.equals(shopCountry);
        boolean isB2b = Boolean.TRUE.equals(order.getIsB2b());

        result.setEuSale(isEuSale);
        result.setDomesticSale(isSameCountry);
        result.setB2b(isB2b);

        if (!isEuSale) {
            // Vente hors UE - pas de TVA UE
            result.setExpectedVatRate(BigDecimal.ZERO);
            result.setVatExempt(true);
            result.setExemptionReason("Vente hors Union Européenne");
            return result;
        }

        if (isSameCountry) {
            // Vente domestique - TVA du pays du vendeur
            result.setExpectedVatRate(getStandardVatRate(shopCountry));
            return result;
        }

        // Vente intracommunautaire
        if (isB2b && hasValidVatNumber(order)) {
            // B2B avec TVA valide - exonération
            result.setExpectedVatRate(BigDecimal.ZERO);
            result.setVatExempt(true);
            result.setExemptionReason("Livraison intracommunautaire B2B - TVA autoliquidée par l'acheteur");
            return result;
        }

        // B2C ou B2B sans TVA valide
        if (isOssRegistered) {
            // OSS: TVA du pays de destination
            result.setExpectedVatRate(getStandardVatRate(customerCountry));
            result.setApplicableCountry(customerCountry);
        } else {
            // Non OSS: vérifier le seuil
            // Hypothèse: si pas OSS, on applique la TVA du pays de l'acheteur (régime simplifié)
            result.setExpectedVatRate(getStandardVatRate(customerCountry));
            result.setApplicableCountry(customerCountry);
        }

        return result;
    }

    /**
     * Vérifie si une commande a des erreurs de TVA
     */
    @Transactional
    public Order detectVatErrors(Order order) {
        VatAnalysisResult analysis = analyzeOrder(order);
        
        BigDecimal appliedRate = order.getAppliedVatRate();
        BigDecimal expectedRate = analysis.getExpectedVatRate();
        
        if (appliedRate == null) appliedRate = BigDecimal.ZERO;
        if (expectedRate == null) expectedRate = BigDecimal.ZERO;

        // Calculer la TVA attendue
        BigDecimal subtotal = order.getSubtotalAmount() != null ? order.getSubtotalAmount() : BigDecimal.ZERO;
        BigDecimal calculatedVat = subtotal.multiply(expectedRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        
        order.setExpectedVatRate(expectedRate);
        order.setCalculatedVatAmount(calculatedVat);

        // Détecter les erreurs
        BigDecimal difference = appliedRate.subtract(expectedRate).abs();
        boolean hasError = difference.compareTo(BigDecimal.valueOf(0.5)) > 0; // Tolérance de 0.5%

        order.setHasVatError(hasError);
        
        if (hasError) {
            String errorType = determineErrorType(appliedRate, expectedRate, order);
            order.setVatErrorType(errorType);
            
            // Calculer la différence de TVA
            BigDecimal actualVat = order.getTaxAmount() != null ? order.getTaxAmount() : BigDecimal.ZERO;
            order.setVatDifference(calculatedVat.subtract(actualVat));
            
            log.warn("Erreur TVA détectée: order={}, appliqué={}%, attendu={}%, type={}",
                order.getOrderNumber(), appliedRate, expectedRate, errorType);
        }

        // Mise à jour exonération
        order.setVatExempt(analysis.isVatExempt());
        order.setVatExemptReason(analysis.getExemptionReason());

        return orderRepository.save(order);
    }

    /**
     * Analyse toutes les commandes d'une boutique sur une période
     */
    @Transactional
    public Map<String, Object> analyzeShopVat(Shop shop, LocalDateTime start, LocalDateTime end) {
        List<Order> orders = orderRepository.findByShopAndOrderDateBetween(shop, start, end);
        
        Map<String, Object> analysis = new HashMap<>();
        int totalOrders = orders.size();
        int errorsCount = 0;
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalVat = BigDecimal.ZERO;
        BigDecimal vatDifference = BigDecimal.ZERO;
        Map<String, BigDecimal> salesByCountry = new HashMap<>();
        Map<String, BigDecimal> vatByCountry = new HashMap<>();
        List<Map<String, Object>> errors = new ArrayList<>();

        for (Order order : orders) {
            detectVatErrors(order);
            
            if (Boolean.TRUE.equals(order.getHasVatError())) {
                errorsCount++;
                errors.add(Map.of(
                    "orderId", order.getShopifyOrderId(),
                    "orderNumber", order.getOrderNumber() != null ? order.getOrderNumber() : "",
                    "errorType", order.getVatErrorType() != null ? order.getVatErrorType() : "UNKNOWN",
                    "appliedRate", order.getAppliedVatRate(),
                    "expectedRate", order.getExpectedVatRate(),
                    "difference", order.getVatDifference()
                ));
            }

            totalSales = totalSales.add(order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO);
            totalVat = totalVat.add(order.getTaxAmount() != null ? order.getTaxAmount() : BigDecimal.ZERO);
            vatDifference = vatDifference.add(order.getVatDifference() != null ? order.getVatDifference() : BigDecimal.ZERO);

            String country = order.getCustomerCountryCode();
            if (country != null) {
                salesByCountry.merge(country, order.getTotalAmount() != null ? order.getTotalAmount() : BigDecimal.ZERO, BigDecimal::add);
                vatByCountry.merge(country, order.getTaxAmount() != null ? order.getTaxAmount() : BigDecimal.ZERO, BigDecimal::add);
            }
        }

        analysis.put("totalOrders", totalOrders);
        analysis.put("errorsCount", errorsCount);
        analysis.put("errorRate", totalOrders > 0 ? (errorsCount * 100.0 / totalOrders) : 0);
        analysis.put("totalSales", totalSales);
        analysis.put("totalVat", totalVat);
        analysis.put("vatDifference", vatDifference);
        analysis.put("salesByCountry", salesByCountry);
        analysis.put("vatByCountry", vatByCountry);
        analysis.put("errors", errors);
        analysis.put("periodStart", start);
        analysis.put("periodEnd", end);

        // Créer des alertes si nécessaire
        if (errorsCount > 0) {
            createVatErrorAlert(shop, errorsCount, errors);
        }

        return analysis;
    }

    /**
     * Vérifie le seuil OSS pour une boutique
     */
    @Transactional
    public Map<String, Object> checkOssThreshold(Shop shop) {
        String homeCountry = shop.getCountryCode();
        LocalDateTime yearStart = LocalDateTime.now().withDayOfYear(1).withHour(0).withMinute(0).withSecond(0);
        
        BigDecimal euSales = orderRepository.sumEuSalesForOssThreshold(shop, homeCountry, yearStart);
        if (euSales == null) euSales = BigDecimal.ZERO;

        BigDecimal threshold = BigDecimal.valueOf(10000);
        BigDecimal percentage = euSales.multiply(BigDecimal.valueOf(100)).divide(threshold, 2, RoundingMode.HALF_UP);
        boolean thresholdExceeded = euSales.compareTo(threshold) >= 0;
        boolean warningLevel = percentage.compareTo(BigDecimal.valueOf(80)) >= 0;

        Map<String, Object> result = new HashMap<>();
        result.put("euSales", euSales);
        result.put("threshold", threshold);
        result.put("percentage", percentage);
        result.put("thresholdExceeded", thresholdExceeded);
        result.put("warningLevel", warningLevel);
        result.put("ossRegistered", shop.getOssRegistered());

        // Créer alerte si nécessaire
        if (warningLevel && !Boolean.TRUE.equals(shop.getOssThresholdAlertSent())) {
            createOssThresholdAlert(shop, euSales, percentage, thresholdExceeded);
        }

        return result;
    }

    /**
     * Obtient le taux de TVA standard pour un pays
     */
    public BigDecimal getStandardVatRate(String countryCode) {
        if (countryCode == null) return BigDecimal.ZERO;
        return euVatRateRepository.findStandardRateByCountryCode(countryCode, LocalDate.now())
            .orElse(BigDecimal.ZERO);
    }

    /**
     * Obtient tous les taux de TVA UE actuels
     */
    public List<Map<String, Object>> getAllEuVatRates() {
        var rates = euVatRateRepository.findAllEuCountries(LocalDate.now());
        List<Map<String, Object>> result = new ArrayList<>();
        
        for (var rate : rates) {
            Map<String, Object> rateMap = new HashMap<>();
            rateMap.put("countryCode", rate.getCountryCode());
            rateMap.put("countryName", rate.getCountryName());
            rateMap.put("standardRate", rate.getStandardRate());
            rateMap.put("reducedRate", rate.getReducedRate());
            result.add(rateMap);
        }
        
        return result;
    }

    // Méthodes privées

    private boolean hasValidVatNumber(Order order) {
        if (order.getCustomerVatNumber() == null || order.getCustomerVatNumber().isEmpty()) {
            return false;
        }

        // Vérifier si une validation VIES existe
        List<VatValidation> validations = vatValidationRepository.findByOrderId(order.getId());
        return validations.stream()
            .anyMatch(v -> v.getValidationStatus() == VatValidation.ValidationStatus.VALID);
    }

    private String determineErrorType(BigDecimal applied, BigDecimal expected, Order order) {
        if (applied == null || applied.compareTo(BigDecimal.ZERO) == 0) {
            if (expected.compareTo(BigDecimal.ZERO) > 0) {
                return "VAT_MISSING";
            }
        }
        
        if (applied.compareTo(expected) > 0) {
            return "VAT_OVERCHARGED";
        }
        
        if (applied.compareTo(expected) < 0) {
            return "VAT_UNDERCHARGED";
        }
        
        return "VAT_WRONG_COUNTRY";
    }

    private void createVatErrorAlert(Shop shop, int errorCount, List<Map<String, Object>> errors) {
        // Vérifier si une alerte similaire existe déjà
        if (vatAlertRepository.existsByShopAndAlertTypeAndIsResolvedFalse(shop, VatAlert.AlertType.VAT_RATE_ERROR)) {
            return;
        }

        VatAlert alert = VatAlert.builder()
            .shop(shop)
            .alertType(VatAlert.AlertType.VAT_RATE_ERROR)
            .severity(errorCount > 5 ? VatAlert.Severity.ERROR : VatAlert.Severity.WARNING)
            .title("Erreurs de TVA détectées")
            .message(String.format("%d commande(s) avec des erreurs de taux de TVA ont été détectées. " +
                "Vérifiez la configuration de vos taxes dans Shopify.", errorCount))
            .actionRequired("Vérifier les paramètres de TVA dans Shopify Admin > Paramètres > Taxes")
            .build();

        vatAlertRepository.save(alert);
        log.info("Alerte TVA créée pour shop={}", shop.getShopifyDomain());
    }

    private void createOssThresholdAlert(Shop shop, BigDecimal euSales, BigDecimal percentage, boolean exceeded) {
        VatAlert.AlertType type = exceeded ? 
            VatAlert.AlertType.OSS_THRESHOLD_EXCEEDED : 
            VatAlert.AlertType.OSS_THRESHOLD_WARNING;

        String title = exceeded ? 
            "Seuil OSS dépassé !" : 
            "Vous approchez du seuil OSS";

        String message = exceeded ?
            String.format("Vos ventes UE (%.2f €) ont dépassé le seuil de 10 000 €. " +
                "Vous devez vous inscrire au régime OSS.", euSales) :
            String.format("Vos ventes UE atteignent %.1f%% du seuil OSS (10 000 €). " +
                "Préparez-vous à l'inscription OSS.", percentage);

        VatAlert alert = VatAlert.builder()
            .shop(shop)
            .alertType(type)
            .severity(exceeded ? VatAlert.Severity.CRITICAL : VatAlert.Severity.WARNING)
            .title(title)
            .message(message)
            .actionRequired(exceeded ? 
                "Inscrivez-vous au régime OSS auprès de votre administration fiscale" :
                "Préparez votre inscription au régime OSS")
            .build();

        vatAlertRepository.save(alert);
        
        shop.setOssThresholdAlertSent(true);
        
        auditLogService.log(shop, AuditLog.ActionType.ALERT_CREATED, "VatAlert", alert.getId(),
            "Alerte seuil OSS créée: " + percentage + "%");
    }
}
