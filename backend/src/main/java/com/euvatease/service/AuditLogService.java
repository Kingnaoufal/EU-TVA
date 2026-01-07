package com.euvatease.service;

import com.euvatease.entity.AuditLog;
import com.euvatease.entity.Shop;
import com.euvatease.repository.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private static final Logger log = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Async
    @Transactional
    public void log(Shop shop, AuditLog.ActionType actionType, String entityType, Long entityId, String description) {
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
    public void logWithValues(Shop shop, AuditLog.ActionType actionType, String entityType, Long entityId, 
                              String description, String oldValue, String newValue) {
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

    @Async
    @Transactional
    public void logWithRequest(Shop shop, AuditLog.ActionType actionType, String description, 
                                String ipAddress, String userAgent) {
        AuditLog auditLog = AuditLog.builder()
            .shop(shop)
            .actionType(actionType)
            .description(description)
            .ipAddress(ipAddress)
            .userAgent(userAgent)
            .build();

        auditLogRepository.save(auditLog);
    }
}
