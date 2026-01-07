package com.euvatease.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity representing a line item in an OSS (One-Stop Shop) VAT report.
 * Each line contains VAT information for a specific country and rate.
 */
@Entity
@Table(name = "oss_report_lines")
public class OssReportLine {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private OssReport report;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "country_name")
    private String countryName;

    @Column(name = "vat_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal vatRate;

    @Column(name = "taxable_amount", precision = 14, scale = 2)
    private BigDecimal taxableAmount;

    @Column(name = "vat_amount", precision = 14, scale = 2)
    private BigDecimal vatAmount;

    @Column(name = "total_amount", precision = 14, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "orders_count")
    private Integer ordersCount;

    @Column(name = "b2c_amount", precision = 14, scale = 2)
    private BigDecimal b2cAmount;

    @Column(name = "b2b_exempt_amount", precision = 14, scale = 2)
    private BigDecimal b2bExemptAmount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public OssReportLine() {
    }

    public OssReportLine(@Nullable Long id,
                         @Nonnull OssReport report,
                         @Nonnull String countryCode,
                         @Nullable String countryName,
                         @Nonnull BigDecimal vatRate,
                         @Nullable BigDecimal taxableAmount,
                         @Nullable BigDecimal vatAmount,
                         @Nullable BigDecimal totalAmount,
                         @Nullable Integer ordersCount,
                         @Nullable BigDecimal b2cAmount,
                         @Nullable BigDecimal b2bExemptAmount,
                         @Nullable LocalDateTime createdAt) {
        this.id = id;
        this.report = report;
        this.countryCode = countryCode;
        this.countryName = countryName;
        this.vatRate = vatRate;
        this.taxableAmount = taxableAmount;
        this.vatAmount = vatAmount;
        this.totalAmount = totalAmount;
        this.ordersCount = ordersCount;
        this.b2cAmount = b2cAmount;
        this.b2bExemptAmount = b2bExemptAmount;
        this.createdAt = createdAt;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Lifecycle methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Getters/Setters
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @Nonnull
    public OssReport getReport() {
        return report;
    }

    public void setReport(@Nonnull OssReport report) {
        this.report = report;
    }

    @Nonnull
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(@Nonnull String countryCode) {
        this.countryCode = countryCode;
    }

    @Nullable
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(@Nullable String countryName) {
        this.countryName = countryName;
    }

    @Nonnull
    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(@Nonnull BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    @Nullable
    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(@Nullable BigDecimal taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    @Nullable
    public BigDecimal getVatAmount() {
        return vatAmount;
    }

    public void setVatAmount(@Nullable BigDecimal vatAmount) {
        this.vatAmount = vatAmount;
    }

    @Nullable
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(@Nullable BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Nullable
    public Integer getOrdersCount() {
        return ordersCount;
    }

    public void setOrdersCount(@Nullable Integer ordersCount) {
        this.ordersCount = ordersCount;
    }

    @Nullable
    public BigDecimal getB2cAmount() {
        return b2cAmount;
    }

    public void setB2cAmount(@Nullable BigDecimal b2cAmount) {
        this.b2cAmount = b2cAmount;
    }

    @Nullable
    public BigDecimal getB2bExemptAmount() {
        return b2bExemptAmount;
    }

    public void setB2bExemptAmount(@Nullable BigDecimal b2bExemptAmount) {
        this.b2bExemptAmount = b2bExemptAmount;
    }

    @Nullable
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Inner Classes
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    public static OssReportLineBuilder builder() {
        return new OssReportLineBuilder();
    }

    public static class OssReportLineBuilder {

        private Long id;
        private OssReport report;
        private String countryCode;
        private String countryName;
        private BigDecimal vatRate;
        private BigDecimal taxableAmount;
        private BigDecimal vatAmount;
        private BigDecimal totalAmount;
        private Integer ordersCount;
        private BigDecimal b2cAmount;
        private BigDecimal b2bExemptAmount;
        private LocalDateTime createdAt;

        @Nonnull
        public OssReportLineBuilder id(@Nullable Long id) {
            this.id = id;
            return this;
        }

        @Nonnull
        public OssReportLineBuilder report(@Nonnull OssReport report) {
            this.report = report;
            return this;
        }

        @Nonnull
        public OssReportLineBuilder countryCode(@Nonnull String countryCode) {
            this.countryCode = countryCode;
            return this;
        }

        @Nonnull
        public OssReportLineBuilder countryName(@Nullable String countryName) {
            this.countryName = countryName;
            return this;
        }

        @Nonnull
        public OssReportLineBuilder vatRate(@Nonnull BigDecimal vatRate) {
            this.vatRate = vatRate;
            return this;
        }

        @Nonnull
        public OssReportLineBuilder taxableAmount(@Nullable BigDecimal taxableAmount) {
            this.taxableAmount = taxableAmount;
            return this;
        }

        @Nonnull
        public OssReportLineBuilder vatAmount(@Nullable BigDecimal vatAmount) {
            this.vatAmount = vatAmount;
            return this;
        }

        @Nonnull
        public OssReportLineBuilder totalAmount(@Nullable BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        @Nonnull
        public OssReportLineBuilder ordersCount(@Nullable Integer ordersCount) {
            this.ordersCount = ordersCount;
            return this;
        }

        @Nonnull
        public OssReportLineBuilder b2cAmount(@Nullable BigDecimal b2cAmount) {
            this.b2cAmount = b2cAmount;
            return this;
        }

        @Nonnull
        public OssReportLineBuilder b2bExemptAmount(@Nullable BigDecimal b2bExemptAmount) {
            this.b2bExemptAmount = b2bExemptAmount;
            return this;
        }

        @Nonnull
        public OssReportLineBuilder createdAt(@Nullable LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Nonnull
        public OssReportLine build() {
            return new OssReportLine(id, report, countryCode, countryName, vatRate, taxableAmount, vatAmount,
                    totalAmount, ordersCount, b2cAmount, b2bExemptAmount, createdAt);
        }
    }
}
