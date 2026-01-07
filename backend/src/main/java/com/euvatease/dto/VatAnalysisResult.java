package com.euvatease.dto;

import java.math.BigDecimal;

/**
 * Résultat de l'analyse TVA d'une commande
 */
public class VatAnalysisResult {

    private Long orderId;
    private String shopifyOrderId;
    private BigDecimal expectedVatRate;
    private String applicableCountry;
    private boolean euSale;
    private boolean domesticSale;
    private boolean b2b;
    private boolean vatExempt;
    private String exemptionReason;
    private boolean hasError;
    private String errorType;
    private String errorMessage;

    public VatAnalysisResult() {
    }

    public VatAnalysisResult(Long orderId, String shopifyOrderId, BigDecimal expectedVatRate,
                             String applicableCountry, boolean euSale, boolean domesticSale, boolean b2b,
                             boolean vatExempt, String exemptionReason, boolean hasError,
                             String errorType, String errorMessage) {
        this.orderId = orderId;
        this.shopifyOrderId = shopifyOrderId;
        this.expectedVatRate = expectedVatRate;
        this.applicableCountry = applicableCountry;
        this.euSale = euSale;
        this.domesticSale = domesticSale;
        this.b2b = b2b;
        this.vatExempt = vatExempt;
        this.exemptionReason = exemptionReason;
        this.hasError = hasError;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }

    // Getters
    public Long getOrderId() { return orderId; }
    public String getShopifyOrderId() { return shopifyOrderId; }
    public BigDecimal getExpectedVatRate() { return expectedVatRate; }
    public String getApplicableCountry() { return applicableCountry; }
    public boolean isEuSale() { return euSale; }
    public boolean isDomesticSale() { return domesticSale; }
    public boolean isB2b() { return b2b; }
    public boolean isVatExempt() { return vatExempt; }
    public String getExemptionReason() { return exemptionReason; }
    public boolean isHasError() { return hasError; }
    public String getErrorType() { return errorType; }
    public String getErrorMessage() { return errorMessage; }

    // Setters
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public void setShopifyOrderId(String shopifyOrderId) { this.shopifyOrderId = shopifyOrderId; }
    public void setExpectedVatRate(BigDecimal expectedVatRate) { this.expectedVatRate = expectedVatRate; }
    public void setApplicableCountry(String applicableCountry) { this.applicableCountry = applicableCountry; }
    public void setEuSale(boolean euSale) { this.euSale = euSale; }
    public void setDomesticSale(boolean domesticSale) { this.domesticSale = domesticSale; }
    public void setB2b(boolean b2b) { this.b2b = b2b; }
    public void setVatExempt(boolean vatExempt) { this.vatExempt = vatExempt; }
    public void setExemptionReason(String exemptionReason) { this.exemptionReason = exemptionReason; }
    public void setHasError(boolean hasError) { this.hasError = hasError; }
    public void setErrorType(String errorType) { this.errorType = errorType; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    // Builder
    public static VatAnalysisResultBuilder builder() { return new VatAnalysisResultBuilder(); }

    public static class VatAnalysisResultBuilder {
        private Long orderId;
        private String shopifyOrderId;
        private BigDecimal expectedVatRate;
        private String applicableCountry;
        private boolean euSale;
        private boolean domesticSale;
        private boolean b2b;
        private boolean vatExempt;
        private String exemptionReason;
        private boolean hasError;
        private String errorType;
        private String errorMessage;

        public VatAnalysisResultBuilder orderId(Long orderId) { this.orderId = orderId; return this; }
        public VatAnalysisResultBuilder shopifyOrderId(String shopifyOrderId) { this.shopifyOrderId = shopifyOrderId; return this; }
        public VatAnalysisResultBuilder expectedVatRate(BigDecimal expectedVatRate) { this.expectedVatRate = expectedVatRate; return this; }
        public VatAnalysisResultBuilder applicableCountry(String applicableCountry) { this.applicableCountry = applicableCountry; return this; }
        public VatAnalysisResultBuilder euSale(boolean euSale) { this.euSale = euSale; return this; }
        public VatAnalysisResultBuilder domesticSale(boolean domesticSale) { this.domesticSale = domesticSale; return this; }
        public VatAnalysisResultBuilder b2b(boolean b2b) { this.b2b = b2b; return this; }
        public VatAnalysisResultBuilder vatExempt(boolean vatExempt) { this.vatExempt = vatExempt; return this; }
        public VatAnalysisResultBuilder exemptionReason(String exemptionReason) { this.exemptionReason = exemptionReason; return this; }
        public VatAnalysisResultBuilder hasError(boolean hasError) { this.hasError = hasError; return this; }
        public VatAnalysisResultBuilder errorType(String errorType) { this.errorType = errorType; return this; }
        public VatAnalysisResultBuilder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }

        public VatAnalysisResult build() {
            return new VatAnalysisResult(orderId, shopifyOrderId, expectedVatRate, applicableCountry, euSale,
                    domesticSale, b2b, vatExempt, exemptionReason, hasError, errorType, errorMessage);
        }
    }
}
