package com.euvatease.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "oss_report_lines")
public class OssReportLine {

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

    public OssReportLine() {
    }

    public OssReportLine(Long id, OssReport report, String countryCode, String countryName, BigDecimal vatRate,
                         BigDecimal taxableAmount, BigDecimal vatAmount, BigDecimal totalAmount, Integer ordersCount,
                         BigDecimal b2cAmount, BigDecimal b2bExemptAmount, LocalDateTime createdAt) {
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public OssReport getReport() { return report; }
    public String getCountryCode() { return countryCode; }
    public String getCountryName() { return countryName; }
    public BigDecimal getVatRate() { return vatRate; }
    public BigDecimal getTaxableAmount() { return taxableAmount; }
    public BigDecimal getVatAmount() { return vatAmount; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public Integer getOrdersCount() { return ordersCount; }
    public BigDecimal getB2cAmount() { return b2cAmount; }
    public BigDecimal getB2bExemptAmount() { return b2bExemptAmount; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setReport(OssReport report) { this.report = report; }
    public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
    public void setCountryName(String countryName) { this.countryName = countryName; }
    public void setVatRate(BigDecimal vatRate) { this.vatRate = vatRate; }
    public void setTaxableAmount(BigDecimal taxableAmount) { this.taxableAmount = taxableAmount; }
    public void setVatAmount(BigDecimal vatAmount) { this.vatAmount = vatAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setOrdersCount(Integer ordersCount) { this.ordersCount = ordersCount; }
    public void setB2cAmount(BigDecimal b2cAmount) { this.b2cAmount = b2cAmount; }
    public void setB2bExemptAmount(BigDecimal b2bExemptAmount) { this.b2bExemptAmount = b2bExemptAmount; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static OssReportLineBuilder builder() { return new OssReportLineBuilder(); }

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

        public OssReportLineBuilder id(Long id) { this.id = id; return this; }
        public OssReportLineBuilder report(OssReport report) { this.report = report; return this; }
        public OssReportLineBuilder countryCode(String countryCode) { this.countryCode = countryCode; return this; }
        public OssReportLineBuilder countryName(String countryName) { this.countryName = countryName; return this; }
        public OssReportLineBuilder vatRate(BigDecimal vatRate) { this.vatRate = vatRate; return this; }
        public OssReportLineBuilder taxableAmount(BigDecimal taxableAmount) { this.taxableAmount = taxableAmount; return this; }
        public OssReportLineBuilder vatAmount(BigDecimal vatAmount) { this.vatAmount = vatAmount; return this; }
        public OssReportLineBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public OssReportLineBuilder ordersCount(Integer ordersCount) { this.ordersCount = ordersCount; return this; }
        public OssReportLineBuilder b2cAmount(BigDecimal b2cAmount) { this.b2cAmount = b2cAmount; return this; }
        public OssReportLineBuilder b2bExemptAmount(BigDecimal b2bExemptAmount) { this.b2bExemptAmount = b2bExemptAmount; return this; }
        public OssReportLineBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public OssReportLine build() {
            return new OssReportLine(id, report, countryCode, countryName, vatRate, taxableAmount, vatAmount,
                    totalAmount, ordersCount, b2cAmount, b2bExemptAmount, createdAt);
        }
    }
}
