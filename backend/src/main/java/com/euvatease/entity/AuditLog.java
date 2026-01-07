package com.euvatease.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Column(name = "entity_type")
    private String entityType;

    @Column(name = "entity_id")
    private Long entityId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public AuditLog() {
    }

    public AuditLog(Long id, Shop shop, ActionType actionType, String entityType, Long entityId,
                    String description, String oldValue, String newValue, String ipAddress,
                    String userAgent, LocalDateTime createdAt) {
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public Shop getShop() { return shop; }
    public ActionType getActionType() { return actionType; }
    public String getEntityType() { return entityType; }
    public Long getEntityId() { return entityId; }
    public String getDescription() { return description; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public String getIpAddress() { return ipAddress; }
    public String getUserAgent() { return userAgent; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setShop(Shop shop) { this.shop = shop; }
    public void setActionType(ActionType actionType) { this.actionType = actionType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }
    public void setDescription(String description) { this.description = description; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static AuditLogBuilder builder() { return new AuditLogBuilder(); }

    public static class AuditLogBuilder {
        private Long id;
        private Shop shop;
        private ActionType actionType;
        private String entityType;
        private Long entityId;
        private String description;
        private String oldValue;
        private String newValue;
        private String ipAddress;
        private String userAgent;
        private LocalDateTime createdAt;

        public AuditLogBuilder id(Long id) { this.id = id; return this; }
        public AuditLogBuilder shop(Shop shop) { this.shop = shop; return this; }
        public AuditLogBuilder actionType(ActionType actionType) { this.actionType = actionType; return this; }
        public AuditLogBuilder entityType(String entityType) { this.entityType = entityType; return this; }
        public AuditLogBuilder entityId(Long entityId) { this.entityId = entityId; return this; }
        public AuditLogBuilder description(String description) { this.description = description; return this; }
        public AuditLogBuilder oldValue(String oldValue) { this.oldValue = oldValue; return this; }
        public AuditLogBuilder newValue(String newValue) { this.newValue = newValue; return this; }
        public AuditLogBuilder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public AuditLogBuilder userAgent(String userAgent) { this.userAgent = userAgent; return this; }
        public AuditLogBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public AuditLog build() {
            return new AuditLog(id, shop, actionType, entityType, entityId, description, oldValue,
                    newValue, ipAddress, userAgent, createdAt);
        }
    }

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
}
