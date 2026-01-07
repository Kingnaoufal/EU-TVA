package com.euvatease.service;

import com.euvatease.entity.AuditLog;
import com.euvatease.entity.Shop;
import com.euvatease.repository.AuditLogRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AuditLogService {

    //~ ------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    //~ ------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ------------------------------------------------------------------------------------------------

    @Nonnull
    private final AuditLogRepository auditLogRepository;

    //~ ------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ------------------------------------------------------------------------------------------------

    public AuditLogService(@Nonnull AuditLogRepository auditLogRepository) {
        this.auditLogRepository = Objects.requireNonNull(auditLogRepository, "auditLogRepository must not be null");
    }

    //~ ------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ------------------------------------------------------------------------------------------------

    @Async
    @Transactional
    public void log(@Nonnull Shop shop,
                    @Nonnull AuditLog.ActionType actionType,
                    @Nonnull String entityType,
                    @Nullable Long entityId,
                    @Nullable String description) {
        AuditLog auditLog = AuditLog.builder()
            .shop(shop)
            .actionType(actionType)
            .entityType(entityType)
            .entityId(entityId)
            .description(description)
            .build();

        auditLogRepository.save(auditLog);
        log.debug("Audit log: shop={}, action={}, entity={}:{}",
            shop.getShopifyDomain(), actionType, entityType, entityId);
    }

    @Async
    @Transactional
    public void logWithRequest(@Nonnull Shop shop,
                               @Nonnull AuditLog.ActionType actionType,
                               @Nullable String description,
                               @Nullable String ipAddress,
                               @Nullable String userAgent) {
        AuditLog auditLog = AuditLog.builder()
            .shop(shop)
            .actionType(actionType)
            .description(description)
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .build();

        auditLogRepository.save(auditLog);
    }

    @Async
    @Transactional
    public void logWithValues(@Nonnull Shop shop,
                              @Nonnull AuditLog.ActionType actionType,
                              @Nonnull String entityType,
                              @Nullable Long entityId,
                              @Nullable String description,
                              @Nullable String oldValue,
                              @Nullable String newValue) {
        AuditLog auditLog = AuditLog.builder()
            .shop(shop)
            .actionType(actionType)
            .entityType(entityType)
            .entityId(entityId)
            .description(description)
            .oldValue(oldValue)
            .newValue(newValue)
            .build();

        auditLogRepository.save(auditLog);
    }
}
