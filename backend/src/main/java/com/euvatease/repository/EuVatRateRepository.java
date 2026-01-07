package com.euvatease.repository;

import com.euvatease.entity.EuVatRate;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface EuVatRateRepository extends JpaRepository<EuVatRate, Long> {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    boolean existsByCountryCode(@Nonnull String countryCode);

    @Query("SELECT e FROM EuVatRate e WHERE e.isEuMember = true AND e.effectiveFrom <= :date AND (e.effectiveTo IS NULL OR e.effectiveTo >= :date) ORDER BY e.countryName")
    @Nonnull
    List<EuVatRate> findAllEuCountries(@Nonnull @Param("date") LocalDate date);

    @Query("SELECT e.countryCode FROM EuVatRate e WHERE e.isEuMember = true AND e.effectiveFrom <= :date AND (e.effectiveTo IS NULL OR e.effectiveTo >= :date)")
    @Nonnull
    List<String> findAllEuCountryCodes(@Nonnull @Param("date") LocalDate date);

    @Query("SELECT e FROM EuVatRate e WHERE e.countryCode = :countryCode AND e.effectiveFrom <= :date AND (e.effectiveTo IS NULL OR e.effectiveTo >= :date)")
    @Nonnull
    Optional<EuVatRate> findByCountryCodeAndDate(@Nonnull @Param("countryCode") String countryCode,
                                                 @Nonnull @Param("date") LocalDate date);

    @Nonnull
    List<EuVatRate> findByIsEuMemberTrueOrderByCountryNameAsc();

    @Query("SELECT e.standardRate FROM EuVatRate e WHERE e.countryCode = :countryCode AND e.effectiveFrom <= :date AND (e.effectiveTo IS NULL OR e.effectiveTo >= :date)")
    @Nonnull
    Optional<BigDecimal> findStandardRateByCountryCode(@Nonnull @Param("countryCode") String countryCode,
                                                       @Nonnull @Param("date") LocalDate date);
}
