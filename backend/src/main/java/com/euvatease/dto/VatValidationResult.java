package com.euvatease.dto;

import com.euvatease.entity.VatValidation.ValidationStatus;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.time.LocalDateTime;

/**
 * Résultat de validation TVA intracommunautaire.
 */
public class VatValidationResult {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nullable
    private Long id;

    @Nullable
    private String vatNumber;

    @Nullable
    private String countryCode;

    @Nullable
    private ValidationStatus status;

    @Nullable
    private String companyName;

    @Nullable
    private String companyAddress;

    @Nullable
    private LocalDateTime validationDate;

    @Nullable
    private String userMessage;

    private boolean isLegalProof;

    @Nullable
    private String requestId;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public VatValidationResult() {
    }

    public VatValidationResult(@Nullable Long id,
                               @Nullable String vatNumber,
                               @Nullable String countryCode,
                               @Nullable ValidationStatus status,
                               @Nullable String companyName,
                               @Nullable String companyAddress,
                               @Nullable LocalDateTime validationDate,
                               @Nullable String userMessage,
                               boolean isLegalProof,
                               @Nullable String requestId) {
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

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    public static VatValidationResultBuilder builder() {
        return new VatValidationResultBuilder();
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @Nullable
    public String getVatNumber() {
        return vatNumber;
    }

    public void setVatNumber(@Nullable String vatNumber) {
        this.vatNumber = vatNumber;
    }

    @Nullable
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(@Nullable String countryCode) {
        this.countryCode = countryCode;
    }

    @Nullable
    public ValidationStatus getStatus() {
        return status;
    }

    public void setStatus(@Nullable ValidationStatus status) {
        this.status = status;
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
    public LocalDateTime getValidationDate() {
        return validationDate;
    }

    public void setValidationDate(@Nullable LocalDateTime validationDate) {
        this.validationDate = validationDate;
    }

    @Nullable
    public String getUserMessage() {
        return userMessage;
    }

    public void setUserMessage(@Nullable String userMessage) {
        this.userMessage = userMessage;
    }

    public boolean isLegalProof() {
        return isLegalProof;
    }

    public void setLegalProof(boolean isLegalProof) {
        this.isLegalProof = isLegalProof;
    }

    @Nullable
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(@Nullable String requestId) {
        this.requestId = requestId;
    }

    public boolean isValid() {
        return status == ValidationStatus.VALID;
    }

    public boolean allowsB2bExemption() {
        return isValid() && isLegalProof;
    }

    @Nonnull
    public String getStatusIcon() {
        if (status == null) {
            return "⚠️";
        }
        return switch (status) {
            case VALID -> "✅";
            case INVALID -> "❌";
            case UNAVAILABLE, PENDING -> "⚠️";
            case ERROR, FORMAT_ERROR -> "❌";
        };
    }

    @Nonnull
    public String getStatusColor() {
        if (status == null) {
            return "warning";
        }
        return switch (status) {
            case VALID -> "success";
            case INVALID, ERROR, FORMAT_ERROR -> "critical";
            case UNAVAILABLE, PENDING -> "warning";
        };
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Nested Classes
    //~ ----------------------------------------------------------------------------------------------------------------

    public static class VatValidationResultBuilder {

        @Nullable
        private Long id;

        @Nullable
        private String vatNumber;

        @Nullable
        private String countryCode;

        @Nullable
        private ValidationStatus status;

        @Nullable
        private String companyName;

        @Nullable
        private String companyAddress;

        @Nullable
        private LocalDateTime validationDate;

        @Nullable
        private String userMessage;

        private boolean isLegalProof;

        @Nullable
        private String requestId;

        @Nonnull
        public VatValidationResultBuilder id(@Nullable Long id) {
            this.id = id;
            return this;
        }

        @Nonnull
        public VatValidationResultBuilder vatNumber(@Nullable String vatNumber) {
            this.vatNumber = vatNumber;
            return this;
        }

        @Nonnull
        public VatValidationResultBuilder countryCode(@Nullable String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        @Nonnull
        public VatValidationResultBuilder status(@Nullable ValidationStatus status) {
            this.status = status;
            return this;
        }

        @Nonnull
        public VatValidationResultBuilder companyName(@Nullable String companyName) {
            this.companyName = companyName;
            return this;
        }

        @Nonnull
        public VatValidationResultBuilder companyAddress(@Nullable String companyAddress) {
            this.companyAddress = companyAddress;
            return this;
        }

        @Nonnull
        public VatValidationResultBuilder validationDate(@Nullable LocalDateTime validationDate) {
            this.validationDate = validationDate;
            return this;
        }

        @Nonnull
        public VatValidationResultBuilder userMessage(@Nullable String userMessage) {
            this.userMessage = userMessage;
            return this;
        }

        @Nonnull
        public VatValidationResultBuilder isLegalProof(boolean isLegalProof) {
            this.isLegalProof = isLegalProof;
            return this;
        }

        @Nonnull
        public VatValidationResultBuilder requestId(@Nullable String requestId) {
            this.requestId = requestId;
            return this;
        }

        @Nonnull
        public VatValidationResult build() {
            return new VatValidationResult(id, vatNumber, countryCode, status, companyName, companyAddress,
                    validationDate, userMessage, isLegalProof, requestId);
        }
    }
}
