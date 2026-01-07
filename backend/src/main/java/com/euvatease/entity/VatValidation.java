package com.euvatease.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "vat_validations")
public class VatValidation {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Nullable
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    @Nonnull
    private Shop shop;

    @Column(name = "order_id")
    @Nullable
    private Long orderId;

    @Column(name = "shopify_order_id")
    @Nullable
    private String shopifyOrderId;

    @Column(name = "vat_number", nullable = false)
    @Nonnull
    private String vatNumber;

    @Column(name = "country_code", nullable = false, length = 2)
    @Nonnull
    private String countryCode;

    @Column(name = "vat_number_without_country")
    @Nullable
    private String vatNumberWithoutCountry;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status", nullable = false)
    @Nonnull
    private ValidationStatus validationStatus;

    @Column(name = "company_name")
    @Nullable
    private String companyName;

    @Column(name = "company_address", columnDefinition = "TEXT")
    @Nullable
    private String companyAddress;

    @Column(name = "vies_request_id")
    @Nullable
    private String viesRequestId;

    @Column(name = "vies_response", columnDefinition = "TEXT")
    @Nullable
    private String viesResponse;

    @Column(name = "error_message")
    @Nullable
    private String errorMessage;

    @Column(name = "retry_count")
    @Nullable
    private Integer retryCount;

    @Column(name = "validation_date", nullable = false)
    @Nonnull
    private LocalDateTime validationDate;

    @Column(name = "next_retry_at")
    @Nullable
    private LocalDateTime nextRetryAt;

    @Column(name = "created_at")
    @Nullable
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Nullable
    private LocalDateTime updatedAt;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public VatValidation() {
    }

