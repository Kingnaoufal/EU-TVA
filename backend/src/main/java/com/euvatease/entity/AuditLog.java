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
@Table(name = "audit_logs")
public class AuditLog {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Enums
    //~ ----------------------------------------------------------------------------------------------------------------

    public enum ActionType {
        SHOP_INSTALLED,
        SHOP_UNINSTALLED,
        ORDERS_SYNCED,
        ORDER_CREATED,
        ORDER_UPDATED,
        ORDER_REFUNDED,
        VAT_VALIDATED,
        REPORT_GENERATED,
        REPORT_DOWNLOADED,
        REPORT_SUBMITTED,
        ALERT_CREATED,
        ALERT_RESOLVED,
        SETTINGS_UPDATED,
        SUBSCRIPTION_STARTED,
        SUBSCRIPTION_CANCELLED,
        LOGIN,
        LOGOUT
    }

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
    @Column(name = "action_type", nullable = false)
    @Nonnull
    private ActionType actionType;

    @Column(name = "entity_type")
    @Nullable
    private String entityType;

    @Column(name = "entity_id")
    @Nullable
    private Long entityId;

    @Column(name = "description", columnDefinition = "TEXT")
    @Nullable
    private String description;

    @Column(name = "old_value", columnDefinition = "TEXT")
    @Nullable
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    @Nullable
    private String newValue;

    @Column(name = "ip_address")
    @Nullable
    private String ipAddress;

    @Column(name = "user_agent")
    @Nullable
    private String userAgent;

    @Column(name = "created_at")
    @Nullable
    private LocalDateTime createdAt;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public AuditLog() {
    }

    public AuditLog(@Nullable Long id,
                    @Nonnull Shop shop,
                    @Nonnull ActionType actionType,
                    @Nullable String entityType,
                    @Nullable Long entityId,
                    @Nullable String description,
                    @Nullable String oldValue,
                    @Nullable String newValue,
                    @Nullable String ipAddress,
                    @Nullable String userAgent,
                    @Nullable LocalDateTime createdAt) {
        this.id = id;
        this.shop = shop;
        this.actionType = actionType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.description = description;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.createdAt = createdAt;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    public static AuditLogBuilder builder() {
        return new AuditLogBuilder();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
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
    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(@Nonnull ActionType actionType) {
        this.actionType = actionType;
    }

    @Nullable
    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(@Nullable String entityType) {
        this.entityType = entityType;
    }

    @Nullable
    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(@Nullable Long entityId) {
        this.entityId = entityId;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    public void setDescription(@Nullable String description) {
        this.description = description;
    }

    @Nullable
    public String getOldValue() {
        return oldValue;
    }

    public void setOldValue(@Nullable String oldValue) {
        this.oldValue = oldValue;
    }

    @Nullable
    public String getNewValue() {
        return newValue;
    }

    public void setNewValue(@Nullable String newValue) {
        this.newValue = newValue;
    }

    @Nullable
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(@Nullable String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Nullable
    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(@Nullable String userAgent) {
        this.userAgent = userAgent;
    }

    @Nullable
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Inner Classes
    //~ ----------------------------------------------------------------------------------------------------------------

    public static class AuditLogBuilder {

        @Nullable
        private Long id;

        @Nullable
        private Shop shop;

        @Nullable
        private ActionType actionType;

        @Nullable
        private String entityType;

        @Nullable
        private Long entityId;

        @Nullable
        private String description;

        @Nullable
        private String oldValue;

        @Nullable
        private String newValue;

        @Nullable
        private String ipAddress;

        @Nullable
        private String userAgent;

        @Nullable
        private LocalDateTime createdAt;

        @Nonnull
        public AuditLogBuilder id(@Nullable Long id) {
            this.id = id;
            return this;
        }

        @Nonnull
        public AuditLogBuilder shop(@Nonnull Shop shop) {
            this.shop = shop;
            return this;
        }

        @Nonnull
        public AuditLogBuilder actionType(@Nonnull ActionType actionType) {
            this.actionType = actionType;
            return this;
        }

        @Nonnull
        public AuditLogBuilder entityType(@Nullable String entityType) {
            this.entityType = entityType;
            return this;
        }

        @Nonnull
        public AuditLogBuilder entityId(@Nullable Long entityId) {
            this.entityId = entityId;
            return this;
        }

        @Nonnull
        public AuditLogBuilder description(@Nullable String description) {
            this.description = description;
            return this;
        }

        @Nonnull
        public AuditLogBuilder oldValue(@Nullable String oldValue) {
            this.oldValue = oldValue;
            return this;
        }

        @Nonnull
        public AuditLogBuilder newValue(@Nullable String newValue) {
            this.newValue = newValue;
            return this;
        }

        @Nonnull
        public AuditLogBuilder ipAddress(@Nullable String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }

        @Nonnull
        public AuditLogBuilder userAgent(@Nullable String userAgent) {
            this.userAgent = userAgent;
            return this;
        }

        @Nonnull
        public AuditLogBuilder createdAt(@Nullable LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Nonnull
        public AuditLog build() {
            return new AuditLog(id, shop, actionType, entityType, entityId, description, oldValue,
                    newValue, ipAddress, userAgent, createdAt);
        }
    }
}
