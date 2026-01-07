package com.euvatease.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "shops")
public class Shop {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Nullable
    private Long id;

    @Column(name = "shopify_domain", unique = true, nullable = false)
    @Nonnull
    private String shopifyDomain;

    @Column(name = "shopify_shop_id", unique = true)
    @Nullable
    private String shopifyShopId;

    @Column(name = "shop_name")
    @Nullable
    private String shopName;

    @Column(name = "email")
    @Nullable
    private String email;

    @Column(name = "country_code")
    @Nullable
    private String countryCode;

    @Column(name = "currency")
    @Nullable
    private String currency;

    @Column(name = "access_token", length = 512)
    @Nullable
    private String accessToken;

    @Column(name = "vat_number")
    @Nullable
    private String vatNumber;

    @Column(name = "oss_registered")
    @Nullable
    private Boolean ossRegistered;

    @Column(name = "oss_country_code")
    @Nullable
    private String ossCountryCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_status")
    @Nullable
    private SubscriptionStatus subscriptionStatus;

    @Column(name = "subscription_plan")
    @Nullable
    private String subscriptionPlan;

    @Column(name = "shopify_charge_id")
    @Nullable
    private String shopifyChargeId;

    @Column(name = "trial_ends_at")
    @Nullable
    private LocalDateTime trialEndsAt;

    @Column(name = "subscription_ends_at")
    @Nullable
    private LocalDateTime subscriptionEndsAt;

    @Column(name = "installed_at")
    @Nullable
    private LocalDateTime installedAt;

    @Column(name = "uninstalled_at")
    @Nullable
    private LocalDateTime uninstalledAt;

    @Column(name = "created_at")
    @Nullable
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Nullable
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    @Nullable
    private Boolean isActive;

    @Column(name = "alert_email_enabled")
    @Nullable
    private Boolean alertEmailEnabled;

    @Column(name = "oss_threshold_alert_sent")
    @Nullable
    private Boolean ossThresholdAlertSent;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public Shop() {
    }

