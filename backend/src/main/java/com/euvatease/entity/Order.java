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
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Nullable
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shop_id", nullable = false)
    @Nonnull
    private Shop shop;

    @Column(name = "shopify_order_id", nullable = false)
    @Nonnull
    private String shopifyOrderId;

    @Column(name = "order_number")
    @Nullable
    private String orderNumber;

    @Column(name = "order_date")
    @Nullable
    private LocalDateTime orderDate;

    @Column(name = "customer_country_code")
    @Nullable
    private String customerCountryCode;

    @Column(name = "customer_country_name")
    @Nullable
    private String customerCountryName;

    @Column(name = "customer_email")
    @Nullable
    private String customerEmail;

    @Column(name = "customer_vat_number")
    @Nullable
    private String customerVatNumber;

    @Column(name = "is_b2b")
    @Nullable
    private Boolean isB2b;

    @Column(name = "total_amount", precision = 12, scale = 2)
    @Nullable
    private BigDecimal totalAmount;

    @Column(name = "subtotal_amount", precision = 12, scale = 2)
    @Nullable
    private BigDecimal subtotalAmount;

    @Column(name = "shipping_amount", precision = 12, scale = 2)
    @Nullable
    private BigDecimal shippingAmount;

    @Column(name = "tax_amount", precision = 12, scale = 2)
    @Nullable
    private BigDecimal taxAmount;

    @Column(name = "currency")
    @Nullable
    private String currency;

    @Column(name = "applied_vat_rate", precision = 5, scale = 2)
    @Nullable
    private BigDecimal appliedVatRate;

    @Column(name = "expected_vat_rate", precision = 5, scale = 2)
    @Nullable
    private BigDecimal expectedVatRate;

    @Column(name = "calculated_vat_amount", precision = 12, scale = 2)
    @Nullable
    private BigDecimal calculatedVatAmount;

    @Column(name = "vat_difference", precision = 12, scale = 2)
    @Nullable
    private BigDecimal vatDifference;

    @Column(name = "has_vat_error")
    @Nullable
    private Boolean hasVatError;

    @Column(name = "vat_error_type")
    @Nullable
    private String vatErrorType;

    @Column(name = "vat_exempt")
    @Nullable
    private Boolean vatExempt;

    @Column(name = "vat_exempt_reason")
    @Nullable
    private String vatExemptReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "financial_status")
    @Nullable
    private FinancialStatus financialStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "fulfillment_status")
    @Nullable
    private FulfillmentStatus fulfillmentStatus;

    @Column(name = "is_refunded")
    @Nullable
    private Boolean isRefunded;

    @Column(name = "refund_amount", precision = 12, scale = 2)
    @Nullable
    private BigDecimal refundAmount;

    @Column(name = "included_in_oss_report")
    @Nullable
    private Boolean includedInOssReport;

    @Column(name = "oss_report_id")
    @Nullable
    private Long ossReportId;

    @Column(name = "raw_data", columnDefinition = "TEXT")
    @Nullable
    private String rawData;

    @Column(name = "created_at")
    @Nullable
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @Nullable
    private LocalDateTime updatedAt;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public Order() {
    }

    public Order(@Nullable Long id,
                 @Nonnull Shop shop,
                 @Nonnull String shopifyOrderId,
                 @Nullable String orderNumber,
                 @Nullable LocalDateTime orderDate,
                 @Nullable String customerCountryCode,
                 @Nullable String customerCountryName,
                 @Nullable String customerEmail,
                 @Nullable String customerVatNumber,
                 @Nullable Boolean isB2b,
                 @Nullable BigDecimal totalAmount,
                 @Nullable BigDecimal subtotalAmount,
                 @Nullable BigDecimal shippingAmount,
                 @Nullable BigDecimal taxAmount,
                 @Nullable String currency,
                 @Nullable BigDecimal appliedVatRate,
                 @Nullable BigDecimal expectedVatRate,
                 @Nullable BigDecimal calculatedVatAmount,
                 @Nullable BigDecimal vatDifference,
                 @Nullable Boolean hasVatError,
                 @Nullable String vatErrorType,
                 @Nullable Boolean vatExempt,
                 @Nullable String vatExemptReason,
                 @Nullable FinancialStatus financialStatus,
                 @Nullable FulfillmentStatus fulfillmentStatus,
                 @Nullable Boolean isRefunded,
                 @Nullable BigDecimal refundAmount,
                 @Nullable Boolean includedInOssReport,
                 @Nullable Long ossReportId,
                 @Nullable String rawData,
                 @Nullable LocalDateTime createdAt,
                 @Nullable LocalDateTime updatedAt) {
        this.id = id;
        this.shop = Objects.requireNonNull(shop, "shop must not be null");
        this.shopifyOrderId = Objects.requireNonNull(shopifyOrderId, "shopifyOrderId must not be null");
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

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static OrderBuilder builder() {
        return new OrderBuilder();
    }

    @Nullable
    public BigDecimal getAppliedVatRate() {
        return appliedVatRate;
    }

    @Nullable
    public BigDecimal getCalculatedVatAmount() {
        return calculatedVatAmount;
    }

    @Nullable
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Nullable
    public String getCurrency() {
        return currency;
    }

    @Nullable
    public String getCustomerCountryCode() {
        return customerCountryCode;
    }

    @Nullable
    public String getCustomerCountryName() {
        return customerCountryName;
    }

    @Nullable
    public String getCustomerEmail() {
        return customerEmail;
    }

    @Nullable
    public String getCustomerVatNumber() {
        return customerVatNumber;
    }

    @Nullable
    public BigDecimal getExpectedVatRate() {
        return expectedVatRate;
    }

    @Nullable
    public FinancialStatus getFinancialStatus() {
        return financialStatus;
    }

    @Nullable
    public FulfillmentStatus getFulfillmentStatus() {
        return fulfillmentStatus;
    }

    @Nullable
    public Boolean getHasVatError() {
        return hasVatError;
    }

    @Nullable
    public Long getId() {
        return id;
    }

    @Nullable
    public Boolean getIncludedInOssReport() {
        return includedInOssReport;
    }

    @Nullable
    public Boolean getIsB2b() {
        return isB2b;
    }

    @Nullable
    public Boolean getIsRefunded() {
        return isRefunded;
    }

    @Nullable
    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    @Nullable
    public String getOrderNumber() {
        return orderNumber;
    }

    @Nullable
    public Long getOssReportId() {
        return ossReportId;
    }

    @Nullable
    public String getRawData() {
        return rawData;
    }

    @Nullable
    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    @Nullable
    public BigDecimal getShippingAmount() {
        return shippingAmount;
    }

    @Nonnull
    public Shop getShop() {
        return shop;
    }

    @Nonnull
    public String getShopifyOrderId() {
        return shopifyOrderId;
    }

    @Nullable
    public BigDecimal getSubtotalAmount() {
        return subtotalAmount;
    }

    @Nullable
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    @Nullable
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    @Nullable
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @Nullable
    public BigDecimal getVatDifference() {
        return vatDifference;
    }

    @Nullable
    public String getVatErrorType() {
        return vatErrorType;
    }

    @Nullable
    public Boolean getVatExempt() {
        return vatExempt;
    }

    @Nullable
    public String getVatExemptReason() {
        return vatExemptReason;
    }

    public void setAppliedVatRate(@Nullable BigDecimal appliedVatRate) {
        this.appliedVatRate = appliedVatRate;
    }

    public void setCalculatedVatAmount(@Nullable BigDecimal calculatedVatAmount) {
        this.calculatedVatAmount = calculatedVatAmount;
    }

    public void setCreatedAt(@Nullable LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public void setCurrency(@Nullable String currency) {
        this.currency = currency;
    }

    public void setCustomerCountryCode(@Nullable String customerCountryCode) {
        this.customerCountryCode = customerCountryCode;
    }

    public void setCustomerCountryName(@Nullable String customerCountryName) {
        this.customerCountryName = customerCountryName;
    }

    public void setCustomerEmail(@Nullable String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setCustomerVatNumber(@Nullable String customerVatNumber) {
        this.customerVatNumber = customerVatNumber;
    }

    public void setExpectedVatRate(@Nullable BigDecimal expectedVatRate) {
        this.expectedVatRate = expectedVatRate;
    }

    public void setFinancialStatus(@Nullable FinancialStatus financialStatus) {
        this.financialStatus = financialStatus;
    }

    public void setFulfillmentStatus(@Nullable FulfillmentStatus fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    public void setHasVatError(@Nullable Boolean hasVatError) {
        this.hasVatError = hasVatError;
    }

    public void setId(@Nullable Long id) {
        this.id = id;
    }

    public void setIncludedInOssReport(@Nullable Boolean includedInOssReport) {
        this.includedInOssReport = includedInOssReport;
    }

    public void setIsB2b(@Nullable Boolean isB2b) {
        this.isB2b = isB2b;
    }

    public void setIsRefunded(@Nullable Boolean isRefunded) {
        this.isRefunded = isRefunded;
    }

    public void setOrderDate(@Nullable LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public void setOrderNumber(@Nullable String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public void setOssReportId(@Nullable Long ossReportId) {
        this.ossReportId = ossReportId;
    }

    public void setRawData(@Nullable String rawData) {
        this.rawData = rawData;
    }

    public void setRefundAmount(@Nullable BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public void setShippingAmount(@Nullable BigDecimal shippingAmount) {
        this.shippingAmount = shippingAmount;
    }

    public void setShop(@Nonnull Shop shop) {
        this.shop = Objects.requireNonNull(shop, "shop must not be null");
    }

    public void setShopifyOrderId(@Nonnull String shopifyOrderId) {
        this.shopifyOrderId = Objects.requireNonNull(shopifyOrderId, "shopifyOrderId must not be null");
    }

    public void setSubtotalAmount(@Nullable BigDecimal subtotalAmount) {
        this.subtotalAmount = subtotalAmount;
    }

    public void setTaxAmount(@Nullable BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public void setTotalAmount(@Nullable BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setUpdatedAt(@Nullable LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setVatDifference(@Nullable BigDecimal vatDifference) {
        this.vatDifference = vatDifference;
    }

    public void setVatErrorType(@Nullable String vatErrorType) {
        this.vatErrorType = vatErrorType;
    }

    public void setVatExempt(@Nullable Boolean vatExempt) {
        this.vatExempt = vatExempt;
    }

    public void setVatExemptReason(@Nullable String vatExemptReason) {
        this.vatExemptReason = vatExemptReason;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isB2b == null) {
            isB2b = false;
        }
        if (hasVatError == null) {
            hasVatError = false;
        }
        if (vatExempt == null) {
            vatExempt = false;
        }
        if (isRefunded == null) {
            isRefunded = false;
        }
        if (includedInOssReport == null) {
            includedInOssReport = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Nested Classes
    //~ ----------------------------------------------------------------------------------------------------------------

    public enum FinancialStatus {
        PENDING,
        AUTHORIZED,
        PARTIALLY_PAID,
        PAID,
        PARTIALLY_REFUNDED,
        REFUNDED,
        VOIDED
    }

    public enum FulfillmentStatus {
        NULL,
        PARTIAL,
        FULFILLED
    }

    public static class OrderBuilder {

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Instance fields
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nullable
        private Long id;

        @Nullable
        private Shop shop;

        @Nullable
        private String shopifyOrderId;

        @Nullable
        private String orderNumber;

        @Nullable
        private LocalDateTime orderDate;

        @Nullable
        private String customerCountryCode;

        @Nullable
        private String customerCountryName;

        @Nullable
        private String customerEmail;

        @Nullable
        private String customerVatNumber;

        @Nullable
        private Boolean isB2b;

        @Nullable
        private BigDecimal totalAmount;

        @Nullable
        private BigDecimal subtotalAmount;

        @Nullable
        private BigDecimal shippingAmount;

        @Nullable
        private BigDecimal taxAmount;

        @Nullable
        private String currency;

        @Nullable
        private BigDecimal appliedVatRate;

        @Nullable
        private BigDecimal expectedVatRate;

        @Nullable
        private BigDecimal calculatedVatAmount;

        @Nullable
        private BigDecimal vatDifference;

        @Nullable
        private Boolean hasVatError;

        @Nullable
        private String vatErrorType;

        @Nullable
        private Boolean vatExempt;

        @Nullable
        private String vatExemptReason;

        @Nullable
        private FinancialStatus financialStatus;

        @Nullable
        private FulfillmentStatus fulfillmentStatus;

        @Nullable
        private Boolean isRefunded;

        @Nullable
        private BigDecimal refundAmount;

        @Nullable
        private Boolean includedInOssReport;

        @Nullable
        private Long ossReportId;

        @Nullable
        private String rawData;

        @Nullable
        private LocalDateTime createdAt;

        @Nullable
        private LocalDateTime updatedAt;

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Methods
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nonnull
        public OrderBuilder appliedVatRate(@Nullable BigDecimal appliedVatRate) {
            this.appliedVatRate = appliedVatRate;
            return this;
        }

        @Nonnull
        public Order build() {
            return new Order(id, shop, shopifyOrderId, orderNumber, orderDate, customerCountryCode,
                    customerCountryName, customerEmail, customerVatNumber, isB2b, totalAmount, subtotalAmount,
                    shippingAmount, taxAmount, currency, appliedVatRate, expectedVatRate, calculatedVatAmount,
                    vatDifference, hasVatError, vatErrorType, vatExempt, vatExemptReason, financialStatus,
                    fulfillmentStatus, isRefunded, refundAmount, includedInOssReport, ossReportId, rawData,
                    createdAt, updatedAt);
        }

        @Nonnull
        public OrderBuilder calculatedVatAmount(@Nullable BigDecimal calculatedVatAmount) {
            this.calculatedVatAmount = calculatedVatAmount;
            return this;
        }

        @Nonnull
        public OrderBuilder createdAt(@Nullable LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Nonnull
        public OrderBuilder currency(@Nullable String currency) {
            this.currency = currency;
            return this;
        }

        @Nonnull
        public OrderBuilder customerCountryCode(@Nullable String customerCountryCode) {
            this.customerCountryCode = customerCountryCode;
            return this;
        }

        @Nonnull
        public OrderBuilder customerCountryName(@Nullable String customerCountryName) {
            this.customerCountryName = customerCountryName;
            return this;
        }

        @Nonnull
        public OrderBuilder customerEmail(@Nullable String customerEmail) {
            this.customerEmail = customerEmail;
            return this;
        }

        @Nonnull
        public OrderBuilder customerVatNumber(@Nullable String customerVatNumber) {
            this.customerVatNumber = customerVatNumber;
            return this;
        }

        @Nonnull
        public OrderBuilder expectedVatRate(@Nullable BigDecimal expectedVatRate) {
            this.expectedVatRate = expectedVatRate;
            return this;
        }

        @Nonnull
        public OrderBuilder financialStatus(@Nullable FinancialStatus financialStatus) {
            this.financialStatus = financialStatus;
            return this;
        }

        @Nonnull
        public OrderBuilder fulfillmentStatus(@Nullable FulfillmentStatus fulfillmentStatus) {
            this.fulfillmentStatus = fulfillmentStatus;
            return this;
        }

        @Nonnull
        public OrderBuilder hasVatError(@Nullable Boolean hasVatError) {
            this.hasVatError = hasVatError;
            return this;
        }

        @Nonnull
        public OrderBuilder id(@Nullable Long id) {
            this.id = id;
            return this;
        }

        @Nonnull
        public OrderBuilder includedInOssReport(@Nullable Boolean includedInOssReport) {
            this.includedInOssReport = includedInOssReport;
            return this;
        }

        @Nonnull
        public OrderBuilder isB2b(@Nullable Boolean isB2b) {
            this.isB2b = isB2b;
            return this;
        }

        @Nonnull
        public OrderBuilder isRefunded(@Nullable Boolean isRefunded) {
            this.isRefunded = isRefunded;
            return this;
        }

        @Nonnull
        public OrderBuilder orderDate(@Nullable LocalDateTime orderDate) {
            this.orderDate = orderDate;
            return this;
        }

        @Nonnull
        public OrderBuilder orderNumber(@Nullable String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        @Nonnull
        public OrderBuilder ossReportId(@Nullable Long ossReportId) {
            this.ossReportId = ossReportId;
            return this;
        }

        @Nonnull
        public OrderBuilder rawData(@Nullable String rawData) {
            this.rawData = rawData;
            return this;
        }

        @Nonnull
        public OrderBuilder refundAmount(@Nullable BigDecimal refundAmount) {
            this.refundAmount = refundAmount;
            return this;
        }

        @Nonnull
        public OrderBuilder shippingAmount(@Nullable BigDecimal shippingAmount) {
            this.shippingAmount = shippingAmount;
            return this;
        }

        @Nonnull
        public OrderBuilder shop(@Nonnull Shop shop) {
            this.shop = shop;
            return this;
        }

        @Nonnull
        public OrderBuilder shopifyOrderId(@Nonnull String shopifyOrderId) {
            this.shopifyOrderId = shopifyOrderId;
            return this;
        }

        @Nonnull
        public OrderBuilder subtotalAmount(@Nullable BigDecimal subtotalAmount) {
            this.subtotalAmount = subtotalAmount;
            return this;
        }

        @Nonnull
        public OrderBuilder taxAmount(@Nullable BigDecimal taxAmount) {
            this.taxAmount = taxAmount;
            return this;
        }

        @Nonnull
        public OrderBuilder totalAmount(@Nullable BigDecimal totalAmount) {
            this.totalAmount = totalAmount;
            return this;
        }

        @Nonnull
        public OrderBuilder updatedAt(@Nullable LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        @Nonnull
        public OrderBuilder vatDifference(@Nullable BigDecimal vatDifference) {
            this.vatDifference = vatDifference;
            return this;
        }

        @Nonnull
        public OrderBuilder vatErrorType(@Nullable String vatErrorType) {
            this.vatErrorType = vatErrorType;
            return this;
        }

        @Nonnull
        public OrderBuilder vatExempt(@Nullable Boolean vatExempt) {
            this.vatExempt = vatExempt;
            return this;
        }

        @Nonnull
        public OrderBuilder vatExemptReason(@Nullable String vatExemptReason) {
            this.vatExemptReason = vatExemptReason;
            return this;
        }
    }
}
