package com.euvatease.repository;

import com.euvatease.entity.AuditLog;
import com.euvatease.entity.Shop;
import jakarta.annotation.Nonnull;
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

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.shop = :shop AND a.createdAt >= :since")
    long countRecentActivity(@Nonnull @Param("shop") Shop shop,
                             @Nonnull @Param("since") LocalDateTime since);

    @Query("SELECT a FROM AuditLog a WHERE a.shop = :shop AND a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.createdAt DESC")
    @Nonnull
    List<AuditLog> findByEntity(@Nonnull @Param("shop") Shop shop,
                                @Nonnull @Param("entityType") String entityType,
                                @Nonnull @Param("entityId") Long entityId);

    @Nonnull
    List<AuditLog> findByShopAndActionTypeOrderByCreatedAtDesc(@Nonnull Shop shop,
                                                               @Nonnull AuditLog.ActionType actionType);

    @Query("SELECT a FROM AuditLog a WHERE a.shop = :shop AND a.createdAt BETWEEN :start AND :end ORDER BY a.createdAt DESC")
    @Nonnull
    List<AuditLog> findByShopAndPeriod(@Nonnull @Param("shop") Shop shop,
                                       @Nonnull @Param("start") LocalDateTime start,
                                       @Nonnull @Param("end") LocalDateTime end);

    @Nonnull
    Page<AuditLog> findByShopOrderByCreatedAtDesc(@Nonnull Shop shop,
                                                  @Nonnull Pageable pageable);
}
