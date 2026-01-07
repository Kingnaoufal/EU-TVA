package com.euvatease.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour représenter une commande Shopify.
 */
public class ShopifyOrderDto {

    private String id;
    private String orderNumber;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Montants
    private BigDecimal totalPrice;
    private BigDecimal subtotalPrice;
    private BigDecimal totalTax;
    private String currency;
    private String presentmentCurrency;
    
    // Statuts
    private String financialStatus;
    private String fulfillmentStatus;
    
    // Adresses
    private AddressDto billingAddress;
    private AddressDto shippingAddress;
    
    // Client
    private CustomerDto customer;
    
    // Lignes de taxes
    private List<TaxLineDto> taxLines;
    
    // Notes (peut contenir le numéro de TVA)
    private String note;
    private List<NoteAttributeDto> noteAttributes;
    
    // Lignes de commande
    private List<LineItemDto> lineItems;

    public ShopifyOrderDto() {
    }

    public ShopifyOrderDto(String id, String orderNumber, String email, LocalDateTime createdAt,
                           LocalDateTime updatedAt, BigDecimal totalPrice, BigDecimal subtotalPrice,
                           BigDecimal totalTax, String currency, String presentmentCurrency,
                           String financialStatus, String fulfillmentStatus, AddressDto billingAddress,
                           AddressDto shippingAddress, CustomerDto customer, List<TaxLineDto> taxLines,
                           String note, List<NoteAttributeDto> noteAttributes, List<LineItemDto> lineItems) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.email = email;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.totalPrice = totalPrice;
        this.subtotalPrice = subtotalPrice;
        this.totalTax = totalTax;
        this.currency = currency;
        this.presentmentCurrency = presentmentCurrency;
        this.financialStatus = financialStatus;
        this.fulfillmentStatus = fulfillmentStatus;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.customer = customer;
        this.taxLines = taxLines;
        this.note = note;
        this.noteAttributes = noteAttributes;
        this.lineItems = lineItems;
    }

    // Getters
    public String getId() { return id; }
    public String getOrderNumber() { return orderNumber; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public BigDecimal getSubtotalPrice() { return subtotalPrice; }
    public BigDecimal getTotalTax() { return totalTax; }
    public String getCurrency() { return currency; }
    public String getPresentmentCurrency() { return presentmentCurrency; }
    public String getFinancialStatus() { return financialStatus; }
    public String getFulfillmentStatus() { return fulfillmentStatus; }
    public AddressDto getBillingAddress() { return billingAddress; }
    public AddressDto getShippingAddress() { return shippingAddress; }
    public CustomerDto getCustomer() { return customer; }
    public List<TaxLineDto> getTaxLines() { return taxLines; }
    public String getNote() { return note; }
    public List<NoteAttributeDto> getNoteAttributes() { return noteAttributes; }
    public List<LineItemDto> getLineItems() { return lineItems; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public void setSubtotalPrice(BigDecimal subtotalPrice) { this.subtotalPrice = subtotalPrice; }
    public void setTotalTax(BigDecimal totalTax) { this.totalTax = totalTax; }
    public void setCurrency(String currency) { this.currency = currency; }
    public void setPresentmentCurrency(String presentmentCurrency) { this.presentmentCurrency = presentmentCurrency; }
    public void setFinancialStatus(String financialStatus) { this.financialStatus = financialStatus; }
    public void setFulfillmentStatus(String fulfillmentStatus) { this.fulfillmentStatus = fulfillmentStatus; }
    public void setBillingAddress(AddressDto billingAddress) { this.billingAddress = billingAddress; }
    public void setShippingAddress(AddressDto shippingAddress) { this.shippingAddress = shippingAddress; }
    public void setCustomer(CustomerDto customer) { this.customer = customer; }
    public void setTaxLines(List<TaxLineDto> taxLines) { this.taxLines = taxLines; }
    public void setNote(String note) { this.note = note; }
    public void setNoteAttributes(List<NoteAttributeDto> noteAttributes) { this.noteAttributes = noteAttributes; }
    public void setLineItems(List<LineItemDto> lineItems) { this.lineItems = lineItems; }

    // Builder
    public static ShopifyOrderDtoBuilder builder() { return new ShopifyOrderDtoBuilder(); }

    public static class ShopifyOrderDtoBuilder {
        private String id;
        private String orderNumber;
        private String email;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private BigDecimal totalPrice;
        private BigDecimal subtotalPrice;
        private BigDecimal totalTax;
        private String currency;
        private String presentmentCurrency;
        private String financialStatus;
        private String fulfillmentStatus;
        private AddressDto billingAddress;
        private AddressDto shippingAddress;
        private CustomerDto customer;
        private List<TaxLineDto> taxLines;
        private String note;
        private List<NoteAttributeDto> noteAttributes;
        private List<LineItemDto> lineItems;

        public ShopifyOrderDtoBuilder id(String id) { this.id = id; return this; }
        public ShopifyOrderDtoBuilder orderNumber(String orderNumber) { this.orderNumber = orderNumber; return this; }
        public ShopifyOrderDtoBuilder email(String email) { this.email = email; return this; }
        public ShopifyOrderDtoBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public ShopifyOrderDtoBuilder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public ShopifyOrderDtoBuilder totalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; return this; }
        public ShopifyOrderDtoBuilder subtotalPrice(BigDecimal subtotalPrice) { this.subtotalPrice = subtotalPrice; return this; }
        public ShopifyOrderDtoBuilder totalTax(BigDecimal totalTax) { this.totalTax = totalTax; return this; }
        public ShopifyOrderDtoBuilder currency(String currency) { this.currency = currency; return this; }
        public ShopifyOrderDtoBuilder presentmentCurrency(String presentmentCurrency) { this.presentmentCurrency = presentmentCurrency; return this; }
        public ShopifyOrderDtoBuilder financialStatus(String financialStatus) { this.financialStatus = financialStatus; return this; }
        public ShopifyOrderDtoBuilder fulfillmentStatus(String fulfillmentStatus) { this.fulfillmentStatus = fulfillmentStatus; return this; }
        public ShopifyOrderDtoBuilder billingAddress(AddressDto billingAddress) { this.billingAddress = billingAddress; return this; }
        public ShopifyOrderDtoBuilder shippingAddress(AddressDto shippingAddress) { this.shippingAddress = shippingAddress; return this; }
        public ShopifyOrderDtoBuilder customer(CustomerDto customer) { this.customer = customer; return this; }
        public ShopifyOrderDtoBuilder taxLines(List<TaxLineDto> taxLines) { this.taxLines = taxLines; return this; }
        public ShopifyOrderDtoBuilder note(String note) { this.note = note; return this; }
        public ShopifyOrderDtoBuilder noteAttributes(List<NoteAttributeDto> noteAttributes) { this.noteAttributes = noteAttributes; return this; }
        public ShopifyOrderDtoBuilder lineItems(List<LineItemDto> lineItems) { this.lineItems = lineItems; return this; }

        public ShopifyOrderDto build() {
            return new ShopifyOrderDto(id, orderNumber, email, createdAt, updatedAt, totalPrice, subtotalPrice,
                    totalTax, currency, presentmentCurrency, financialStatus, fulfillmentStatus, billingAddress,
                    shippingAddress, customer, taxLines, note, noteAttributes, lineItems);
        }
    }

    // Nested classes
    public static class AddressDto {
        private String firstName;
        private String lastName;
        private String company;
        private String address1;
        private String address2;
        private String city;
        private String province;
        private String provinceCode;
        private String country;
        private String countryCode;
        private String zip;
        private String phone;

        public AddressDto() {}

        public AddressDto(String firstName, String lastName, String company, String address1, String address2,
                          String city, String province, String provinceCode, String country, String countryCode,
                          String zip, String phone) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.company = company;
            this.address1 = address1;
            this.address2 = address2;
            this.city = city;
            this.province = province;
            this.provinceCode = provinceCode;
            this.country = country;
            this.countryCode = countryCode;
            this.zip = zip;
            this.phone = phone;
        }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getCompany() { return company; }
        public void setCompany(String company) { this.company = company; }
        public String getAddress1() { return address1; }
        public void setAddress1(String address1) { this.address1 = address1; }
        public String getAddress2() { return address2; }
        public void setAddress2(String address2) { this.address2 = address2; }
        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }
        public String getProvince() { return province; }
        public void setProvince(String province) { this.province = province; }
        public String getProvinceCode() { return provinceCode; }
        public void setProvinceCode(String provinceCode) { this.provinceCode = provinceCode; }
        public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
        public String getCountryCode() { return countryCode; }
        public void setCountryCode(String countryCode) { this.countryCode = countryCode; }
        public String getZip() { return zip; }
        public void setZip(String zip) { this.zip = zip; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public static AddressDtoBuilder builder() { return new AddressDtoBuilder(); }

        public static class AddressDtoBuilder {
            private String firstName;
            private String lastName;
            private String company;
            private String address1;
            private String address2;
            private String city;
            private String province;
            private String provinceCode;
            private String country;
            private String countryCode;
            private String zip;
            private String phone;

            public AddressDtoBuilder firstName(String firstName) { this.firstName = firstName; return this; }
            public AddressDtoBuilder lastName(String lastName) { this.lastName = lastName; return this; }
            public AddressDtoBuilder company(String company) { this.company = company; return this; }
            public AddressDtoBuilder address1(String address1) { this.address1 = address1; return this; }
            public AddressDtoBuilder address2(String address2) { this.address2 = address2; return this; }
            public AddressDtoBuilder city(String city) { this.city = city; return this; }
            public AddressDtoBuilder province(String province) { this.province = province; return this; }
            public AddressDtoBuilder provinceCode(String provinceCode) { this.provinceCode = provinceCode; return this; }
            public AddressDtoBuilder country(String country) { this.country = country; return this; }
            public AddressDtoBuilder countryCode(String countryCode) { this.countryCode = countryCode; return this; }
            public AddressDtoBuilder zip(String zip) { this.zip = zip; return this; }
            public AddressDtoBuilder phone(String phone) { this.phone = phone; return this; }

            public AddressDto build() {
                return new AddressDto(firstName, lastName, company, address1, address2, city, province,
                        provinceCode, country, countryCode, zip, phone);
            }
        }
    }

    public static class CustomerDto {
        private String id;
        private String email;
        private String firstName;
        private String lastName;
        private String phone;
        private Boolean taxExempt;
        private List<String> taxExemptions;

        public CustomerDto() {}

        public CustomerDto(String id, String email, String firstName, String lastName, String phone,
                           Boolean taxExempt, List<String> taxExemptions) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.taxExempt = taxExempt;
            this.taxExemptions = taxExemptions;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }
        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public Boolean getTaxExempt() { return taxExempt; }
        public void setTaxExempt(Boolean taxExempt) { this.taxExempt = taxExempt; }
        public List<String> getTaxExemptions() { return taxExemptions; }
        public void setTaxExemptions(List<String> taxExemptions) { this.taxExemptions = taxExemptions; }

        public static CustomerDtoBuilder builder() { return new CustomerDtoBuilder(); }

        public static class CustomerDtoBuilder {
            private String id;
            private String email;
            private String firstName;
            private String lastName;
            private String phone;
            private Boolean taxExempt;
            private List<String> taxExemptions;

            public CustomerDtoBuilder id(String id) { this.id = id; return this; }
            public CustomerDtoBuilder email(String email) { this.email = email; return this; }
            public CustomerDtoBuilder firstName(String firstName) { this.firstName = firstName; return this; }
            public CustomerDtoBuilder lastName(String lastName) { this.lastName = lastName; return this; }
            public CustomerDtoBuilder phone(String phone) { this.phone = phone; return this; }
            public CustomerDtoBuilder taxExempt(Boolean taxExempt) { this.taxExempt = taxExempt; return this; }
            public CustomerDtoBuilder taxExemptions(List<String> taxExemptions) { this.taxExemptions = taxExemptions; return this; }

            public CustomerDto build() {
                return new CustomerDto(id, email, firstName, lastName, phone, taxExempt, taxExemptions);
            }
        }
    }

    public static class TaxLineDto {
        private String title;
        private BigDecimal price;
        private BigDecimal rate;
        private Boolean channelLiable;

        public TaxLineDto() {}

        public TaxLineDto(String title, BigDecimal price, BigDecimal rate, Boolean channelLiable) {
            this.title = title;
            this.price = price;
            this.rate = rate;
            this.channelLiable = channelLiable;
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public BigDecimal getRate() { return rate; }
        public void setRate(BigDecimal rate) { this.rate = rate; }
        public Boolean getChannelLiable() { return channelLiable; }
        public void setChannelLiable(Boolean channelLiable) { this.channelLiable = channelLiable; }

        public static TaxLineDtoBuilder builder() { return new TaxLineDtoBuilder(); }

        public static class TaxLineDtoBuilder {
            private String title;
            private BigDecimal price;
            private BigDecimal rate;
            private Boolean channelLiable;

            public TaxLineDtoBuilder title(String title) { this.title = title; return this; }
            public TaxLineDtoBuilder price(BigDecimal price) { this.price = price; return this; }
            public TaxLineDtoBuilder rate(BigDecimal rate) { this.rate = rate; return this; }
            public TaxLineDtoBuilder channelLiable(Boolean channelLiable) { this.channelLiable = channelLiable; return this; }

            public TaxLineDto build() {
                return new TaxLineDto(title, price, rate, channelLiable);
            }
        }
    }

    public static class NoteAttributeDto {
        private String name;
        private String value;

        public NoteAttributeDto() {}

        public NoteAttributeDto(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        public static NoteAttributeDtoBuilder builder() { return new NoteAttributeDtoBuilder(); }

        public static class NoteAttributeDtoBuilder {
            private String name;
            private String value;

            public NoteAttributeDtoBuilder name(String name) { this.name = name; return this; }
            public NoteAttributeDtoBuilder value(String value) { this.value = value; return this; }

            public NoteAttributeDto build() {
                return new NoteAttributeDto(name, value);
            }
        }
    }

    public static class LineItemDto {
        private String id;
        private String title;
        private Integer quantity;
        private BigDecimal price;
        private String sku;
        private Boolean taxable;
        private List<TaxLineDto> taxLines;

        public LineItemDto() {}

        public LineItemDto(String id, String title, Integer quantity, BigDecimal price, String sku,
                           Boolean taxable, List<TaxLineDto> taxLines) {
            this.id = id;
            this.title = title;
            this.quantity = quantity;
            this.price = price;
            this.sku = sku;
            this.taxable = taxable;
            this.taxLines = taxLines;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public Boolean getTaxable() { return taxable; }
        public void setTaxable(Boolean taxable) { this.taxable = taxable; }
        public List<TaxLineDto> getTaxLines() { return taxLines; }
        public void setTaxLines(List<TaxLineDto> taxLines) { this.taxLines = taxLines; }

        public static LineItemDtoBuilder builder() { return new LineItemDtoBuilder(); }

        public static class LineItemDtoBuilder {
            private String id;
            private String title;
            private Integer quantity;
            private BigDecimal price;
            private String sku;
            private Boolean taxable;
            private List<TaxLineDto> taxLines;

            public LineItemDtoBuilder id(String id) { this.id = id; return this; }
            public LineItemDtoBuilder title(String title) { this.title = title; return this; }
            public LineItemDtoBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
            public LineItemDtoBuilder price(BigDecimal price) { this.price = price; return this; }
            public LineItemDtoBuilder sku(String sku) { this.sku = sku; return this; }
            public LineItemDtoBuilder taxable(Boolean taxable) { this.taxable = taxable; return this; }
            public LineItemDtoBuilder taxLines(List<TaxLineDto> taxLines) { this.taxLines = taxLines; return this; }

            public LineItemDto build() {
                return new LineItemDto(id, title, quantity, price, sku, taxable, taxLines);
            }
        }
    }
}
