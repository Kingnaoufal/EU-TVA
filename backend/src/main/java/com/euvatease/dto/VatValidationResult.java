package com.euvatease.dto;

import com.euvatease.entity.VatValidation.ValidationStatus;
import java.time.LocalDateTime;

/**
 * Résultat de validation TVA intracommunautaire.
 */
public class VatValidationResult {

    private Long id;
    private String vatNumber;
    private String countryCode;
    private ValidationStatus status;
    private String companyName;
    private String companyAddress;
    private LocalDateTime validationDate;
    private String userMessage;
    private boolean isLegalProof;
    private String requestId;

    public VatValidationResult() {
    }

    public VatValidationResult(Long id, String vatNumber, String countryCode, ValidationStatus status,
                               String companyName, String companyAddress, LocalDateTime validationDate,
                               String userMessage, boolean isLegalProof, String requestId) {
        this.id = id;
        this.vatNumber = vatNumber;
        this.countryCode = countryCode;
        this.status = status;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.validationDate = validationDate;
        this.userMessage = userMessage;
        this.isLegalProof = isLegalProof;
        this.requestId = requestId;
    }

    // Getters
    public Long getId() { return id; }
    public String getVatNumber() { return vatNumber; }
    public String getCountryCode() { return countryCode; }
    public ValidationStatus getStatus() { return status; }
    public String getCompanyName() { return companyName; }
    public String getCompanyAddress() { return companyAddress; }
    public LocalDateTime getValidationDate() { return validationDate; }
    public String getUserMessage() { return userMessage; }
    public boolean isLegalProof() { return isLegalProof; }
    public String getRequestId() { return requestId; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setVatNumber(String vatNumber) { this.vatNumber = vatNumber; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public void setStatus(ValidationStatus status) { this.status = status; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setCompanyAddress(String companyAddress) { this.companyAddress = companyAddress; }
    public void setValidationDate(LocalDateTime validationDate) { this.validationDate = validationDate; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }
    public void setLegalProof(boolean isLegalProof) { this.isLegalProof = isLegalProof; }
    public void setRequestId(String requestId) { this.requestId = requestId; }

    // Builder
    public static VatValidationResultBuilder builder() { return new VatValidationResultBuilder(); }

    public static class VatValidationResultBuilder {
        private Long id;
        private String vatNumber;
        private String countryCode;
        private ValidationStatus status;
        private String companyName;
        private String companyAddress;
        private LocalDateTime validationDate;
        private String userMessage;
        private boolean isLegalProof;
        private String requestId;

        public VatValidationResultBuilder id(Long id) { this.id = id; return this; }
        public VatValidationResultBuilder vatNumber(String vatNumber) { this.vatNumber = vatNumber; return this; }
        public VatValidationResultBuilder countryCode(String countryCode) { this.countryCode = countryCode; return this; }
        public VatValidationResultBuilder status(ValidationStatus status) { this.status = status; return this; }
        public VatValidationResultBuilder companyName(String companyName) { this.companyName = companyName; return this; }
        public VatValidationResultBuilder companyAddress(String companyAddress) { this.companyAddress = companyAddress; return this; }
        public VatValidationResultBuilder validationDate(LocalDateTime validationDate) { this.validationDate = validationDate; return this; }
        public VatValidationResultBuilder userMessage(String userMessage) { this.userMessage = userMessage; return this; }
        public VatValidationResultBuilder isLegalProof(boolean isLegalProof) { this.isLegalProof = isLegalProof; return this; }
        public VatValidationResultBuilder requestId(String requestId) { this.requestId = requestId; return this; }

        public VatValidationResult build() {
            return new VatValidationResult(id, vatNumber, countryCode, status, companyName, companyAddress,
                    validationDate, userMessage, isLegalProof, requestId);
        }
    }

    // Business methods
    public boolean isValid() {
        return status == ValidationStatus.VALID;
    }

    public boolean allowsB2bExemption() {
        return isValid() && isLegalProof;
    }

    public String getStatusIcon() {
        return switch (status) {
            case VALID -> "✅";
            case INVALID -> "❌";
            case UNAVAILABLE, PENDING -> "⚠️";
            case ERROR, FORMAT_ERROR -> "❌";
        };
    }

    public String getStatusColor() {
        return switch (status) {
            case VALID -> "success";
            case INVALID, ERROR, FORMAT_ERROR -> "critical";
            case UNAVAILABLE, PENDING -> "warning";
        };
    }
}
