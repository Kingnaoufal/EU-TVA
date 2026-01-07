package com.euvatease.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    private Shop shop;

    @Column(name = "shopify_order_id", nullable = false)
    private String shopifyOrderId;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "customer_country_code")
    private String customerCountryCode;

    @Column(name = "customer_country_name")
    private String customerCountryName;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "customer_vat_number")
    private String customerVatNumber;

    @Column(name = "is_b2b")
    private Boolean isB2b;

    @Column(name = "total_amount", precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "subtotal_amount", precision = 12, scale = 2)
    private BigDecimal subtotalAmount;

    @Column(name = "shipping_amount", precision = 12, scale = 2)
    private BigDecimal shippingAmount;

    @Column(name = "tax_amount", precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "currency")
    private String currency;

    @Column(name = "applied_vat_rate", precision = 5, scale = 2)
    private BigDecimal appliedVatRate;

    @Column(name = "expected_vat_rate", precision = 5, scale = 2)
    private BigDecimal expectedVatRate;

    @Column(name = "calculated_vat_amount", precision = 12, scale = 2)
    private BigDecimal calculatedVatAmount;

    @Column(name = "vat_difference", precision = 12, scale = 2)
    private BigDecimal vatDifference;

    @Column(name = "has_vat_error")
    private Boolean hasVatError;

    @Column(name = "vat_error_type")
    private String vatErrorType;

    @Column(name = "vat_exempt")
    private Boolean vatExempt;

    @Column(name = "vat_exempt_reason")
    private String vatExemptReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_status")
    private FinancialStatus financialStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_status")
    private FulfillmentStatus fulfillmentStatus;

    @Column(name = "is_refunded")
    private Boolean isRefunded;

    @Column(name = "refund_amount", precision = 12, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "included_in_oss_report")
    private Boolean includedInOssReport;

    @Column(name = "oss_report_id")
    private Long ossReportId;

    @Column(name = "raw_data", columnDefinition = "TEXT")
    private String rawData;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Order() {
    }

    public Order(Long id, Shop shop, String shopifyOrderId, String orderNumber, LocalDateTime orderDate,
                 String customerCountryCode, String customerCountryName, String customerEmail,
                 String customerVatNumber, Boolean isB2b, BigDecimal totalAmount, BigDecimal subtotalAmount,
                 BigDecimal shippingAmount, BigDecimal taxAmount, String currency, BigDecimal appliedVatRate,
                 BigDecimal expectedVatRate, BigDecimal calculatedVatAmount, BigDecimal vatDifference,
                 Boolean hasVatError, String vatErrorType, Boolean vatExempt, String vatExemptReason,
                 FinancialStatus financialStatus, FulfillmentStatus fulfillmentStatus, Boolean isRefunded,
                 BigDecimal refundAmount, Boolean includedInOssReport, Long ossReportId, String rawData,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.shop = shop;
        this.shopifyOrderId = shopifyOrderId;
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.customerCountryCode = customerCountryCode;
        this.customerCountryName = customerCountryName;
        this.customerEmail = customerEmail;
        this.customerVatNumber = customerVatNumber;
        this.isB2b = isB2b;
        this.totalAmount = totalAmount;
        this.subtotalAmount = subtotalAmount;
        this.shippingAmount = shippingAmount;
        this.taxAmount = taxAmount;
        this.currency = currency;
        this.appliedVatRate = appliedVatRate;
        this.expectedVatRate = expectedVatRate;
        this.calculatedVatAmount = calculatedVatAmount;
        this.vatDifference = vatDifference;
        this.hasVatError = hasVatError;
        this.vatErrorType = vatErrorType;
        this.vatExempt = vatExempt;
        this.vatExemptReason = vatExemptReason;
        this.financialStatus = financialStatus;
        this.fulfillmentStatus = fulfillmentStatus;
        this.isRefunded = isRefunded;
        this.refundAmount = refundAmount;
        this.includedInOssReport = includedInOssReport;
        this.ossReportId = ossReportId;
        this.rawData = rawData;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isB2b == null) isB2b = false;
        if (hasVatError == null) hasVatError = false;
        if (vatExempt == null) vatExempt = false;
        if (isRefunded == null) isRefunded = false;
        if (includedInOssReport == null) includedInOssReport = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public Shop getShop() { return shop; }
    public String getShopifyOrderId() { return shopifyOrderId; }
    public String getOrderNumber() { return orderNumber; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getCustomerCountryCode() { return customerCountryCode; }
    public String getCustomerCountryName() { return customerCountryName; }
    public String getCustomerEmail() { return customerEmail; }
    public String getCustomerVatNumber() { return customerVatNumber; }
    public Boolean getIsB2b() { return isB2b; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getSubtotalAmount() { return subtotalAmount; }
    public BigDecimal getShippingAmount() { return shippingAmount; }
    public BigDecimal getTaxAmount() { return taxAmount; }
    public String getCurrency() { return currency; }
    public BigDecimal getAppliedVatRate() { return appliedVatRate; }
    public BigDecimal getExpectedVatRate() { return expectedVatRate; }
    public BigDecimal getCalculatedVatAmount() { return calculatedVatAmount; }
    public BigDecimal getVatDifference() { return vatDifference; }
    public Boolean getHasVatError() { return hasVatError; }
    public String getVatErrorType() { return vatErrorType; }
    public Boolean getVatExempt() { return vatExempt; }
    public String getVatExemptReason() { return vatExemptReason; }
    public FinancialStatus getFinancialStatus() { return financialStatus; }
    public FulfillmentStatus getFulfillmentStatus() { return fulfillmentStatus; }
    public Boolean getIsRefunded() { return isRefunded; }
    public BigDecimal getRefundAmount() { return refundAmount; }
    public Boolean getIncludedInOssReport() { return includedInOssReport; }
    public Long getOssReportId() { return ossReportId; }
    public String getRawData() { return rawData; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setShop(Shop shop) { this.shop = shop; }
    public void setShopifyOrderId(String shopifyOrderId) { this.shopifyOrderId = shopifyOrderId; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public void setCustomerCountryCode(String customerCountryCode) { this.customerCountryCode = customerCountryCode; }
    public void setCustomerCountryName(String customerCountryName) { this.customerCountryName = customerCountryName; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public void setCustomerVatNumber(String customerVatNumber) { this.customerVatNumber = customerVatNumber; }
    public void setIsB2b(Boolean isB2b) { this.isB2b = isB2b; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setSubtotalAmount(BigDecimal subtotalAmount) { this.subtotalAmount = subtotalAmount; }
    public void setShippingAmount(BigDecimal shippingAmount) { this.shippingAmount = shippingAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setAppliedVatRate(BigDecimal appliedVatRate) { this.appliedVatRate = appliedVatRate; }
    public void setExpectedVatRate(BigDecimal expectedVatRate) { this.expectedVatRate = expectedVatRate; }
    public void setCalculatedVatAmount(BigDecimal calculatedVatAmount) { this.calculatedVatAmount = calculatedVatAmount; }
    public void setVatDifference(BigDecimal vatDifference) { this.vatDifference = vatDifference; }
    public void setHasVatError(Boolean hasVatError) { this.hasVatError = hasVatError; }
    public void setVatErrorType(String vatErrorType) { this.vatErrorType = vatErrorType; }
    public void setVatExempt(Boolean vatExempt) { this.vatExempt = vatExempt; }
    public void setVatExemptReason(String vatExemptReason) { this.vatExemptReason = vatExemptReason; }
    public void setFinancialStatus(FinancialStatus financialStatus) { this.financialStatus = financialStatus; }
    public void setFulfillmentStatus(FulfillmentStatus fulfillmentStatus) { this.fulfillmentStatus = fulfillmentStatus; }
    public void setIsRefunded(Boolean isRefunded) { this.isRefunded = isRefunded; }
    public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
    public void setIncludedInOssReport(Boolean includedInOssReport) { this.includedInOssReport = includedInOssReport; }
    public void setOssReportId(Long ossReportId) { this.ossReportId = ossReportId; }
    public void setRawData(String rawData) { this.rawData = rawData; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // Builder
    public static OrderBuilder builder() { return new OrderBuilder(); }

    public static class OrderBuilder {
        private Long id;
        private Shop shop;
        private String shopifyOrderId;
        private String orderNumber;
        private LocalDateTime orderDate;
        private String customerCountryCode;
        private String customerCountryName;
        private String customerEmail;
        private String customerVatNumber;
        private Boolean isB2b;
        private BigDecimal totalAmount;
        private BigDecimal subtotalAmount;
        private BigDecimal shippingAmount;
        private BigDecimal taxAmount;
        private String currency;
        private BigDecimal appliedVatRate;
        private BigDecimal expectedVatRate;
        private BigDecimal calculatedVatAmount;
        private BigDecimal vatDifference;
        private Boolean hasVatError;
        private String vatErrorType;
        private Boolean vatExempt;
        private String vatExemptReason;
        private FinancialStatus financialStatus;
        private FulfillmentStatus fulfillmentStatus;
        private Boolean isRefunded;
        private BigDecimal refundAmount;
        private Boolean includedInOssReport;
        private Long ossReportId;
        private String rawData;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public OrderBuilder id(Long id) { this.id = id; return this; }
        public OrderBuilder shop(Shop shop) { this.shop = shop; return this; }
        public OrderBuilder shopifyOrderId(String shopifyOrderId) { this.shopifyOrderId = shopifyOrderId; return this; }
        public OrderBuilder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public OrderBuilder orderDate(LocalDateTime orderDate) { this.orderDate = orderDate; return this; }
        public OrderBuilder customerCountryCode(String customerCountryCode) { this.customerCountryCode = customerCountryCode; return this; }
        public OrderBuilder customerCountryName(String customerCountryName) { this.customerCountryName = customerCountryName; return this; }
        public OrderBuilder customerEmail(String customerEmail) { this.customerEmail = customerEmail; return this; }
        public OrderBuilder customerVatNumber(String customerVatNumber) { this.customerVatNumber = customerVatNumber; return this; }
        public OrderBuilder isB2b(Boolean isB2b) { this.isB2b = isB2b; return this; }
        public OrderBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public OrderBuilder subtotalAmount(BigDecimal subtotalAmount) { this.subtotalAmount = subtotalAmount; return this; }
        public OrderBuilder shippingAmount(BigDecimal shippingAmount) { this.shippingAmount = shippingAmount; return this; }
        public OrderBuilder taxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; return this; }
        public OrderBuilder currency(String currency) { this.currency = currency; return this; }
        public OrderBuilder appliedVatRate(BigDecimal appliedVatRate) { this.appliedVatRate = appliedVatRate; return this; }
        public OrderBuilder expectedVatRate(BigDecimal expectedVatRate) { this.expectedVatRate = expectedVatRate; return this; }
        public OrderBuilder calculatedVatAmount(BigDecimal calculatedVatAmount) { this.calculatedVatAmount = calculatedVatAmount; return this; }
        public OrderBuilder vatDifference(BigDecimal vatDifference) { this.vatDifference = vatDifference; return this; }
        public OrderBuilder hasVatError(Boolean hasVatError) { this.hasVatError = hasVatError; return this; }
        public OrderBuilder vatErrorType(String vatErrorType) { this.vatErrorType = vatErrorType; return this; }
        public OrderBuilder vatExempt(Boolean vatExempt) { this.vatExempt = vatExempt; return this; }
        public OrderBuilder vatExemptReason(String vatExemptReason) { this.vatExemptReason = vatExemptReason; return this; }
        public OrderBuilder financialStatus(FinancialStatus financialStatus) { this.financialStatus = financialStatus; return this; }
        public OrderBuilder fulfillmentStatus(FulfillmentStatus fulfillmentStatus) { this.fulfillmentStatus = fulfillmentStatus; return this; }
        public OrderBuilder isRefunded(Boolean isRefunded) { this.isRefunded = isRefunded; return this; }
        public OrderBuilder refundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; return this; }
        public OrderBuilder includedInOssReport(Boolean includedInOssReport) { this.includedInOssReport = includedInOssReport; return this; }
        public OrderBuilder ossReportId(Long ossReportId) { this.ossReportId = ossReportId; return this; }
        public OrderBuilder rawData(String rawData) { this.rawData = rawData; return this; }
        public OrderBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public OrderBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Order build() {
            return new Order(id, shop, shopifyOrderId, orderNumber, orderDate, customerCountryCode,
                    customerCountryName, customerEmail, customerVatNumber, isB2b, totalAmount, subtotalAmount,
                    shippingAmount, taxAmount, currency, appliedVatRate, expectedVatRate, calculatedVatAmount,
                    vatDifference, hasVatError, vatErrorType, vatExempt, vatExemptReason, financialStatus,
                    fulfillmentStatus, isRefunded, refundAmount, includedInOssReport, ossReportId, rawData,
                    createdAt, updatedAt);
        }
    }

    public enum FinancialStatus {
        PENDING, AUTHORIZED, PARTIALLY_PAID, PAID, PARTIALLY_REFUNDED, REFUNDED, VOIDED
    }

    public enum FulfillmentStatus {
        NULL, PARTIAL, FULFILLED
    }
}
