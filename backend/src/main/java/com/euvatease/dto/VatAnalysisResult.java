package com.euvatease.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;

/**
 * Résultat de l'analyse TVA d'une commande
 */
public class VatAnalysisResult {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nullable
    private Long orderId;

    @Nullable
    private String shopifyOrderId;

    @Nullable
    private BigDecimal expectedVatRate;

    @Nullable
    private String applicableCountry;

    private boolean euSale;

    private boolean domesticSale;

    private boolean b2b;

    private boolean vatExempt;

    @Nullable
    private String exemptionReason;

    private boolean hasError;

    @Nullable
    private String errorType;

    @Nullable
    private String errorMessage;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public VatAnalysisResult() {
    }

    public VatAnalysisResult(@Nullable Long orderId,
                             @Nullable String shopifyOrderId,
                             @Nullable BigDecimal expectedVatRate,
                             @Nullable String applicableCountry,
                             boolean euSale,
                             boolean domesticSale,
                             boolean b2b,
                             boolean vatExempt,
                             @Nullable String exemptionReason,
                             boolean hasError,
                             @Nullable String errorType,
                             @Nullable String errorMessage) {
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

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    public static VatAnalysisResultBuilder builder() {
        return new VatAnalysisResultBuilder();
    }

    @Nullable
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(@Nullable Long orderId) {
        this.orderId = orderId;
    }

    @Nullable
    public String getShopifyOrderId() {
        return shopifyOrderId;
    }

    public void setShopifyOrderId(@Nullable String shopifyOrderId) {
        this.shopifyOrderId = shopifyOrderId;
    }

    @Nullable
    public BigDecimal getExpectedVatRate() {
        return expectedVatRate;
    }

    public void setExpectedVatRate(@Nullable BigDecimal expectedVatRate) {
        this.expectedVatRate = expectedVatRate;
    }

    @Nullable
    public String getApplicableCountry() {
        return applicableCountry;
    }

    public void setApplicableCountry(@Nullable String applicableCountry) {
        this.applicableCountry = applicableCountry;
    }

    public boolean isEuSale() {
        return euSale;
    }

    public void setEuSale(boolean euSale) {
        this.euSale = euSale;
    }

    public boolean isDomesticSale() {
        return domesticSale;
    }

    public void setDomesticSale(boolean domesticSale) {
        this.domesticSale = domesticSale;
    }

    public boolean isB2b() {
        return b2b;
    }

    public void setB2b(boolean b2b) {
        this.b2b = b2b;
    }

    public boolean isVatExempt() {
        return vatExempt;
    }

    public void setVatExempt(boolean vatExempt) {
        this.vatExempt = vatExempt;
    }

    @Nullable
    public String getExemptionReason() {
        return exemptionReason;
    }

    public void setExemptionReason(@Nullable String exemptionReason) {
        this.exemptionReason = exemptionReason;
    }

    public boolean isHasError() {
        return hasError;
    }

    public void setHasError(boolean hasError) {
        this.hasError = hasError;
    }

    @Nullable
    public String getErrorType() {
        return errorType;
    }

    public void setErrorType(@Nullable String errorType) {
        this.errorType = errorType;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Nested Classes
    //~ ----------------------------------------------------------------------------------------------------------------

    public static class VatAnalysisResultBuilder {

        @Nullable
        private Long orderId;

        @Nullable
        private String shopifyOrderId;

        @Nullable
        private BigDecimal expectedVatRate;

        @Nullable
        private String applicableCountry;

        private boolean euSale;

        private boolean domesticSale;

        private boolean b2b;

        private boolean vatExempt;

        @Nullable
        private String exemptionReason;

        private boolean hasError;

        @Nullable
        private String errorType;

        @Nullable
        private String errorMessage;

        @Nonnull
        public VatAnalysisResultBuilder orderId(@Nullable Long orderId) {
            this.orderId = orderId;
            return this;
        }

        @Nonnull
        public VatAnalysisResultBuilder shopifyOrderId(@Nullable String shopifyOrderId) {
            this.shopifyOrderId = shopifyOrderId;
            return this;
        }

        @Nonnull
        public VatAnalysisResultBuilder expectedVatRate(@Nullable BigDecimal expectedVatRate) {
            this.expectedVatRate = expectedVatRate;
            return this;
        }

        @Nonnull
        public VatAnalysisResultBuilder applicableCountry(@Nullable String applicableCountry) {
            this.applicableCountry = applicableCountry;
            return this;
        }

        @Nonnull
        public VatAnalysisResultBuilder euSale(boolean euSale) {
            this.euSale = euSale;
            return this;
        }

        @Nonnull
        public VatAnalysisResultBuilder domesticSale(boolean domesticSale) {
            this.domesticSale = domesticSale;
            return this;
        }

        @Nonnull
        public VatAnalysisResultBuilder b2b(boolean b2b) {
            this.b2b = b2b;
            return this;
        }

        @Nonnull
        public VatAnalysisResultBuilder vatExempt(boolean vatExempt) {
            this.vatExempt = vatExempt;
            return this;
        }

        @Nonnull
        public VatAnalysisResultBuilder exemptionReason(@Nullable String exemptionReason) {
            this.exemptionReason = exemptionReason;
            return this;
        }

        @Nonnull
        public VatAnalysisResultBuilder hasError(boolean hasError) {
            this.hasError = hasError;
            return this;
        }

        @Nonnull
        public VatAnalysisResultBuilder errorType(@Nullable String errorType) {
            this.errorType = errorType;
            return this;
        }

        @Nonnull
        public VatAnalysisResultBuilder errorMessage(@Nullable String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        @Nonnull
        public VatAnalysisResult build() {
            return new VatAnalysisResult(orderId, shopifyOrderId, expectedVatRate, applicableCountry, euSale,
                    domesticSale, b2b, vatExempt, exemptionReason, hasError, errorType, errorMessage);
        }
    }
}
