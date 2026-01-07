package com.euvatease.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "eu_vat_rates")
public class EuVatRate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "country_name", nullable = false)
    private String countryName;

    @Column(name = "standard_rate", nullable = false, precision = 5, scale = 2)
    private BigDecimal standardRate;

    @Column(name = "reduced_rate", precision = 5, scale = 2)
    private BigDecimal reducedRate;

    @Column(name = "reduced_rate_2", precision = 5, scale = 2)
    private BigDecimal reducedRate2;

    @Column(name = "super_reduced_rate", precision = 5, scale = 2)
    private BigDecimal superReducedRate;

    @Column(name = "parking_rate", precision = 5, scale = 2)
    private BigDecimal parkingRate;

    @Column(name = "is_eu_member")
    private Boolean isEuMember;

    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    public EuVatRate() {}

    public EuVatRate(Long id, String countryCode, String countryName, BigDecimal standardRate,
                     BigDecimal reducedRate, BigDecimal reducedRate2, BigDecimal superReducedRate,
                     BigDecimal parkingRate, Boolean isEuMember, LocalDate effectiveFrom, LocalDate effectiveTo) {
        this.id = id;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.standardRate = standardRate;
        this.reducedRate = reducedRate;
        this.reducedRate2 = reducedRate2;
        this.superReducedRate = superReducedRate;
        this.parkingRate = parkingRate;
        this.isEuMember = isEuMember;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
    }

    // Getters
    public Long getId() { return id; }
    public String getCountryCode() { return countryCode; }
    public String getCountryName() { return countryName; }
    public BigDecimal getStandardRate() { return standardRate; }
    public BigDecimal getReducedRate() { return reducedRate; }
    public BigDecimal getReducedRate2() { return reducedRate2; }
    public BigDecimal getSuperReducedRate() { return superReducedRate; }
    public BigDecimal getParkingRate() { return parkingRate; }
    public Boolean getIsEuMember() { return isEuMember; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public LocalDate getEffectiveTo() { return effectiveTo; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    public void setStandardRate(BigDecimal standardRate) { this.standardRate = standardRate; }
    public void setReducedRate(BigDecimal reducedRate) { this.reducedRate = reducedRate; }
    public void setReducedRate2(BigDecimal reducedRate2) { this.reducedRate2 = reducedRate2; }
    public void setSuperReducedRate(BigDecimal superReducedRate) { this.superReducedRate = superReducedRate; }
    public void setParkingRate(BigDecimal parkingRate) { this.parkingRate = parkingRate; }
    public void setIsEuMember(Boolean isEuMember) { this.isEuMember = isEuMember; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }
    public void setEffectiveTo(LocalDate effectiveTo) { this.effectiveTo = effectiveTo; }

    // Builder
    public static EuVatRateBuilder builder() { return new EuVatRateBuilder(); }

    public static class EuVatRateBuilder {
        private Long id;
        private String countryCode;
        private String countryName;
        private BigDecimal standardRate;
        private BigDecimal reducedRate;
        private BigDecimal reducedRate2;
        private BigDecimal superReducedRate;
        private BigDecimal parkingRate;
        private Boolean isEuMember;
        private LocalDate effectiveFrom;
        private LocalDate effectiveTo;

        public EuVatRateBuilder id(Long id) { this.id = id; return this; }
        public EuVatRateBuilder countryCode(String countryCode) { this.countryCode = countryCode; return this; }
        public EuVatRateBuilder countryName(String countryName) { this.countryName = countryName; return this; }
        public EuVatRateBuilder standardRate(BigDecimal standardRate) { this.standardRate = standardRate; return this; }
        public EuVatRateBuilder reducedRate(BigDecimal reducedRate) { this.reducedRate = reducedRate; return this; }
        public EuVatRateBuilder reducedRate2(BigDecimal reducedRate2) { this.reducedRate2 = reducedRate2; return this; }
        public EuVatRateBuilder superReducedRate(BigDecimal superReducedRate) { this.superReducedRate = superReducedRate; return this; }
        public EuVatRateBuilder parkingRate(BigDecimal parkingRate) { this.parkingRate = parkingRate; return this; }
        public EuVatRateBuilder isEuMember(Boolean isEuMember) { this.isEuMember = isEuMember; return this; }
        public EuVatRateBuilder effectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; return this; }
        public EuVatRateBuilder effectiveTo(LocalDate effectiveTo) { this.effectiveTo = effectiveTo; return this; }

        public EuVatRate build() {
            return new EuVatRate(id, countryCode, countryName, standardRate, reducedRate, reducedRate2,
                    superReducedRate, parkingRate, isEuMember, effectiveFrom, effectiveTo);
        }
    }
}
