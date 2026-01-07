package com.euvatease.entity;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "oss_reports")
public class OssReport {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "quarter", nullable = false)
    private Integer quarter;

    @Column(name = "period_start")
    private LocalDateTime periodStart;

    @Column(name = "period_end")
    private LocalDateTime periodEnd;

    @Column(name = "total_sales", precision = 14, scale = 2)
    private BigDecimal totalSales;

    @Column(name = "total_vat", precision = 14, scale = 2)
    private BigDecimal totalVat;

    @Column(name = "total_orders")
    private Integer totalOrders;

    @Column(name = "b2b_orders")
    private Integer b2bOrders;

    @Column(name = "b2c_orders")
    private Integer b2cOrders;

    @Column(name = "exempt_orders")
    private Integer exemptOrders;

    @Column(name = "countries_count")
    private Integer countriesCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ReportStatus status;

    @Column(name = "csv_file_path")
    private String csvFilePath;

    @Column(name = "pdf_file_path")
    private String pdfFilePath;

    @Column(name = "csv_file_url")
    private String csvFileUrl;

    @Column(name = "pdf_file_url")
    private String pdfFileUrl;

    @Column(name = "report_data", columnDefinition = "TEXT")
    private String reportData;

    @Column(name = "generated_at")
    private LocalDateTime generatedAt;

    @Column(name = "downloaded_at")
    private LocalDateTime downloadedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public OssReport() {
    }

    public OssReport(@Nullable Long id,
                     @Nonnull Shop shop,
                     @Nonnull Integer year,
                     @Nonnull Integer quarter,
                     @Nullable LocalDateTime periodStart,
                     @Nullable LocalDateTime periodEnd,
                     @Nullable BigDecimal totalSales,
                     @Nullable BigDecimal totalVat,
                     @Nullable Integer totalOrders,
                     @Nullable Integer b2bOrders,
                     @Nullable Integer b2cOrders,
                     @Nullable Integer exemptOrders,
                     @Nullable Integer countriesCount,
                     @Nullable ReportStatus status,
                     @Nullable String csvFilePath,
                     @Nullable String pdfFilePath,
                     @Nullable String csvFileUrl,
                     @Nullable String pdfFileUrl,
                     @Nullable String reportData,
                     @Nullable LocalDateTime generatedAt,
                     @Nullable LocalDateTime downloadedAt,
                     @Nullable LocalDateTime submittedAt,
                     @Nullable String notes,
                     @Nullable LocalDateTime createdAt,
                     @Nullable LocalDateTime updatedAt) {
        this.id = id;
        this.shop = shop;
        this.year = year;
        this.quarter = quarter;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.totalSales = totalSales;
        this.totalVat = totalVat;
        this.totalOrders = totalOrders;
        this.b2bOrders = b2bOrders;
        this.b2cOrders = b2cOrders;
        this.exemptOrders = exemptOrders;
        this.countriesCount = countriesCount;
        this.status = status;
        this.csvFilePath = csvFilePath;
        this.pdfFilePath = pdfFilePath;
        this.csvFileUrl = csvFileUrl;
        this.pdfFileUrl = pdfFileUrl;
        this.reportData = reportData;
        this.generatedAt = generatedAt;
        this.downloadedAt = downloadedAt;
        this.submittedAt = submittedAt;
        this.notes = notes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    public static OssReportBuilder builder() {
        return new OssReportBuilder();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ReportStatus.DRAFT;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @Nullable
    public Long getId() {
        return id;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    @Nonnull
    public Shop getShop() {
        return shop;
    }

    public void setShop(@Nonnull Shop shop) {
        this.shop = shop;
    }

    @Nonnull
    public Integer getYear() {
        return year;
    }

    public void setYear(@Nonnull Integer year) {
        this.year = year;
    }

    @Nonnull
    public Integer getQuarter() {
        return quarter;
    }

    public void setQuarter(@Nonnull Integer quarter) {
        this.quarter = quarter;
    }

    @Nullable
    public LocalDateTime getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(@Nullable LocalDateTime periodStart) {
        this.periodStart = periodStart;
    }

    @Nullable
    public LocalDateTime getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(@Nullable LocalDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }

    @Nullable
    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(@Nullable BigDecimal totalSales) {
        this.totalSales = totalSales;
    }

    @Nullable
    public BigDecimal getTotalVat() {
        return totalVat;
    }

    public void setTotalVat(@Nullable BigDecimal totalVat) {
        this.totalVat = totalVat;
    }

    @Nullable
    public Integer getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(@Nullable Integer totalOrders) {
        this.totalOrders = totalOrders;
    }

    @Nullable
    public Integer getB2bOrders() {
        return b2bOrders;
    }

    public void setB2bOrders(@Nullable Integer b2bOrders) {
        this.b2bOrders = b2bOrders;
    }

    @Nullable
    public Integer getB2cOrders() {
        return b2cOrders;
    }

    public void setB2cOrders(@Nullable Integer b2cOrders) {
        this.b2cOrders = b2cOrders;
    }

    @Nullable
    public Integer getExemptOrders() {
        return exemptOrders;
    }

    public void setExemptOrders(@Nullable Integer exemptOrders) {
        this.exemptOrders = exemptOrders;
    }

    @Nullable
    public Integer getCountriesCount() {
        return countriesCount;
    }

    public void setCountriesCount(@Nullable Integer countriesCount) {
        this.countriesCount = countriesCount;
    }

    @Nullable
    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(@Nullable ReportStatus status) {
        this.status = status;
    }

    @Nullable
    public String getCsvFilePath() {
        return csvFilePath;
    }

    public void setCsvFilePath(@Nullable String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    @Nullable
    public String getPdfFilePath() {
        return pdfFilePath;
    }

    public void setPdfFilePath(@Nullable String pdfFilePath) {
        this.pdfFilePath = pdfFilePath;
    }

    @Nullable
    public String getCsvFileUrl() {
        return csvFileUrl;
    }

    public void setCsvFileUrl(@Nullable String csvFileUrl) {
        this.csvFileUrl = csvFileUrl;
    }

    @Nullable
    public String getPdfFileUrl() {
        return pdfFileUrl;
    }

    public void setPdfFileUrl(@Nullable String pdfFileUrl) {
        this.pdfFileUrl = pdfFileUrl;
    }

    @Nullable
    public String getReportData() {
        return reportData;
    }

    public void setReportData(@Nullable String reportData) {
        this.reportData = reportData;
    }

    @Nullable
    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(@Nullable LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    @Nullable
    public LocalDateTime getDownloadedAt() {
        return downloadedAt;
    }

    public void setDownloadedAt(@Nullable LocalDateTime downloadedAt) {
        this.downloadedAt = downloadedAt;
    }

    @Nullable
    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(@Nullable LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    @Nullable
    public String getNotes() {
        return notes;
    }

    public void setNotes(@Nullable String notes) {
        this.notes = notes;
    }

    @Nullable
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@Nullable LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Nullable
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(@Nullable LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Nonnull
    public String getPeriodLabel() {
        return String.format("T%d %d", quarter, year);
    }

    @Nonnull
    public String getQuarterLabel() {
        return String.format("T%d %d", quarter, year);
    }

    @Nonnull
    public String getStatusMessage() {
        if (status == null) {
            return "Inconnu";
        }
        return switch (status) {
            case DRAFT -> "Brouillon";
            case GENERATED -> "Généré";
            case DOWNLOADED -> "Téléchargé";
            case SUBMITTED -> "Soumis";
        };
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Nested Classes
    //~ ----------------------------------------------------------------------------------------------------------------

    public enum ReportStatus {
        DRAFT,
        GENERATED,
        DOWNLOADED,
        SUBMITTED
    }

    public static class OssReportBuilder {

        private Long id;
        private Shop shop;
        private Integer year;
        private Integer quarter;
        private LocalDateTime periodStart;
        private LocalDateTime periodEnd;
        private BigDecimal totalSales;
        private BigDecimal totalVat;
        private Integer totalOrders;
        private Integer b2bOrders;
        private Integer b2cOrders;
        private Integer exemptOrders;
        private Integer countriesCount;
        private ReportStatus status;
        private String csvFilePath;
        private String pdfFilePath;
        private String csvFileUrl;
        private String pdfFileUrl;
        private String reportData;
        private LocalDateTime generatedAt;
        private LocalDateTime downloadedAt;
        private LocalDateTime submittedAt;
        private String notes;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        @Nonnull
        public OssReportBuilder id(@Nullable Long id) {
            this.id = id;
            return this;
        }

        @Nonnull
        public OssReportBuilder shop(@Nonnull Shop shop) {
            this.shop = shop;
            return this;
        }

        @Nonnull
        public OssReportBuilder year(@Nonnull Integer year) {
            this.year = year;
            return this;
        }

        @Nonnull
        public OssReportBuilder quarter(@Nonnull Integer quarter) {
            this.quarter = quarter;
            return this;
        }

        @Nonnull
        public OssReportBuilder periodStart(@Nullable LocalDateTime periodStart) {
            this.periodStart = periodStart;
            return this;
        }

        @Nonnull
        public OssReportBuilder periodEnd(@Nullable LocalDateTime periodEnd) {
            this.periodEnd = periodEnd;
            return this;
        }

        @Nonnull
        public OssReportBuilder totalSales(@Nullable BigDecimal totalSales) {
            this.totalSales = totalSales;
            return this;
        }

        @Nonnull
        public OssReportBuilder totalVat(@Nullable BigDecimal totalVat) {
            this.totalVat = totalVat;
            return this;
        }

        @Nonnull
        public OssReportBuilder totalOrders(@Nullable Integer totalOrders) {
            this.totalOrders = totalOrders;
            return this;
        }

        @Nonnull
        public OssReportBuilder b2bOrders(@Nullable Integer b2bOrders) {
            this.b2bOrders = b2bOrders;
            return this;
        }

        @Nonnull
        public OssReportBuilder b2cOrders(@Nullable Integer b2cOrders) {
            this.b2cOrders = b2cOrders;
            return this;
        }

        @Nonnull
        public OssReportBuilder exemptOrders(@Nullable Integer exemptOrders) {
            this.exemptOrders = exemptOrders;
            return this;
        }

        @Nonnull
        public OssReportBuilder countriesCount(@Nullable Integer countriesCount) {
            this.countriesCount = countriesCount;
            return this;
        }

        @Nonnull
        public OssReportBuilder status(@Nullable ReportStatus status) {
            this.status = status;
            return this;
        }

        @Nonnull
        public OssReportBuilder csvFilePath(@Nullable String csvFilePath) {
            this.csvFilePath = csvFilePath;
            return this;
        }

        @Nonnull
        public OssReportBuilder pdfFilePath(@Nullable String pdfFilePath) {
            this.pdfFilePath = pdfFilePath;
            return this;
        }

        @Nonnull
        public OssReportBuilder csvFileUrl(@Nullable String csvFileUrl) {
            this.csvFileUrl = csvFileUrl;
            return this;
        }

        @Nonnull
        public OssReportBuilder pdfFileUrl(@Nullable String pdfFileUrl) {
            this.pdfFileUrl = pdfFileUrl;
            return this;
        }

        @Nonnull
        public OssReportBuilder reportData(@Nullable String reportData) {
            this.reportData = reportData;
            return this;
        }

        @Nonnull
        public OssReportBuilder generatedAt(@Nullable LocalDateTime generatedAt) {
            this.generatedAt = generatedAt;
            return this;
        }

        @Nonnull
        public OssReportBuilder downloadedAt(@Nullable LocalDateTime downloadedAt) {
            this.downloadedAt = downloadedAt;
            return this;
        }

        @Nonnull
        public OssReportBuilder submittedAt(@Nullable LocalDateTime submittedAt) {
            this.submittedAt = submittedAt;
            return this;
        }

        @Nonnull
        public OssReportBuilder notes(@Nullable String notes) {
            this.notes = notes;
            return this;
        }

        @Nonnull
        public OssReportBuilder createdAt(@Nullable LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Nonnull
        public OssReportBuilder updatedAt(@Nullable LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        @Nonnull
        public OssReport build() {
            return new OssReport(id, shop, year, quarter, periodStart, periodEnd, totalSales, totalVat,
                    totalOrders, b2bOrders, b2cOrders, exemptOrders, countriesCount, status, csvFilePath,
                    pdfFilePath, csvFileUrl, pdfFileUrl, reportData, generatedAt, downloadedAt, submittedAt,
                    notes, createdAt, updatedAt);
        }
    }
}
