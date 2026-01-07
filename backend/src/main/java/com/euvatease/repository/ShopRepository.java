package com.euvatease.repository;

import com.euvatease.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    Optional<Shop> findByShopifyDomain(String shopifyDomain);

    Optional<Shop> findByShopifyShopId(String shopifyShopId);

    boolean existsByShopifyDomain(String shopifyDomain);

    List<Shop> findByIsActiveTrue();

    List<Shop> findBySubscriptionStatus(Shop.SubscriptionStatus status);

    @Query("SELECT s FROM Shop s WHERE s.isActive = true AND s.subscriptionStatus IN ('TRIAL', 'ACTIVE')")
    List<Shop> findActiveSubscriptions();

    @Query("SELECT s FROM Shop s WHERE s.isActive = true AND s.alertEmailEnabled = true")
    List<Shop> findShopsWithAlertsEnabled();

    @Query("SELECT s FROM Shop s WHERE s.subscriptionStatus = 'TRIAL' AND s.trialEndsAt <= CURRENT_TIMESTAMP")
    List<Shop> findExpiredTrials();

    @Query("SELECT s FROM Shop s WHERE s.subscriptionStatus = 'ACTIVE' AND s.subscriptionEndsAt <= CURRENT_TIMESTAMP")
    List<Shop> findExpiredSubscriptions();

    @Query("SELECT s FROM Shop s WHERE s.isActive = true AND s.ossThresholdAlertSent = false")
    List<Shop> findShopsNeedingOssThresholdCheck();

    @Query("SELECT COUNT(s) FROM Shop s WHERE s.isActive = true")
    long countActiveShops();

    @Query("SELECT s FROM Shop s WHERE s.countryCode = :countryCode AND s.isActive = true")
    List<Shop> findByCountryCode(@Param("countryCode") String countryCode);
}
