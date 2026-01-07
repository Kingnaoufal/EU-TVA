package com.euvatease.repository;

import com.euvatease.entity.Order;
import com.euvatease.entity.Shop;
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

    Optional<Order> findByShopAndShopifyOrderId(Shop shop, String shopifyOrderId);

    Page<Order> findByShop(Shop shop, Pageable pageable);

    Page<Order> findByShopAndHasVatErrorTrue(Shop shop, Pageable pageable);

    List<Order> findByShopAndOrderDateBetween(Shop shop, LocalDateTime start, LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.shop = :shop AND o.orderDate BETWEEN :start AND :end AND o.includedInOssReport = false")
    List<Order> findOrdersForOssReport(@Param("shop") Shop shop, 
                                        @Param("start") LocalDateTime start, 
                                        @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.shop = :shop AND o.customerCountryCode = :countryCode AND o.orderDate BETWEEN :start AND :end")
    List<Order> findByShopAndCountryAndPeriod(@Param("shop") Shop shop,
                                               @Param("countryCode") String countryCode,
                                               @Param("start") LocalDateTime start,
                                               @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shop = :shop AND o.hasVatError = true")
    long countVatErrors(@Param("shop") Shop shop);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.shop = :shop AND o.orderDate BETWEEN :start AND :end AND o.isRefunded = false")
    BigDecimal sumTotalSales(@Param("shop") Shop shop, 
                             @Param("start") LocalDateTime start, 
                             @Param("end") LocalDateTime end);

    @Query("SELECT SUM(o.totalAmount) FROM Order o WHERE o.shop = :shop AND o.customerCountryCode != :homeCountry AND o.orderDate >= :yearStart")
    BigDecimal sumEuSalesForOssThreshold(@Param("shop") Shop shop, 
                                          @Param("homeCountry") String homeCountry,
                                          @Param("yearStart") LocalDateTime yearStart);

    @Query("SELECT DISTINCT o.customerCountryCode FROM Order o WHERE o.shop = :shop AND o.orderDate BETWEEN :start AND :end")
    List<String> findDistinctCountries(@Param("shop") Shop shop, 
                                        @Param("start") LocalDateTime start, 
                                        @Param("end") LocalDateTime end);

    @Query("SELECT o.customerCountryCode, COUNT(o), SUM(o.totalAmount), SUM(o.taxAmount) " +
           "FROM Order o WHERE o.shop = :shop AND o.orderDate BETWEEN :start AND :end " +
           "GROUP BY o.customerCountryCode ORDER BY SUM(o.totalAmount) DESC")
    List<Object[]> getSalesByCountry(@Param("shop") Shop shop, 
                                      @Param("start") LocalDateTime start, 
                                      @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shop = :shop AND o.isB2b = true AND o.orderDate BETWEEN :start AND :end")
    long countB2bOrders(@Param("shop") Shop shop, 
                        @Param("start") LocalDateTime start, 
                        @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shop = :shop AND o.vatExempt = true AND o.orderDate BETWEEN :start AND :end")
    long countExemptOrders(@Param("shop") Shop shop, 
                           @Param("start") LocalDateTime start, 
                           @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o WHERE o.shop = :shop AND o.hasVatError = true ORDER BY o.orderDate DESC")
    List<Order> findRecentVatErrors(@Param("shop") Shop shop, Pageable pageable);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.shop = :shop AND o.orderDate BETWEEN :start AND :end")
    long countOrdersInPeriod(@Param("shop") Shop shop, 
                             @Param("start") LocalDateTime start, 
                             @Param("end") LocalDateTime end);

    boolean existsByShopAndShopifyOrderId(Shop shop, String shopifyOrderId);
}
