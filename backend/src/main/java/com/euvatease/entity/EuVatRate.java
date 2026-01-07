package com.euvatease.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "eu_vat_rates")
public class EuVatRate {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

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

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public EuVatRate() {
    }

    public EuVatRate(@Nullable Long id,
                     @Nonnull String countryCode,
                     @Nonnull String countryName,
                     @Nonnull BigDecimal standardRate,
                     @Nullable BigDecimal reducedRate,
                     @Nullable BigDecimal reducedRate2,
                     @Nullable BigDecimal superReducedRate,
                     @Nullable BigDecimal parkingRate,
                     @Nullable Boolean isEuMember,
                     @Nonnull LocalDate effectiveFrom,
                     @Nullable LocalDate effectiveTo) {
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

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    public static EuVatRateBuilder builder() {
        return new EuVatRateBuilder();
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @Nonnull
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(@Nonnull String countryCode) {
        this.countryCode = countryCode;
    }

    @Nonnull
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(@Nonnull String countryName) {
        this.countryName = countryName;
    }

    @Nonnull
    public BigDecimal getStandardRate() {
        return standardRate;
    }

    public void setStandardRate(@Nonnull BigDecimal standardRate) {
        this.standardRate = standardRate;
    }

    @Nullable
    public BigDecimal getReducedRate() {
        return reducedRate;
    }

    public void setReducedRate(@Nullable BigDecimal reducedRate) {
        this.reducedRate = reducedRate;
    }

    @Nullable
    public BigDecimal getReducedRate2() {
        return reducedRate2;
    }

    public void setReducedRate2(@Nullable BigDecimal reducedRate2) {
        this.reducedRate2 = reducedRate2;
    }

    @Nullable
    public BigDecimal getSuperReducedRate() {
        return superReducedRate;
    }

    public void setSuperReducedRate(@Nullable BigDecimal superReducedRate) {
        this.superReducedRate = superReducedRate;
    }

    @Nullable
    public BigDecimal getParkingRate() {
        return parkingRate;
    }

    public void setParkingRate(@Nullable BigDecimal parkingRate) {
        this.parkingRate = parkingRate;
    }

    @Nullable
    public Boolean getIsEuMember() {
        return isEuMember;
    }

    public void setIsEuMember(@Nullable Boolean isEuMember) {
        this.isEuMember = isEuMember;
    }

    @Nonnull
    public LocalDate getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(@Nonnull LocalDate effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    @Nullable
    public LocalDate getEffectiveTo() {
        return effectiveTo;
    }

    public void setEffectiveTo(@Nullable LocalDate effectiveTo) {
        this.effectiveTo = effectiveTo;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Nested Classes
    //~ ----------------------------------------------------------------------------------------------------------------

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

        @Nonnull
        public EuVatRateBuilder id(@Nullable Long id) {
            this.id = id;
            return this;
        }

        @Nonnull
        public EuVatRateBuilder countryCode(@Nonnull String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        @Nonnull
        public EuVatRateBuilder countryName(@Nonnull String countryName) {
            this.countryName = countryName;
            return this;
        }

        @Nonnull
        public EuVatRateBuilder standardRate(@Nonnull BigDecimal standardRate) {
            this.standardRate = standardRate;
            return this;
        }

        @Nonnull
        public EuVatRateBuilder reducedRate(@Nullable BigDecimal reducedRate) {
            this.reducedRate = reducedRate;
            return this;
        }

        @Nonnull
        public EuVatRateBuilder reducedRate2(@Nullable BigDecimal reducedRate2) {
            this.reducedRate2 = reducedRate2;
            return this;
        }

        @Nonnull
        public EuVatRateBuilder superReducedRate(@Nullable BigDecimal superReducedRate) {
            this.superReducedRate = superReducedRate;
            return this;
        }

        @Nonnull
        public EuVatRateBuilder parkingRate(@Nullable BigDecimal parkingRate) {
            this.parkingRate = parkingRate;
            return this;
        }

        @Nonnull
        public EuVatRateBuilder isEuMember(@Nullable Boolean isEuMember) {
            this.isEuMember = isEuMember;
            return this;
        }

        @Nonnull
        public EuVatRateBuilder effectiveFrom(@Nonnull LocalDate effectiveFrom) {
            this.effectiveFrom = effectiveFrom;
            return this;
        }

        @Nonnull
        public EuVatRateBuilder effectiveTo(@Nullable LocalDate effectiveTo) {
            this.effectiveTo = effectiveTo;
            return this;
        }

        @Nonnull
        public EuVatRate build() {
            return new EuVatRate(id, countryCode, countryName, standardRate, reducedRate, reducedRate2,
                    superReducedRate, parkingRate, isEuMember, effectiveFrom, effectiveTo);
        }
    }
}
