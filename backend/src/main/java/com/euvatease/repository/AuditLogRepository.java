package com.euvatease.repository;

import com.euvatease.entity.AuditLog;
import com.euvatease.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    Page<AuditLog> findByShopOrderByCreatedAtDesc(Shop shop, Pageable pageable);

    List<AuditLog> findByShopAndActionTypeOrderByCreatedAtDesc(Shop shop, AuditLog.ActionType actionType);

    @Query("SELECT a FROM AuditLog a WHERE a.shop = :shop AND a.createdAt BETWEEN :start AND :end ORDER BY a.createdAt DESC")
    List<AuditLog> findByShopAndPeriod(@Param("shop") Shop shop, 
                                        @Param("start") LocalDateTime start, 
                                        @Param("end") LocalDateTime end);

    @Query("SELECT a FROM AuditLog a WHERE a.shop = :shop AND a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.createdAt DESC")
    List<AuditLog> findByEntity(@Param("shop") Shop shop, 
                                 @Param("entityType") String entityType, 
                                 @Param("entityId") Long entityId);

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.shop = :shop AND a.createdAt >= :since")
    long countRecentActivity(@Param("shop") Shop shop, @Param("since") LocalDateTime since);
}
