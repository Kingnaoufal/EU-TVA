package com.euvatease.repository;

import com.euvatease.entity.EuVatRate;
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

    @Query("SELECT e FROM EuVatRate e WHERE e.countryCode = :countryCode AND e.effectiveFrom <= :date AND (e.effectiveTo IS NULL OR e.effectiveTo >= :date)")
    Optional<EuVatRate> findByCountryCodeAndDate(@Param("countryCode") String countryCode, @Param("date") LocalDate date);

    @Query("SELECT e FROM EuVatRate e WHERE e.isEuMember = true AND e.effectiveFrom <= :date AND (e.effectiveTo IS NULL OR e.effectiveTo >= :date) ORDER BY e.countryName")
    List<EuVatRate> findAllEuCountries(@Param("date") LocalDate date);

    @Query("SELECT e.countryCode FROM EuVatRate e WHERE e.isEuMember = true AND e.effectiveFrom <= :date AND (e.effectiveTo IS NULL OR e.effectiveTo >= :date)")
    List<String> findAllEuCountryCodes(@Param("date") LocalDate date);

    @Query("SELECT e.standardRate FROM EuVatRate e WHERE e.countryCode = :countryCode AND e.effectiveFrom <= :date AND (e.effectiveTo IS NULL OR e.effectiveTo >= :date)")
    Optional<BigDecimal> findStandardRateByCountryCode(@Param("countryCode") String countryCode, @Param("date") LocalDate date);

    List<EuVatRate> findByIsEuMemberTrueOrderByCountryNameAsc();

    boolean existsByCountryCode(String countryCode);
}
