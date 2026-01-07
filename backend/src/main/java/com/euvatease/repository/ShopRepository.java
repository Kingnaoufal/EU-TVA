package com.euvatease.repository;

import com.euvatease.entity.Shop;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopRepository extends JpaRepository<Shop, Long> {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Query("SELECT COUNT(s) FROM Shop s WHERE s.isActive = true")
    long countActiveShops();

    boolean existsByShopifyDomain(@Nonnull String shopifyDomain);

    @Query("SELECT s FROM Shop s WHERE s.isActive = true AND s.subscriptionStatus IN ('TRIAL', 'ACTIVE')")
    @Nonnull
    List<Shop> findActiveSubscriptions();

    @Query("SELECT s FROM Shop s WHERE s.countryCode = :countryCode AND s.isActive = true")
    @Nonnull
    List<Shop> findByCountryCode(@Nonnull @Param("countryCode") String countryCode);

    @Nonnull
    List<Shop> findByIsActiveTrue();

    @Nonnull
    Optional<Shop> findByShopifyDomain(@Nonnull String shopifyDomain);

    @Nonnull
    Optional<Shop> findByShopifyShopId(@Nonnull String shopifyShopId);

    @Nonnull
    List<Shop> findBySubscriptionStatus(@Nonnull Shop.SubscriptionStatus status);

    @Query("SELECT s FROM Shop s WHERE s.subscriptionStatus = 'ACTIVE' AND s.subscriptionEndsAt <= CURRENT_TIMESTAMP")
    @Nonnull
    List<Shop> findExpiredSubscriptions();

    @Query("SELECT s FROM Shop s WHERE s.subscriptionStatus = 'TRIAL' AND s.trialEndsAt <= CURRENT_TIMESTAMP")
    @Nonnull
    List<Shop> findExpiredTrials();

    @Query("SELECT s FROM Shop s WHERE s.isActive = true AND s.ossThresholdAlertSent = false")
    @Nonnull
    List<Shop> findShopsNeedingOssThresholdCheck();

    @Query("SELECT s FROM Shop s WHERE s.isActive = true AND s.alertEmailEnabled = true")
    @Nonnull
    List<Shop> findShopsWithAlertsEnabled();
}
