package com.euvatease.repository;

import com.euvatease.entity.Shop;
import com.euvatease.entity.VatValidation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VatValidationRepository extends JpaRepository<VatValidation, Long> {

    Optional<VatValidation> findByVatNumberAndCountryCode(String vatNumber, String countryCode);

    Optional<VatValidation> findByShopifyOrderId(String shopifyOrderId);

    Page<VatValidation> findByShop(Shop shop, Pageable pageable);

    List<VatValidation> findByShopAndValidationStatus(Shop shop, VatValidation.ValidationStatus status);

    @Query("SELECT v FROM VatValidation v WHERE v.vatNumber = :vatNumber ORDER BY v.validationDate DESC")
    List<VatValidation> findLatestByVatNumber(@Param("vatNumber") String vatNumber, Pageable pageable);

    @Query("SELECT v FROM VatValidation v WHERE v.validationStatus = 'PENDING' AND v.nextRetryAt <= :now")
    List<VatValidation> findPendingRetries(@Param("now") LocalDateTime now);

    @Query("SELECT v FROM VatValidation v WHERE v.validationStatus = 'UNAVAILABLE' AND v.retryCount < :maxRetries AND v.nextRetryAt <= :now")
    List<VatValidation> findRetryableValidations(@Param("maxRetries") int maxRetries, @Param("now") LocalDateTime now);

    @Query("SELECT COUNT(v) FROM VatValidation v WHERE v.shop = :shop AND v.validationStatus = 'VALID'")
    long countValidVatNumbers(@Param("shop") Shop shop);

    @Query("SELECT COUNT(v) FROM VatValidation v WHERE v.shop = :shop AND v.validationStatus = 'INVALID'")
    long countInvalidVatNumbers(@Param("shop") Shop shop);

    @Query("SELECT v FROM VatValidation v WHERE v.shop = :shop AND v.validationDate BETWEEN :start AND :end ORDER BY v.validationDate DESC")
    List<VatValidation> findByShopAndPeriod(@Param("shop") Shop shop, 
                                             @Param("start") LocalDateTime start, 
                                             @Param("end") LocalDateTime end);

    @Query("SELECT v FROM VatValidation v WHERE v.orderId = :orderId ORDER BY v.validationDate DESC")
    List<VatValidation> findByOrderId(@Param("orderId") Long orderId);

    boolean existsByVatNumberAndValidationStatusAndValidationDateAfter(
        String vatNumber, 
        VatValidation.ValidationStatus status, 
        LocalDateTime date
    );

    @Query("SELECT v FROM VatValidation v WHERE v.shop = :shop ORDER BY v.validationDate DESC")
    Page<VatValidation> findRecentValidations(@Param("shop") Shop shop, Pageable pageable);
}