    public Shop(@Nullable Long id,
                @Nonnull String shopifyDomain,
                @Nullable String shopifyShopId,
                @Nullable String shopName,
                @Nullable String email,
                @Nullable String countryCode,
                @Nullable String currency,
                @Nullable String accessToken,
                @Nullable String vatNumber,
                @Nullable Boolean ossRegistered,
                @Nullable String ossCountryCode,
                @Nullable SubscriptionStatus subscriptionStatus,
                @Nullable String subscriptionPlan,
                @Nullable String shopifyChargeId,
                @Nullable LocalDateTime trialEndsAt,
                @Nullable LocalDateTime subscriptionEndsAt,
                @Nullable LocalDateTime installedAt,
                @Nullable LocalDateTime uninstalledAt,
                @Nullable LocalDateTime createdAt,
                @Nullable LocalDateTime updatedAt,
                @Nullable Boolean isActive,
                @Nullable Boolean alertEmailEnabled,
                @Nullable Boolean ossThresholdAlertSent) {
        this.id = id;
        this.shopifyDomain = Objects.requireNonNull(shopifyDomain, "shopifyDomain must not be null");
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

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static ShopBuilder builder() {
        return new ShopBuilder();
    }

    @Nullable
    public String getAccessToken() {
        return accessToken;
    }

    @Nullable
    public Boolean getAlertEmailEnabled() {
        return alertEmailEnabled;
    }

    @Nullable
    public String getCountryCode() {
        return countryCode;
    }

    @Nullable
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Nullable
    public String getCurrency() {
        return currency;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    @Nullable
    public LocalDateTime getInstalledAt() {
        return installedAt;
    }

    @Nullable
    public Boolean getIsActive() {
        return isActive;
    }

    @Nullable
    public String getOssCountryCode() {
        return ossCountryCode;
    }

    @Nullable
    public Boolean getOssRegistered() {
        return ossRegistered;
    }

    @Nullable
    public Boolean getOssThresholdAlertSent() {
        return ossThresholdAlertSent;
    }

    @Nullable
    public String getShopifyChargeId() {
        return shopifyChargeId;
    }

    @Nonnull
    public String getShopifyDomain() {
        return shopifyDomain;
    }

    @Nullable
    public String getShopifyShopId() {
        return shopifyShopId;
    }

    @Nullable
    public String getShopName() {
        return shopName;
    }

    @Nullable
    public LocalDateTime getSubscriptionEndsAt() {
        return subscriptionEndsAt;
    }

    @Nullable
    public String getSubscriptionPlan() {
        return subscriptionPlan;
    }

    @Nullable
    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    @Nullable
    public LocalDateTime getTrialEndsAt() {
        return trialEndsAt;
    }

    @Nullable
    public LocalDateTime getUninstalledAt() {
        return uninstalledAt;
    }

    @Nullable
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Nullable
    public String getVatNumber() {
        return vatNumber;
    }

    public void setAccessToken(@Nullable String accessToken) {
        this.accessToken = accessToken;
    }

    public void setAlertEmailEnabled(@Nullable Boolean alertEmailEnabled) {
        this.alertEmailEnabled = alertEmailEnabled;
    }

    public void setCountryCode(@Nullable String countryCode) {
        this.countryCode = countryCode;
    }

    public void setCreatedAt(@Nullable LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCurrency(@Nullable String currency) {
        this.currency = currency;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public void setInstalledAt(@Nullable LocalDateTime installedAt) {
        this.installedAt = installedAt;
    }

    public void setIsActive(@Nullable Boolean isActive) {
        this.isActive = isActive;
    }

    public void setOssCountryCode(@Nullable String ossCountryCode) {
        this.ossCountryCode = ossCountryCode;
    }

    public void setOssRegistered(@Nullable Boolean ossRegistered) {
        this.ossRegistered = ossRegistered;
    }

    public void setOssThresholdAlertSent(@Nullable Boolean ossThresholdAlertSent) {
        this.ossThresholdAlertSent = ossThresholdAlertSent;
    }

    public void setShopifyChargeId(@Nullable String shopifyChargeId) {
        this.shopifyChargeId = shopifyChargeId;
    }

    public void setShopifyDomain(@Nonnull String shopifyDomain) {
        this.shopifyDomain = Objects.requireNonNull(shopifyDomain, "shopifyDomain must not be null");
    }

    public void setShopifyShopId(@Nullable String shopifyShopId) {
        this.shopifyShopId = shopifyShopId;
    }

    public void setShopName(@Nullable String shopName) {
        this.shopName = shopName;
    }

    public void setSubscriptionEndsAt(@Nullable LocalDateTime subscriptionEndsAt) {
        this.subscriptionEndsAt = subscriptionEndsAt;
    }

    public void setSubscriptionPlan(@Nullable String subscriptionPlan) {
        this.subscriptionPlan = subscriptionPlan;
    }

    public void setSubscriptionStatus(@Nullable SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public void setTrialEndsAt(@Nullable LocalDateTime trialEndsAt) {
        this.trialEndsAt = trialEndsAt;
    }

    public void setUninstalledAt(@Nullable LocalDateTime uninstalledAt) {
        this.uninstalledAt = uninstalledAt;
    }

    public void setUpdatedAt(@Nullable LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setVatNumber(@Nullable String vatNumber) {
        this.vatNumber = vatNumber;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
        if (alertEmailEnabled == null) {
            alertEmailEnabled = true;
        }
        if (ossThresholdAlertSent == null) {
            ossThresholdAlertSent = false;
        }
        if (ossRegistered == null) {
            ossRegistered = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Nested Classes
    //~ ----------------------------------------------------------------------------------------------------------------

    public enum SubscriptionStatus {
        TRIAL,
        ACTIVE,
        CANCELLED,
        EXPIRED,
        PENDING
    }

    public static class ShopBuilder {

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Instance fields
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nullable
        private Long id;

        @Nullable
        private String shopifyDomain;

        @Nullable
        private String shopifyShopId;

        @Nullable
        private String shopName;

        @Nullable
        private String email;

        @Nullable
        private String countryCode;

        @Nullable
        private String currency;

        @Nullable
        private String accessToken;

        @Nullable
        private String vatNumber;

        @Nullable
        private Boolean ossRegistered;

        @Nullable
        private String ossCountryCode;

        @Nullable
        private SubscriptionStatus subscriptionStatus;

        @Nullable
        private String subscriptionPlan;

        @Nullable
        private String shopifyChargeId;

        @Nullable
        private LocalDateTime trialEndsAt;

        @Nullable
        private LocalDateTime subscriptionEndsAt;

        @Nullable
        private LocalDateTime installedAt;

        @Nullable
        private LocalDateTime uninstalledAt;

        @Nullable
        private LocalDateTime createdAt;

        @Nullable
        private LocalDateTime updatedAt;

        @Nullable
        private Boolean isActive;

        @Nullable
        private Boolean alertEmailEnabled;

        @Nullable
        private Boolean ossThresholdAlertSent;

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Methods
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nonnull
        public ShopBuilder accessToken(@Nullable String accessToken) {
            this.accessToken = accessToken;
            return this;
        }

        @Nonnull
        public ShopBuilder alertEmailEnabled(@Nullable Boolean alertEmailEnabled) {
            this.alertEmailEnabled = alertEmailEnabled;
            return this;
        }

        @Nonnull
        public Shop build() {
            return new Shop(id, shopifyDomain, shopifyShopId, shopName, email, countryCode, currency,
                    accessToken, vatNumber, ossRegistered, ossCountryCode, subscriptionStatus,
                    subscriptionPlan, shopifyChargeId, trialEndsAt, subscriptionEndsAt, installedAt,
                    uninstalledAt, createdAt, updatedAt, isActive, alertEmailEnabled, ossThresholdAlertSent);
        }

        @Nonnull
        public ShopBuilder countryCode(@Nullable String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        @Nonnull
        public ShopBuilder createdAt(@Nullable LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Nonnull
        public ShopBuilder currency(@Nullable String currency) {
            this.currency = currency;
            return this;
        }

        @Nonnull
        public ShopBuilder email(@Nullable String email) {
            this.email = email;
            return this;
        }

        @Nonnull
        public ShopBuilder id(@Nullable Long id) {
            this.id = id;
            return this;
        }

        @Nonnull
        public ShopBuilder installedAt(@Nullable LocalDateTime installedAt) {
            this.installedAt = installedAt;
            return this;
        }

        @Nonnull
        public ShopBuilder isActive(@Nullable Boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        @Nonnull
        public ShopBuilder ossCountryCode(@Nullable String ossCountryCode) {
            this.ossCountryCode = ossCountryCode;
            return this;
        }

        @Nonnull
        public ShopBuilder ossRegistered(@Nullable Boolean ossRegistered) {
            this.ossRegistered = ossRegistered;
            return this;
        }

        @Nonnull
        public ShopBuilder ossThresholdAlertSent(@Nullable Boolean ossThresholdAlertSent) {
            this.ossThresholdAlertSent = ossThresholdAlertSent;
            return this;
        }

        @Nonnull
        public ShopBuilder shopifyChargeId(@Nullable String shopifyChargeId) {
            this.shopifyChargeId = shopifyChargeId;
            return this;
        }

        @Nonnull
        public ShopBuilder shopifyDomain(@Nonnull String shopifyDomain) {
            this.shopifyDomain = shopifyDomain;
            return this;
        }

        @Nonnull
        public ShopBuilder shopifyShopId(@Nullable String shopifyShopId) {
            this.shopifyShopId = shopifyShopId;
            return this;
        }

        @Nonnull
        public ShopBuilder shopName(@Nullable String shopName) {
            this.shopName = shopName;
            return this;
        }

        @Nonnull
        public ShopBuilder subscriptionEndsAt(@Nullable LocalDateTime subscriptionEndsAt) {
            this.subscriptionEndsAt = subscriptionEndsAt;
            return this;
        }

        @Nonnull
        public ShopBuilder subscriptionPlan(@Nullable String subscriptionPlan) {
            this.subscriptionPlan = subscriptionPlan;
            return this;
        }

        @Nonnull
        public ShopBuilder subscriptionStatus(@Nullable SubscriptionStatus subscriptionStatus) {
            this.subscriptionStatus = subscriptionStatus;
            return this;
        }

        @Nonnull
        public ShopBuilder trialEndsAt(@Nullable LocalDateTime trialEndsAt) {
            this.trialEndsAt = trialEndsAt;
            return this;
        }

        @Nonnull
        public ShopBuilder uninstalledAt(@Nullable LocalDateTime uninstalledAt) {
            this.uninstalledAt = uninstalledAt;
            return this;
        }

        @Nonnull
        public ShopBuilder updatedAt(@Nullable LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        @Nonnull
        public ShopBuilder vatNumber(@Nullable String vatNumber) {
            this.vatNumber = vatNumber;
            return this;
        }
    }
}
