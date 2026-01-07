package com.euvatease.repository;

import com.euvatease.entity.Order;
import com.euvatease.entity.Shop;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shop = :shop AND o.isB2b = true AND o.orderDate BETWEEN :start AND :end")
    long countB2bOrders(@Nonnull @Param("shop") Shop shop,
                        @Nonnull @Param("start") LocalDateTime start,
                        @Nonnull @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shop = :shop AND o.vatExempt = true AND o.orderDate BETWEEN :start AND :end")
    long countExemptOrders(@Nonnull @Param("shop") Shop shop,
                           @Nonnull @Param("start") LocalDateTime start,
                           @Nonnull @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shop = :shop AND o.orderDate BETWEEN :start AND :end")
    long countOrdersInPeriod(@Nonnull @Param("shop") Shop shop,
                             @Nonnull @Param("start") LocalDateTime start,
                             @Nonnull @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shop = :shop AND o.hasVatError = true")
    long countVatErrors(@Nonnull @Param("shop") Shop shop);

    boolean existsByShopAndShopifyOrderId(@Nonnull Shop shop,
                                          @Nonnull String shopifyOrderId);

    @Nonnull
    Page<Order> findByShop(@Nonnull Shop shop,
                           @Nonnull Pageable pageable);

    @Nonnull
    Page<Order> findByShopAndHasVatErrorTrue(@Nonnull Shop shop,
                                             @Nonnull Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.shop = :shop AND o.customerCountryCode = :countryCode AND o.orderDate BETWEEN :start AND :end")
    @Nonnull
    List<Order> findByShopAndCountryAndPeriod(@Nonnull @Param("shop") Shop shop,
                                              @Nonnull @Param("countryCode") String countryCode,
                                              @Nonnull @Param("start") LocalDateTime start,
                                              @Nonnull @Param("end") LocalDateTime end);

    @Nonnull
    List<Order> findByShopAndOrderDateBetween(@Nonnull Shop shop,
                                              @Nonnull LocalDateTime start,
                                              @Nonnull LocalDateTime end);

    @Nonnull
    Optional<Order> findByShopAndShopifyOrderId(@Nonnull Shop shop,
                                                @Nonnull String shopifyOrderId);

    @Query("SELECT DISTINCT o.customerCountryCode FROM Order o WHERE o.shop = :shop AND o.orderDate BETWEEN :start AND :end")
    @Nonnull
    List<String> findDistinctCountries(@Nonnull @Param("shop") Shop shop,
                                       @Nonnull @Param("start") LocalDateTime start,
                                       @Nonnull @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.shop = :shop AND o.orderDate BETWEEN :start AND :end AND o.includedInOssReport = false")
    @Nonnull
    List<Order> findOrdersForOssReport(@Nonnull @Param("shop") Shop shop,
                                       @Nonnull @Param("start") LocalDateTime start,
                                       @Nonnull @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.shop = :shop AND o.hasVatError = true ORDER BY o.orderDate DESC")
    @Nonnull
    List<Order> findRecentVatErrors(@Nonnull @Param("shop") Shop shop,
                                    @Nonnull Pageable pageable);

    @Query("SELECT o.customerCountryCode, COUNT(o), SUM(o.totalAmount), SUM(o.taxAmount) " +
           "FROM Order o WHERE o.shop = :shop AND o.orderDate BETWEEN :start AND :end " +
           "GROUP BY o.customerCountryCode ORDER BY SUM(o.totalAmount) DESC")
    @Nonnull
    List<Object[]> getSalesByCountry(@Nonnull @Param("shop") Shop shop,
                                     @Nonnull @Param("start") LocalDateTime start,
                                     @Nonnull @Param("end") LocalDateTime end);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.shop = :shop AND o.customerCountryCode != :homeCountry AND o.orderDate >= :yearStart")
    @Nullable
    BigDecimal sumEuSalesForOssThreshold(@Nonnull @Param("shop") Shop shop,
                                         @Nonnull @Param("homeCountry") String homeCountry,
                                         @Nonnull @Param("yearStart") LocalDateTime yearStart);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.shop = :shop AND o.orderDate BETWEEN :start AND :end AND o.isRefunded = false")
    @Nullable
    BigDecimal sumTotalSales(@Nonnull @Param("shop") Shop shop,
                             @Nonnull @Param("start") LocalDateTime start,
                             @Nonnull @Param("end") LocalDateTime end);
}