    public VatValidation(@Nullable Long id,
                         @Nonnull Shop shop,
                         @Nullable Long orderId,
                         @Nullable String shopifyOrderId,
                         @Nonnull String vatNumber,
                         @Nonnull String countryCode,
                         @Nullable String vatNumberWithoutCountry,
                         @Nonnull ValidationStatus validationStatus,
                         @Nullable String companyName,
                         @Nullable String companyAddress,
                         @Nullable String viesRequestId,
                         @Nullable String viesResponse,
                         @Nullable String errorMessage,
                         @Nullable Integer retryCount,
                         @Nonnull LocalDateTime validationDate,
                         @Nullable LocalDateTime nextRetryAt,
                         @Nullable LocalDateTime createdAt,
                         @Nullable LocalDateTime updatedAt) {
        this.id = id;
        this.shop = shop;
        this.orderId = orderId;
        this.shopifyOrderId = shopifyOrderId;
        this.vatNumber = vatNumber;
        this.countryCode = countryCode;
        this.vatNumberWithoutCountry = vatNumberWithoutCountry;
        this.validationStatus = validationStatus;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.viesRequestId = viesRequestId;
        this.viesResponse = viesResponse;
        this.errorMessage = errorMessage;
        this.retryCount = retryCount;
        this.validationDate = validationDate;
        this.nextRetryAt = nextRetryAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        validationDate = LocalDateTime.now();
        if (retryCount == null) {
            retryCount = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @Nonnull
    public Shop getShop() {
        return shop;
    }

    public void setShop(@Nonnull Shop shop) {
        this.shop = shop;
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

    @Nonnull
    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(@Nonnull String vatNumber) {
        this.vatNumber = vatNumber;
    }

    @Nonnull
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(@Nonnull String countryCode) {
        this.countryCode = countryCode;
    }

    @Nullable
    public String getVatNumberWithoutCountry() {
        return vatNumberWithoutCountry;
    }

    public void setVatNumberWithoutCountry(@Nullable String vatNumberWithoutCountry) {
        this.vatNumberWithoutCountry = vatNumberWithoutCountry;
    }

    @Nonnull
    public ValidationStatus getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(@Nonnull ValidationStatus validationStatus) {
        this.validationStatus = validationStatus;
    }

    @Nullable
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(@Nullable String companyName) {
        this.companyName = companyName;
    }

    @Nullable
    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(@Nullable String companyAddress) {
        this.companyAddress = companyAddress;
    }

    @Nullable
    public String getViesRequestId() {
        return viesRequestId;
    }

    public void setViesRequestId(@Nullable String viesRequestId) {
        this.viesRequestId = viesRequestId;
    }

    @Nullable
    public String getViesResponse() {
        return viesResponse;
    }

    public void setViesResponse(@Nullable String viesResponse) {
        this.viesResponse = viesResponse;
    }

    @Nullable
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(@Nullable String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Nullable
    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(@Nullable Integer retryCount) {
        this.retryCount = retryCount;
    }

    @Nonnull
    public LocalDateTime getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(@Nonnull LocalDateTime validationDate) {
        this.validationDate = validationDate;
    }

    @Nullable
    public LocalDateTime getNextRetryAt() {
        return nextRetryAt;
    }

    public void setNextRetryAt(@Nullable LocalDateTime nextRetryAt) {
        this.nextRetryAt = nextRetryAt;
    }

    @Nullable
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Nullable
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(@Nullable LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Nonnull
    public static VatValidationBuilder builder() {
        return new VatValidationBuilder();
    }

    @Nonnull
    public String getUserFriendlyMessage() {
        return switch (validationStatus) {
            case VALID -> String.format("✅ Numéro de TVA valide (vérifié le %s)",
                    validationDate != null ? validationDate.toLocalDate() : "N/A");
            case INVALID -> "❌ Numéro de TVA invalide";
            case UNAVAILABLE -> "⚠️ Service de validation temporairement indisponible";
            case PENDING -> "⏳ Validation en cours...";
            case ERROR -> "❌ Erreur technique lors de la validation";
            case FORMAT_ERROR -> "❌ Format de numéro de TVA incorrect";
        };
    }

    public boolean isLegalProof() {
        return validationStatus == ValidationStatus.VALID && viesRequestId != null;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Nested Classes
    //~ ----------------------------------------------------------------------------------------------------------------

    public enum ValidationStatus {
        VALID,
        INVALID,
        UNAVAILABLE,
        PENDING,
        ERROR,
        FORMAT_ERROR
    }

    public static class VatValidationBuilder {

        @Nullable
        private Long id;

        @Nullable
        private Shop shop;

        @Nullable
        private Long orderId;

        @Nullable
        private String shopifyOrderId;

        @Nullable
        private String vatNumber;

        @Nullable
        private String countryCode;

        @Nullable
        private String vatNumberWithoutCountry;

        @Nullable
        private ValidationStatus validationStatus;

        @Nullable
        private String companyName;

        @Nullable
        private String companyAddress;

        @Nullable
        private String viesRequestId;

        @Nullable
        private String viesResponse;

        @Nullable
        private String errorMessage;

        @Nullable
        private Integer retryCount;

        @Nullable
        private LocalDateTime validationDate;

        @Nullable
        private LocalDateTime nextRetryAt;

        @Nullable
        private LocalDateTime createdAt;

        @Nullable
        private LocalDateTime updatedAt;

        @Nonnull
        public VatValidationBuilder id(@Nullable Long id) {
            this.id = id;
            return this;
        }

        @Nonnull
        public VatValidationBuilder shop(@Nonnull Shop shop) {
            this.shop = shop;
            return this;
        }

        @Nonnull
        public VatValidationBuilder orderId(@Nullable Long orderId) {
            this.orderId = orderId;
            return this;
        }

        @Nonnull
        public VatValidationBuilder shopifyOrderId(@Nullable String shopifyOrderId) {
            this.shopifyOrderId = shopifyOrderId;
            return this;
        }

        @Nonnull
        public VatValidationBuilder vatNumber(@Nonnull String vatNumber) {
            this.vatNumber = vatNumber;
            return this;
        }

        @Nonnull
        public VatValidationBuilder countryCode(@Nonnull String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        @Nonnull
        public VatValidationBuilder vatNumberWithoutCountry(@Nullable String vatNumberWithoutCountry) {
            this.vatNumberWithoutCountry = vatNumberWithoutCountry;
            return this;
        }

        @Nonnull
        public VatValidationBuilder validationStatus(@Nonnull ValidationStatus validationStatus) {
            this.validationStatus = validationStatus;
            return this;
        }

        @Nonnull
        public VatValidationBuilder companyName(@Nullable String companyName) {
            this.companyName = companyName;
            return this;
        }

        @Nonnull
        public VatValidationBuilder companyAddress(@Nullable String companyAddress) {
            this.companyAddress = companyAddress;
            return this;
        }

        @Nonnull
        public VatValidationBuilder viesRequestId(@Nullable String viesRequestId) {
            this.viesRequestId = viesRequestId;
            return this;
        }

        @Nonnull
        public VatValidationBuilder viesResponse(@Nullable String viesResponse) {
            this.viesResponse = viesResponse;
            return this;
        }

        @Nonnull
        public VatValidationBuilder errorMessage(@Nullable String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        @Nonnull
        public VatValidationBuilder retryCount(@Nullable Integer retryCount) {
            this.retryCount = retryCount;
            return this;
        }

        @Nonnull
        public VatValidationBuilder validationDate(@Nonnull LocalDateTime validationDate) {
            this.validationDate = validationDate;
            return this;
        }

        @Nonnull
        public VatValidationBuilder nextRetryAt(@Nullable LocalDateTime nextRetryAt) {
            this.nextRetryAt = nextRetryAt;
            return this;
        }

        @Nonnull
        public VatValidationBuilder createdAt(@Nullable LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Nonnull
        public VatValidationBuilder updatedAt(@Nullable LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        @Nonnull
        public VatValidation build() {
            return new VatValidation(id, shop, orderId, shopifyOrderId, vatNumber, countryCode,
                    vatNumberWithoutCountry, validationStatus, companyName, companyAddress, viesRequestId,
                    viesResponse, errorMessage, retryCount, validationDate, nextRetryAt, createdAt, updatedAt);
        }
    }
}
