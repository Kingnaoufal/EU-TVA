package com.euvatease.repository;

import com.euvatease.entity.Shop;
import com.euvatease.entity.VatValidation;
import jakarta.annotation.Nonnull;
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

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Query("SELECT COUNT(v) FROM VatValidation v WHERE v.shop = :shop AND v.validationStatus = 'INVALID'")
    long countInvalidVatNumbers(@Nonnull @Param("shop") Shop shop);

    @Query("SELECT COUNT(v) FROM VatValidation v WHERE v.shop = :shop AND v.validationStatus = 'VALID'")
    long countValidVatNumbers(@Nonnull @Param("shop") Shop shop);

    boolean existsByVatNumberAndValidationStatusAndValidationDateAfter(@Nonnull String vatNumber,
                                                                       @Nonnull VatValidation.ValidationStatus status,
                                                                       @Nonnull LocalDateTime date);

    @Query("SELECT v FROM VatValidation v WHERE v.orderId = :orderId ORDER BY v.validationDate DESC")
    @Nonnull
    List<VatValidation> findByOrderId(@Nonnull @Param("orderId") Long orderId);

    @Nonnull
    Page<VatValidation> findByShop(@Nonnull Shop shop,
                                   @Nonnull Pageable pageable);

    @Query("SELECT v FROM VatValidation v WHERE v.shop = :shop AND v.validationDate BETWEEN :start AND :end ORDER BY v.validationDate DESC")
    @Nonnull
    List<VatValidation> findByShopAndPeriod(@Nonnull @Param("shop") Shop shop,
                                            @Nonnull @Param("start") LocalDateTime start,
                                            @Nonnull @Param("end") LocalDateTime end);

    @Nonnull
    List<VatValidation> findByShopAndValidationStatus(@Nonnull Shop shop,
                                                      @Nonnull VatValidation.ValidationStatus status);

    @Nonnull
    Optional<VatValidation> findByShopifyOrderId(@Nonnull String shopifyOrderId);

    @Nonnull
    Optional<VatValidation> findByVatNumberAndCountryCode(@Nonnull String vatNumber,
                                                          @Nonnull String countryCode);

    @Query("SELECT v FROM VatValidation v WHERE v.vatNumber = :vatNumber ORDER BY v.validationDate DESC")
    @Nonnull
    List<VatValidation> findLatestByVatNumber(@Nonnull @Param("vatNumber") String vatNumber,
                                              @Nonnull Pageable pageable);

    @Query("SELECT v FROM VatValidation v WHERE v.validationStatus = 'PENDING' AND v.nextRetryAt <= :now")
    @Nonnull
    List<VatValidation> findPendingRetries(@Nonnull @Param("now") LocalDateTime now);

    @Query("SELECT v FROM VatValidation v WHERE v.shop = :shop ORDER BY v.validationDate DESC")
    @Nonnull
    Page<VatValidation> findRecentValidations(@Nonnull @Param("shop") Shop shop,
                                              @Nonnull Pageable pageable);

    @Query("SELECT v FROM VatValidation v WHERE v.validationStatus = 'UNAVAILABLE' AND v.retryCount < :maxRetries AND v.nextRetryAt <= :now")
    @Nonnull
    List<VatValidation> findRetryableValidations(@Param("maxRetries") int maxRetries,
                                                 @Nonnull @Param("now") LocalDateTime now);
}
