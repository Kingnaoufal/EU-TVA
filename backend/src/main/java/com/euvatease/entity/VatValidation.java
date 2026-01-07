package com.euvatease.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vat_validations")
public class VatValidation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "shopify_order_id")
    private String shopifyOrderId;

    @Column(name = "vat_number", nullable = false)
    private String vatNumber;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "vat_number_without_country")
    private String vatNumberWithoutCountry;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_status", nullable = false)
    private ValidationStatus validationStatus;

    @Column(name = "company_name")
    private String companyName;

    @Column(name = "company_address", columnDefinition = "TEXT")
    private String companyAddress;

    @Column(name = "vies_request_id")
    private String viesRequestId;

    @Column(name = "vies_response", columnDefinition = "TEXT")
    private String viesResponse;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount;

    @Column(name = "validation_date", nullable = false)
    private LocalDateTime validationDate;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public VatValidation() {
    }

    public VatValidation(Long id, Shop shop, Long orderId, String shopifyOrderId, String vatNumber,
                         String countryCode, String vatNumberWithoutCountry, ValidationStatus validationStatus,
                         String companyName, String companyAddress, String viesRequestId, String viesResponse,
                         String errorMessage, Integer retryCount, LocalDateTime validationDate,
                         LocalDateTime nextRetryAt, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        validationDate = LocalDateTime.now();
        if (retryCount == null) retryCount = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public Shop getShop() { return shop; }
    public Long getOrderId() { return orderId; }
    public String getShopifyOrderId() { return shopifyOrderId; }
    public String getVatNumber() { return vatNumber; }
    public String getCountryCode() { return countryCode; }
    public String getVatNumberWithoutCountry() { return vatNumberWithoutCountry; }
    public ValidationStatus getValidationStatus() { return validationStatus; }
    public String getCompanyName() { return companyName; }
    public String getCompanyAddress() { return companyAddress; }
    public String getViesRequestId() { return viesRequestId; }
    public String getViesResponse() { return viesResponse; }
    public String getErrorMessage() { return errorMessage; }
    public Integer getRetryCount() { return retryCount; }
    public LocalDateTime getValidationDate() { return validationDate; }
    public LocalDateTime getNextRetryAt() { return nextRetryAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setShop(Shop shop) { this.shop = shop; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }
    public void setShopifyOrderId(String shopifyOrderId) { this.shopifyOrderId = shopifyOrderId; }
    public void setVatNumber(String vatNumber) { this.vatNumber = vatNumber; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public void setVatNumberWithoutCountry(String vatNumberWithoutCountry) { this.vatNumberWithoutCountry = vatNumberWithoutCountry; }
    public void setValidationStatus(ValidationStatus validationStatus) { this.validationStatus = validationStatus; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }
    public void setViesRequestId(String viesRequestId) { this.viesRequestId = viesRequestId; }
    public void setViesResponse(String viesResponse) { this.viesResponse = viesResponse; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    public void setValidationDate(LocalDateTime validationDate) { this.validationDate = validationDate; }
    public void setNextRetryAt(LocalDateTime nextRetryAt) { this.nextRetryAt = nextRetryAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder
    public static VatValidationBuilder builder() { return new VatValidationBuilder(); }

    public static class VatValidationBuilder {
        private Long id;
        private Shop shop;
        private Long orderId;
        private String shopifyOrderId;
        private String vatNumber;
        private String countryCode;
        private String vatNumberWithoutCountry;
        private ValidationStatus validationStatus;
        private String companyName;
        private String companyAddress;
        private String viesRequestId;
        private String viesResponse;
        private String errorMessage;
        private Integer retryCount;
        private LocalDateTime validationDate;
        private LocalDateTime nextRetryAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public VatValidationBuilder id(Long id) { this.id = id; return this; }
        public VatValidationBuilder shop(Shop shop) { this.shop = shop; return this; }
        public VatValidationBuilder orderId(Long orderId) { this.orderId = orderId; return this; }
        public VatValidationBuilder shopifyOrderId(String shopifyOrderId) { this.shopifyOrderId = shopifyOrderId; return this; }
        public VatValidationBuilder vatNumber(String vatNumber) { this.vatNumber = vatNumber; return this; }
        public VatValidationBuilder countryCode(String countryCode) { this.countryCode = countryCode; return this; }
        public VatValidationBuilder vatNumberWithoutCountry(String vatNumberWithoutCountry) { this.vatNumberWithoutCountry = vatNumberWithoutCountry; return this; }
        public VatValidationBuilder validationStatus(ValidationStatus validationStatus) { this.validationStatus = validationStatus; return this; }
        public VatValidationBuilder companyName(String companyName) { this.companyName = companyName; return this; }
        public VatValidationBuilder companyAddress(String companyAddress) { this.companyAddress = companyAddress; return this; }
        public VatValidationBuilder viesRequestId(String viesRequestId) { this.viesRequestId = viesRequestId; return this; }
        public VatValidationBuilder viesResponse(String viesResponse) { this.viesResponse = viesResponse; return this; }
        public VatValidationBuilder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }
        public VatValidationBuilder retryCount(Integer retryCount) { this.retryCount = retryCount; return this; }
        public VatValidationBuilder validationDate(LocalDateTime validationDate) { this.validationDate = validationDate; return this; }
        public VatValidationBuilder nextRetryAt(LocalDateTime nextRetryAt) { this.nextRetryAt = nextRetryAt; return this; }
        public VatValidationBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public VatValidationBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public VatValidation build() {
            return new VatValidation(id, shop, orderId, shopifyOrderId, vatNumber, countryCode,
                    vatNumberWithoutCountry, validationStatus, companyName, companyAddress, viesRequestId,
                    viesResponse, errorMessage, retryCount, validationDate, nextRetryAt, createdAt, updatedAt);
        }
    }

    public enum ValidationStatus {
        VALID,
        INVALID,
        UNAVAILABLE,
        PENDING,
        ERROR,
        FORMAT_ERROR
    }

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
}
