package com.euvatease.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "oss_reports")
public class OssReport {

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

    public OssReport() {
    }

    public OssReport(Long id, Shop shop, Integer year, Integer quarter, LocalDateTime periodStart,
                     LocalDateTime periodEnd, BigDecimal totalSales, BigDecimal totalVat, Integer totalOrders,
                     Integer b2bOrders, Integer b2cOrders, Integer exemptOrders, Integer countriesCount,
                     ReportStatus status, String csvFilePath, String pdfFilePath, String csvFileUrl,
                     String pdfFileUrl, String reportData, LocalDateTime generatedAt, LocalDateTime downloadedAt,
                     LocalDateTime submittedAt, String notes, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) status = ReportStatus.DRAFT;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public Shop getShop() { return shop; }
    public Integer getYear() { return year; }
    public Integer getQuarter() { return quarter; }
    public LocalDateTime getPeriodStart() { return periodStart; }
    public LocalDateTime getPeriodEnd() { return periodEnd; }
    public BigDecimal getTotalSales() { return totalSales; }
    public BigDecimal getTotalVat() { return totalVat; }
    public Integer getTotalOrders() { return totalOrders; }
    public Integer getB2bOrders() { return b2bOrders; }
    public Integer getB2cOrders() { return b2cOrders; }
    public Integer getExemptOrders() { return exemptOrders; }
    public Integer getCountriesCount() { return countriesCount; }
    public ReportStatus getStatus() { return status; }
    public String getCsvFilePath() { return csvFilePath; }
    public String getPdfFilePath() { return pdfFilePath; }
    public String getCsvFileUrl() { return csvFileUrl; }
    public String getPdfFileUrl() { return pdfFileUrl; }
    public String getReportData() { return reportData; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public LocalDateTime getDownloadedAt() { return downloadedAt; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public String getNotes() { return notes; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setShop(Shop shop) { this.shop = shop; }
    public void setYear(Integer year) { this.year = year; }
    public void setQuarter(Integer quarter) { this.quarter = quarter; }
    public void setPeriodStart(LocalDateTime periodStart) { this.periodStart = periodStart; }
    public void setPeriodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; }
    public void setTotalSales(BigDecimal totalSales) { this.totalSales = totalSales; }
    public void setTotalVat(BigDecimal totalVat) { this.totalVat = totalVat; }
    public void setTotalOrders(Integer totalOrders) { this.totalOrders = totalOrders; }
    public void setB2bOrders(Integer b2bOrders) { this.b2bOrders = b2bOrders; }
    public void setB2cOrders(Integer b2cOrders) { this.b2cOrders = b2cOrders; }
    public void setExemptOrders(Integer exemptOrders) { this.exemptOrders = exemptOrders; }
    public void setCountriesCount(Integer countriesCount) { this.countriesCount = countriesCount; }
    public void setStatus(ReportStatus status) { this.status = status; }
    public void setCsvFilePath(String csvFilePath) { this.csvFilePath = csvFilePath; }
    public void setPdfFilePath(String pdfFilePath) { this.pdfFilePath = pdfFilePath; }
    public void setCsvFileUrl(String csvFileUrl) { this.csvFileUrl = csvFileUrl; }
    public void setPdfFileUrl(String pdfFileUrl) { this.pdfFileUrl = pdfFileUrl; }
    public void setReportData(String reportData) { this.reportData = reportData; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    public void setDownloadedAt(LocalDateTime downloadedAt) { this.downloadedAt = downloadedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
    public void setNotes(String notes) { this.notes = notes; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder
    public static OssReportBuilder builder() { return new OssReportBuilder(); }

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

        public OssReportBuilder id(Long id) { this.id = id; return this; }
        public OssReportBuilder shop(Shop shop) { this.shop = shop; return this; }
        public OssReportBuilder year(Integer year) { this.year = year; return this; }
        public OssReportBuilder quarter(Integer quarter) { this.quarter = quarter; return this; }
        public OssReportBuilder periodStart(LocalDateTime periodStart) { this.periodStart = periodStart; return this; }
        public OssReportBuilder periodEnd(LocalDateTime periodEnd) { this.periodEnd = periodEnd; return this; }
        public OssReportBuilder totalSales(BigDecimal totalSales) { this.totalSales = totalSales; return this; }
        public OssReportBuilder totalVat(BigDecimal totalVat) { this.totalVat = totalVat; return this; }
        public OssReportBuilder totalOrders(Integer totalOrders) { this.totalOrders = totalOrders; return this; }
        public OssReportBuilder b2bOrders(Integer b2bOrders) { this.b2bOrders = b2bOrders; return this; }
        public OssReportBuilder b2cOrders(Integer b2cOrders) { this.b2cOrders = b2cOrders; return this; }
        public OssReportBuilder exemptOrders(Integer exemptOrders) { this.exemptOrders = exemptOrders; return this; }
        public OssReportBuilder countriesCount(Integer countriesCount) { this.countriesCount = countriesCount; return this; }
        public OssReportBuilder status(ReportStatus status) { this.status = status; return this; }
        public OssReportBuilder csvFilePath(String csvFilePath) { this.csvFilePath = csvFilePath; return this; }
        public OssReportBuilder pdfFilePath(String pdfFilePath) { this.pdfFilePath = pdfFilePath; return this; }
        public OssReportBuilder csvFileUrl(String csvFileUrl) { this.csvFileUrl = csvFileUrl; return this; }
        public OssReportBuilder pdfFileUrl(String pdfFileUrl) { this.pdfFileUrl = pdfFileUrl; return this; }
        public OssReportBuilder reportData(String reportData) { this.reportData = reportData; return this; }
        public OssReportBuilder generatedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; return this; }
        public OssReportBuilder downloadedAt(LocalDateTime downloadedAt) { this.downloadedAt = downloadedAt; return this; }
        public OssReportBuilder submittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; return this; }
        public OssReportBuilder notes(String notes) { this.notes = notes; return this; }
        public OssReportBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public OssReportBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public OssReport build() {
            return new OssReport(id, shop, year, quarter, periodStart, periodEnd, totalSales, totalVat,
                    totalOrders, b2bOrders, b2cOrders, exemptOrders, countriesCount, status, csvFilePath,
                    pdfFilePath, csvFileUrl, pdfFileUrl, reportData, generatedAt, downloadedAt, submittedAt,
                    notes, createdAt, updatedAt);
        }
    }

    public String getPeriodLabel() {
        return String.format("T%d %d", quarter, year);
    }

    public String getQuarterLabel() {
        return String.format("T%d %d", quarter, year);
    }

    public String getStatusMessage() {
        if (status == null) return "Inconnu";
        return switch (status) {
            case DRAFT -> "Brouillon";
            case GENERATED -> "Généré";
            case DOWNLOADED -> "Téléchargé";
            case SUBMITTED -> "Soumis";
        };
    }

    public enum ReportStatus {
        DRAFT,
        GENERATED,
        DOWNLOADED,
        SUBMITTED
    }
}
