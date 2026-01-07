package com.euvatease.repository;

import com.euvatease.entity.Shop;
import com.euvatease.entity.VatAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VatAlertRepository extends JpaRepository<VatAlert, Long> {

    Page<VatAlert> findByShopOrderByCreatedAtDesc(Shop shop, Pageable pageable);

    List<VatAlert> findByShopAndIsReadFalseOrderByCreatedAtDesc(Shop shop);

    List<VatAlert> findByShopAndIsResolvedFalseOrderByCreatedAtDesc(Shop shop);

    @Query("SELECT a FROM VatAlert a WHERE a.shop = :shop AND a.severity IN ('ERROR', 'CRITICAL') AND a.isResolved = false")
    List<VatAlert> findCriticalUnresolvedAlerts(@Param("shop") Shop shop);

    @Query("SELECT COUNT(a) FROM VatAlert a WHERE a.shop = :shop AND a.isRead = false")
    long countUnreadAlerts(@Param("shop") Shop shop);

    @Query("SELECT COUNT(a) FROM VatAlert a WHERE a.shop = :shop AND a.isResolved = false")
    long countUnresolvedAlerts(@Param("shop") Shop shop);

    @Query("SELECT a FROM VatAlert a WHERE a.shop = :shop AND a.alertType = :type AND a.isResolved = false")
    List<VatAlert> findUnresolvedByType(@Param("shop") Shop shop, @Param("type") VatAlert.AlertType type);

    @Modifying
    @Query("UPDATE VatAlert a SET a.isRead = true WHERE a.shop = :shop AND a.isRead = false")
    int markAllAsRead(@Param("shop") Shop shop);

    @Query("SELECT a FROM VatAlert a JOIN FETCH a.shop s WHERE a.emailSent = false AND s.alertEmailEnabled = true")
    List<VatAlert> findAlertsToSend();

    boolean existsByShopAndAlertTypeAndIsResolvedFalse(Shop shop, VatAlert.AlertType alertType);
}
