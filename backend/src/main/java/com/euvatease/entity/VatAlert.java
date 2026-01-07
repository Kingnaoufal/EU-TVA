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
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "vat_alerts")
public class VatAlert {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    @Nonnull
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    @Nonnull
    private Severity severity;

    @Column(name = "title", nullable = false)
    @Nonnull
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    @Nullable
    private String message;

    @Column(name = "action_required")
    @Nullable
    private String actionRequired;

    @Column(name = "related_order_id")
    @Nullable
    private Long relatedOrderId;

    @Column(name = "related_country_code")
    @Nullable
    private String relatedCountryCode;

    @Column(name = "is_read")
    @Nullable
    private Boolean isRead;

    @Column(name = "is_resolved")
    @Nullable
    private Boolean isResolved;

    @Column(name = "email_sent")
    @Nullable
    private Boolean emailSent;

    @Column(name = "email_sent_at")
    @Nullable
    private LocalDateTime emailSentAt;

    @Column(name = "resolved_at")
    @Nullable
    private LocalDateTime resolvedAt;

    @Column(name = "created_at")
    @Nullable
    private LocalDateTime createdAt;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public VatAlert() {
    }

    public VatAlert(@Nullable Long id,
                    @Nonnull Shop shop,
                    @Nonnull AlertType alertType,
                    @Nonnull Severity severity,
                    @Nonnull String title,
                    @Nullable String message,
                    @Nullable String actionRequired,
                    @Nullable Long relatedOrderId,
                    @Nullable String relatedCountryCode,
                    @Nullable Boolean isRead,
                    @Nullable Boolean isResolved,
                    @Nullable Boolean emailSent,
                    @Nullable LocalDateTime emailSentAt,
                    @Nullable LocalDateTime resolvedAt,
                    @Nullable LocalDateTime createdAt) {
        this.id = id;
        this.shop = shop;
        this.alertType = alertType;
        this.severity = severity;
        this.title = title;
        this.message = message;
        this.actionRequired = actionRequired;
        this.relatedOrderId = relatedOrderId;
        this.relatedCountryCode = relatedCountryCode;
        this.isRead = isRead;
        this.isResolved = isResolved;
        this.emailSent = emailSent;
        this.emailSentAt = emailSentAt;
        this.resolvedAt = resolvedAt;
        this.createdAt = createdAt;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    public static VatAlertBuilder builder() {
        return new VatAlertBuilder();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRead == null) {
            isRead = false;
        }
        if (isResolved == null) {
            isResolved = false;
        }
        if (emailSent == null) {
            emailSent = false;
        }
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

    @Nonnull
    public AlertType getAlertType() {
        return alertType;
    }

    public void setAlertType(@Nonnull AlertType alertType) {
        this.alertType = alertType;
    }

    @Nonnull
    public Severity getSeverity() {
        return severity;
    }

    public void setSeverity(@Nonnull Severity severity) {
        this.severity = severity;
    }

    @Nonnull
    public String getTitle() {
        return title;
    }

    public void setTitle(@Nonnull String title) {
        this.title = title;
    }

    @Nullable
    public String getMessage() {
        return message;
    }

    public void setMessage(@Nullable String message) {
        this.message = message;
    }

    @Nullable
    public String getActionRequired() {
        return actionRequired;
    }

    public void setActionRequired(@Nullable String actionRequired) {
        this.actionRequired = actionRequired;
    }

    @Nullable
    public Long getRelatedOrderId() {
        return relatedOrderId;
    }

    public void setRelatedOrderId(@Nullable Long relatedOrderId) {
        this.relatedOrderId = relatedOrderId;
    }

    @Nullable
    public String getRelatedCountryCode() {
        return relatedCountryCode;
    }

    public void setRelatedCountryCode(@Nullable String relatedCountryCode) {
        this.relatedCountryCode = relatedCountryCode;
    }

    @Nullable
    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(@Nullable Boolean isRead) {
        this.isRead = isRead;
    }

    @Nullable
    public Boolean getIsResolved() {
        return isResolved;
    }

    public void setIsResolved(@Nullable Boolean isResolved) {
        this.isResolved = isResolved;
    }

    @Nullable
    public Boolean getEmailSent() {
        return emailSent;
    }

    public void setEmailSent(@Nullable Boolean emailSent) {
        this.emailSent = emailSent;
    }

    @Nullable
    public LocalDateTime getEmailSentAt() {
        return emailSentAt;
    }

    public void setEmailSentAt(@Nullable LocalDateTime emailSentAt) {
        this.emailSentAt = emailSentAt;
    }

    @Nullable
    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(@Nullable LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    @Nullable
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Nonnull
    public String getSeverityIcon() {
        if (severity == null) {
            return "ℹ️";
        }
        return switch (severity) {
            case INFO -> "ℹ️";
            case WARNING -> "⚠️";
            case ERROR -> "❌";
            case CRITICAL -> "🚨";
        };
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Nested Classes
    //~ ----------------------------------------------------------------------------------------------------------------

    public enum AlertType {
        OSS_THRESHOLD_WARNING,
        OSS_THRESHOLD_EXCEEDED,
        VAT_RATE_ERROR,
        VAT_MISSING,
        SHOPIFY_CONFIG_ERROR,
        QUARTERLY_REMINDER,
        SUBSCRIPTION_EXPIRING,
        B2B_VAT_INVALID,
        ORDER_ERROR
    }

    public enum Severity {
        INFO,
        WARNING,
        ERROR,
        CRITICAL
    }

    public static class VatAlertBuilder {

        @Nullable
        private Long id;

        @Nullable
        private Shop shop;

        @Nullable
        private AlertType alertType;

        @Nullable
        private Severity severity;

        @Nullable
        private String title;

        @Nullable
        private String message;

        @Nullable
        private String actionRequired;

        @Nullable
        private Long relatedOrderId;

        @Nullable
        private String relatedCountryCode;

        @Nullable
        private Boolean isRead;

        @Nullable
        private Boolean isResolved;

        @Nullable
        private Boolean emailSent;

        @Nullable
        private LocalDateTime emailSentAt;

        @Nullable
        private LocalDateTime resolvedAt;

        @Nullable
        private LocalDateTime createdAt;

        @Nonnull
        public VatAlertBuilder id(@Nullable Long id) {
            this.id = id;
            return this;
        }

        @Nonnull
        public VatAlertBuilder shop(@Nonnull Shop shop) {
            this.shop = shop;
            return this;
        }

        @Nonnull
        public VatAlertBuilder alertType(@Nonnull AlertType alertType) {
            this.alertType = alertType;
            return this;
        }

        @Nonnull
        public VatAlertBuilder severity(@Nonnull Severity severity) {
            this.severity = severity;
            return this;
        }

        @Nonnull
        public VatAlertBuilder title(@Nonnull String title) {
            this.title = title;
            return this;
        }

        @Nonnull
        public VatAlertBuilder message(@Nullable String message) {
            this.message = message;
            return this;
        }

        @Nonnull
        public VatAlertBuilder actionRequired(@Nullable String actionRequired) {
            this.actionRequired = actionRequired;
            return this;
        }

        @Nonnull
        public VatAlertBuilder relatedOrderId(@Nullable Long relatedOrderId) {
            this.relatedOrderId = relatedOrderId;
            return this;
        }

        @Nonnull
        public VatAlertBuilder relatedCountryCode(@Nullable String relatedCountryCode) {
            this.relatedCountryCode = relatedCountryCode;
            return this;
        }

        @Nonnull
        public VatAlertBuilder isRead(@Nullable Boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        @Nonnull
        public VatAlertBuilder isResolved(@Nullable Boolean isResolved) {
            this.isResolved = isResolved;
            return this;
        }

        @Nonnull
        public VatAlertBuilder emailSent(@Nullable Boolean emailSent) {
            this.emailSent = emailSent;
            return this;
        }

        @Nonnull
        public VatAlertBuilder emailSentAt(@Nullable LocalDateTime emailSentAt) {
            this.emailSentAt = emailSentAt;
            return this;
        }

        @Nonnull
        public VatAlertBuilder resolvedAt(@Nullable LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }

        @Nonnull
        public VatAlertBuilder createdAt(@Nullable LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Nonnull
        public VatAlert build() {
            return new VatAlert(id, shop, alertType, severity, title, message, actionRequired, relatedOrderId,
                    relatedCountryCode, isRead, isResolved, emailSent, emailSentAt, resolvedAt, createdAt);
        }
    }
}
