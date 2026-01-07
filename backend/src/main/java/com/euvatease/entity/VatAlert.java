package com.euvatease.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vat_alerts")
public class VatAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private Severity severity;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "action_required")
    private String actionRequired;

    @Column(name = "related_order_id")
    private Long relatedOrderId;

    @Column(name = "related_country_code")
    private String relatedCountryCode;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "is_resolved")
    private Boolean isResolved;

    @Column(name = "email_sent")
    private Boolean emailSent;

    @Column(name = "email_sent_at")
    private LocalDateTime emailSentAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public VatAlert() {
    }

    public VatAlert(Long id, Shop shop, AlertType alertType, Severity severity, String title, String message,
                    String actionRequired, Long relatedOrderId, String relatedCountryCode, Boolean isRead,
                    Boolean isResolved, Boolean emailSent, LocalDateTime emailSentAt, LocalDateTime resolvedAt,
                    LocalDateTime createdAt) {
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (isRead == null) isRead = false;
        if (isResolved == null) isResolved = false;
        if (emailSent == null) emailSent = false;
    }

    // Getters
    public Long getId() { return id; }
    public Shop getShop() { return shop; }
    public AlertType getAlertType() { return alertType; }
    public Severity getSeverity() { return severity; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public String getActionRequired() { return actionRequired; }
    public Long getRelatedOrderId() { return relatedOrderId; }
    public String getRelatedCountryCode() { return relatedCountryCode; }
    public Boolean getIsRead() { return isRead; }
    public Boolean getIsResolved() { return isResolved; }
    public Boolean getEmailSent() { return emailSent; }
    public LocalDateTime getEmailSentAt() { return emailSentAt; }
    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setShop(Shop shop) { this.shop = shop; }
    public void setAlertType(AlertType alertType) { this.alertType = alertType; }
    public void setSeverity(Severity severity) { this.severity = severity; }
    public void setTitle(String title) { this.title = title; }
    public void setMessage(String message) { this.message = message; }
    public void setActionRequired(String actionRequired) { this.actionRequired = actionRequired; }
    public void setRelatedOrderId(Long relatedOrderId) { this.relatedOrderId = relatedOrderId; }
    public void setRelatedCountryCode(String relatedCountryCode) { this.relatedCountryCode = relatedCountryCode; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    public void setIsResolved(Boolean isResolved) { this.isResolved = isResolved; }
    public void setEmailSent(Boolean emailSent) { this.emailSent = emailSent; }
    public void setEmailSentAt(LocalDateTime emailSentAt) { this.emailSentAt = emailSentAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static VatAlertBuilder builder() { return new VatAlertBuilder(); }

    public static class VatAlertBuilder {
        private Long id;
        private Shop shop;
        private AlertType alertType;
        private Severity severity;
        private String title;
        private String message;
        private String actionRequired;
        private Long relatedOrderId;
        private String relatedCountryCode;
        private Boolean isRead;
        private Boolean isResolved;
        private Boolean emailSent;
        private LocalDateTime emailSentAt;
        private LocalDateTime resolvedAt;
        private LocalDateTime createdAt;

        public VatAlertBuilder id(Long id) { this.id = id; return this; }
        public VatAlertBuilder shop(Shop shop) { this.shop = shop; return this; }
        public VatAlertBuilder alertType(AlertType alertType) { this.alertType = alertType; return this; }
        public VatAlertBuilder severity(Severity severity) { this.severity = severity; return this; }
        public VatAlertBuilder title(String title) { this.title = title; return this; }
        public VatAlertBuilder message(String message) { this.message = message; return this; }
        public VatAlertBuilder actionRequired(String actionRequired) { this.actionRequired = actionRequired; return this; }
        public VatAlertBuilder relatedOrderId(Long relatedOrderId) { this.relatedOrderId = relatedOrderId; return this; }
        public VatAlertBuilder relatedCountryCode(String relatedCountryCode) { this.relatedCountryCode = relatedCountryCode; return this; }
        public VatAlertBuilder isRead(Boolean isRead) { this.isRead = isRead; return this; }
        public VatAlertBuilder isResolved(Boolean isResolved) { this.isResolved = isResolved; return this; }
        public VatAlertBuilder emailSent(Boolean emailSent) { this.emailSent = emailSent; return this; }
        public VatAlertBuilder emailSentAt(LocalDateTime emailSentAt) { this.emailSentAt = emailSentAt; return this; }
        public VatAlertBuilder resolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; return this; }
        public VatAlertBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public VatAlert build() {
            return new VatAlert(id, shop, alertType, severity, title, message, actionRequired, relatedOrderId,
                    relatedCountryCode, isRead, isResolved, emailSent, emailSentAt, resolvedAt, createdAt);
        }
    }

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

    public String getSeverityIcon() {
        if (severity == null) return "ℹ️";
        return switch (severity) {
            case INFO -> "ℹ️";
            case WARNING -> "⚠️";
            case ERROR -> "❌";
            case CRITICAL -> "🚨";
        };
    }
}
