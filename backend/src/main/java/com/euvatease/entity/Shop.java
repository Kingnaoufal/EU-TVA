package com.euvatease.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "shops")
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "shopify_domain", unique = true, nullable = false)
    private String shopifyDomain;

    @Column(name = "shopify_shop_id", unique = true)
    private String shopifyShopId;

    @Column(name = "shop_name")
    private String shopName;

    @Column(name = "email")
    private String email;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "currency")
    private String currency;

    @Column(name = "access_token", length = 512)
    private String accessToken;

    @Column(name = "vat_number")
    private String vatNumber;

    @Column(name = "oss_registered")
    private Boolean ossRegistered;

    @Column(name = "oss_country_code")
    private String ossCountryCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status")
    private SubscriptionStatus subscriptionStatus;

    @Column(name = "subscription_plan")
    private String subscriptionPlan;

    @Column(name = "shopify_charge_id")
    private String shopifyChargeId;

    @Column(name = "trial_ends_at")
    private LocalDateTime trialEndsAt;

    @Column(name = "subscription_ends_at")
    private LocalDateTime subscriptionEndsAt;

    @Column(name = "installed_at")
    private LocalDateTime installedAt;

    @Column(name = "uninstalled_at")
    private LocalDateTime uninstalledAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "alert_email_enabled")
    private Boolean alertEmailEnabled;

    @Column(name = "oss_threshold_alert_sent")
    private Boolean ossThresholdAlertSent;

    public Shop() {
    }

    public Shop(Long id, String shopifyDomain, String shopifyShopId, String shopName, String email,
                String countryCode, String currency, String accessToken, String vatNumber,
                Boolean ossRegistered, String ossCountryCode, SubscriptionStatus subscriptionStatus,
                String subscriptionPlan, String shopifyChargeId, LocalDateTime trialEndsAt,
                LocalDateTime subscriptionEndsAt, LocalDateTime installedAt, LocalDateTime uninstalledAt,
                LocalDateTime createdAt, LocalDateTime updatedAt, Boolean isActive,
                Boolean alertEmailEnabled, Boolean ossThresholdAlertSent) {
        this.id = id;
        this.shopifyDomain = shopifyDomain;
        this.shopifyShopId = shopifyShopId;
        this.shopName = shopName;
        this.email = email;
        this.countryCode = countryCode;
        this.currency = currency;
        this.accessToken = accessToken;
        this.vatNumber = vatNumber;
        this.ossRegistered = ossRegistered;
        this.ossCountryCode = ossCountryCode;
        this.subscriptionStatus = subscriptionStatus;
        this.subscriptionPlan = subscriptionPlan;
        this.shopifyChargeId = shopifyChargeId;
        this.trialEndsAt = trialEndsAt;
        this.subscriptionEndsAt = subscriptionEndsAt;
        this.installedAt = installedAt;
        this.uninstalledAt = uninstalledAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isActive = isActive;
        this.alertEmailEnabled = alertEmailEnabled;
        this.ossThresholdAlertSent = ossThresholdAlertSent;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) isActive = true;
        if (alertEmailEnabled == null) alertEmailEnabled = true;
        if (ossThresholdAlertSent == null) ossThresholdAlertSent = false;
        if (ossRegistered == null) ossRegistered = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getShopifyDomain() { return shopifyDomain; }
    public String getShopifyShopId() { return shopifyShopId; }
    public String getShopName() { return shopName; }
    public String getEmail() { return email; }
    public String getCountryCode() { return countryCode; }
    public String getCurrency() { return currency; }
    public String getAccessToken() { return accessToken; }
    public String getVatNumber() { return vatNumber; }
    public Boolean getOssRegistered() { return ossRegistered; }
    public String getOssCountryCode() { return ossCountryCode; }
    public SubscriptionStatus getSubscriptionStatus() { return subscriptionStatus; }
    public String getSubscriptionPlan() { return subscriptionPlan; }
    public String getShopifyChargeId() { return shopifyChargeId; }
    public LocalDateTime getTrialEndsAt() { return trialEndsAt; }
    public LocalDateTime getSubscriptionEndsAt() { return subscriptionEndsAt; }
    public LocalDateTime getInstalledAt() { return installedAt; }
    public LocalDateTime getUninstalledAt() { return uninstalledAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Boolean getIsActive() { return isActive; }
    public Boolean getAlertEmailEnabled() { return alertEmailEnabled; }
    public Boolean getOssThresholdAlertSent() { return ossThresholdAlertSent; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setShopifyDomain(String shopifyDomain) { this.shopifyDomain = shopifyDomain; }
    public void setShopifyShopId(String shopifyShopId) { this.shopifyShopId = shopifyShopId; }
    public void setShopName(String shopName) { this.shopName = shopName; }
    public void setEmail(String email) { this.email = email; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public void setVatNumber(String vatNumber) { this.vatNumber = vatNumber; }
    public void setOssRegistered(Boolean ossRegistered) { this.ossRegistered = ossRegistered; }
    public void setOssCountryCode(String ossCountryCode) { this.ossCountryCode = ossCountryCode; }
    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) { this.subscriptionStatus = subscriptionStatus; }
    public void setSubscriptionPlan(String subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; }
    public void setShopifyChargeId(String shopifyChargeId) { this.shopifyChargeId = shopifyChargeId; }
    public void setTrialEndsAt(LocalDateTime trialEndsAt) { this.trialEndsAt = trialEndsAt; }
    public void setSubscriptionEndsAt(LocalDateTime subscriptionEndsAt) { this.subscriptionEndsAt = subscriptionEndsAt; }
    public void setInstalledAt(LocalDateTime installedAt) { this.installedAt = installedAt; }
    public void setUninstalledAt(LocalDateTime uninstalledAt) { this.uninstalledAt = uninstalledAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    public void setAlertEmailEnabled(Boolean alertEmailEnabled) { this.alertEmailEnabled = alertEmailEnabled; }
    public void setOssThresholdAlertSent(Boolean ossThresholdAlertSent) { this.ossThresholdAlertSent = ossThresholdAlertSent; }

    // Builder
    public static ShopBuilder builder() { return new ShopBuilder(); }

    public static class ShopBuilder {
        private Long id;
        private String shopifyDomain;
        private String shopifyShopId;
        private String shopName;
        private String email;
        private String countryCode;
        private String currency;
        private String accessToken;
        private String vatNumber;
        private Boolean ossRegistered;
        private String ossCountryCode;
        private SubscriptionStatus subscriptionStatus;
        private String subscriptionPlan;
        private String shopifyChargeId;
        private LocalDateTime trialEndsAt;
        private LocalDateTime subscriptionEndsAt;
        private LocalDateTime installedAt;
        private LocalDateTime uninstalledAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean isActive;
        private Boolean alertEmailEnabled;
        private Boolean ossThresholdAlertSent;

        public ShopBuilder id(Long id) { this.id = id; return this; }
        public ShopBuilder shopifyDomain(String shopifyDomain) { this.shopifyDomain = shopifyDomain; return this; }
        public ShopBuilder shopifyShopId(String shopifyShopId) { this.shopifyShopId = shopifyShopId; return this; }
        public ShopBuilder shopName(String shopName) { this.shopName = shopName; return this; }
        public ShopBuilder email(String email) { this.email = email; return this; }
        public ShopBuilder countryCode(String countryCode) { this.countryCode = countryCode; return this; }
        public ShopBuilder currency(String currency) { this.currency = currency; return this; }
        public ShopBuilder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public ShopBuilder vatNumber(String vatNumber) { this.vatNumber = vatNumber; return this; }
        public ShopBuilder ossRegistered(Boolean ossRegistered) { this.ossRegistered = ossRegistered; return this; }
        public ShopBuilder ossCountryCode(String ossCountryCode) { this.ossCountryCode = ossCountryCode; return this; }
        public ShopBuilder subscriptionStatus(SubscriptionStatus subscriptionStatus) { this.subscriptionStatus = subscriptionStatus; return this; }
        public ShopBuilder subscriptionPlan(String subscriptionPlan) { this.subscriptionPlan = subscriptionPlan; return this; }
        public ShopBuilder shopifyChargeId(String shopifyChargeId) { this.shopifyChargeId = shopifyChargeId; return this; }
        public ShopBuilder trialEndsAt(LocalDateTime trialEndsAt) { this.trialEndsAt = trialEndsAt; return this; }
        public ShopBuilder subscriptionEndsAt(LocalDateTime subscriptionEndsAt) { this.subscriptionEndsAt = subscriptionEndsAt; return this; }
        public ShopBuilder installedAt(LocalDateTime installedAt) { this.installedAt = installedAt; return this; }
        public ShopBuilder uninstalledAt(LocalDateTime uninstalledAt) { this.uninstalledAt = uninstalledAt; return this; }
        public ShopBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ShopBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public ShopBuilder isActive(Boolean isActive) { this.isActive = isActive; return this; }
        public ShopBuilder alertEmailEnabled(Boolean alertEmailEnabled) { this.alertEmailEnabled = alertEmailEnabled; return this; }
        public ShopBuilder ossThresholdAlertSent(Boolean ossThresholdAlertSent) { this.ossThresholdAlertSent = ossThresholdAlertSent; return this; }

        public Shop build() {
            return new Shop(id, shopifyDomain, shopifyShopId, shopName, email, countryCode, currency,
                    accessToken, vatNumber, ossRegistered, ossCountryCode, subscriptionStatus,
                    subscriptionPlan, shopifyChargeId, trialEndsAt, subscriptionEndsAt, installedAt,
                    uninstalledAt, createdAt, updatedAt, isActive, alertEmailEnabled, ossThresholdAlertSent);
        }
    }

    public enum SubscriptionStatus {
        TRIAL, ACTIVE, CANCELLED, EXPIRED, PENDING
    }
}
