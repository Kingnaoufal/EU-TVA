package com.euvatease.dto;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour représenter une commande Shopify.
 */
public class ShopifyOrderDto {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nullable
    private String id;

    @Nullable
    private String orderNumber;

    @Nullable
    private String email;

    @Nullable
    private LocalDateTime createdAt;

    @Nullable
    private LocalDateTime updatedAt;

    @Nullable
    private BigDecimal totalPrice;

    @Nullable
    private BigDecimal subtotalPrice;

    @Nullable
    private BigDecimal totalTax;

    @Nullable
    private String currency;

    @Nullable
    private String presentmentCurrency;

    @Nullable
    private String financialStatus;

    @Nullable
    private String fulfillmentStatus;

    @Nullable
    private AddressDto billingAddress;

    @Nullable
    private AddressDto shippingAddress;

    @Nullable
    private CustomerDto customer;

    @Nullable
    private List<TaxLineDto> taxLines;

    @Nullable
    private String note;

    @Nullable
    private List<NoteAttributeDto> noteAttributes;

    @Nullable
    private List<LineItemDto> lineItems;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public ShopifyOrderDto() {
    }

    public ShopifyOrderDto(@Nullable String id,
                           @Nullable String orderNumber,
                           @Nullable String email,
                           @Nullable LocalDateTime createdAt,
                           @Nullable LocalDateTime updatedAt,
                           @Nullable BigDecimal totalPrice,
                           @Nullable BigDecimal subtotalPrice,
                           @Nullable BigDecimal totalTax,
                           @Nullable String currency,
                           @Nullable String presentmentCurrency,
                           @Nullable String financialStatus,
                           @Nullable String fulfillmentStatus,
                           @Nullable AddressDto billingAddress,
                           @Nullable AddressDto shippingAddress,
                           @Nullable CustomerDto customer,
                           @Nullable List<TaxLineDto> taxLines,
                           @Nullable String note,
                           @Nullable List<NoteAttributeDto> noteAttributes,
                           @Nullable List<LineItemDto> lineItems) {
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

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    public static ShopifyOrderDtoBuilder builder() {
        return new ShopifyOrderDtoBuilder();
    }

    @Nullable
    public String getId() {
        return id;
    }

    public void setId(@Nullable String id) {
        this.id = id;
    }

    @Nullable
    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(@Nullable String orderNumber) {
        this.orderNumber = orderNumber;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
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

    @Nullable
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(@Nullable BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Nullable
    public BigDecimal getSubtotalPrice() {
        return subtotalPrice;
    }

    public void setSubtotalPrice(@Nullable BigDecimal subtotalPrice) {
        this.subtotalPrice = subtotalPrice;
    }

    @Nullable
    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(@Nullable BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    @Nullable
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(@Nullable String currency) {
        this.currency = currency;
    }

    @Nullable
    public String getPresentmentCurrency() {
        return presentmentCurrency;
    }

    public void setPresentmentCurrency(@Nullable String presentmentCurrency) {
        this.presentmentCurrency = presentmentCurrency;
    }

    @Nullable
    public String getFinancialStatus() {
        return financialStatus;
    }

    public void setFinancialStatus(@Nullable String financialStatus) {
        this.financialStatus = financialStatus;
    }

    @Nullable
    public String getFulfillmentStatus() {
        return fulfillmentStatus;
    }

    public void setFulfillmentStatus(@Nullable String fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    @Nullable
    public AddressDto getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(@Nullable AddressDto billingAddress) {
        this.billingAddress = billingAddress;
    }

    @Nullable
    public AddressDto getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(@Nullable AddressDto shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    @Nullable
    public CustomerDto getCustomer() {
        return customer;
    }

    public void setCustomer(@Nullable CustomerDto customer) {
        this.customer = customer;
    }

    @Nullable
    public List<TaxLineDto> getTaxLines() {
        return taxLines;
    }

    public void setTaxLines(@Nullable List<TaxLineDto> taxLines) {
        this.taxLines = taxLines;
    }

    @Nullable
    public String getNote() {
        return note;
    }

    public void setNote(@Nullable String note) {
        this.note = note;
    }

    @Nullable
    public List<NoteAttributeDto> getNoteAttributes() {
        return noteAttributes;
    }

    public void setNoteAttributes(@Nullable List<NoteAttributeDto> noteAttributes) {
        this.noteAttributes = noteAttributes;
    }

    @Nullable
    public List<LineItemDto> getLineItems() {
        return lineItems;
    }

    public void setLineItems(@Nullable List<LineItemDto> lineItems) {
        this.lineItems = lineItems;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Nested Classes
    //~ ----------------------------------------------------------------------------------------------------------------

    public static class ShopifyOrderDtoBuilder {

        @Nullable
        private String id;

        @Nullable
        private String orderNumber;

        @Nullable
        private String email;

        @Nullable
        private LocalDateTime createdAt;

        @Nullable
        private LocalDateTime updatedAt;

        @Nullable
        private BigDecimal totalPrice;

        @Nullable
        private BigDecimal subtotalPrice;

        @Nullable
        private BigDecimal totalTax;

        @Nullable
        private String currency;

        @Nullable
        private String presentmentCurrency;

        @Nullable
        private String financialStatus;

        @Nullable
        private String fulfillmentStatus;

        @Nullable
        private AddressDto billingAddress;

        @Nullable
        private AddressDto shippingAddress;

        @Nullable
        private CustomerDto customer;

        @Nullable
        private List<TaxLineDto> taxLines;

        @Nullable
        private String note;

        @Nullable
        private List<NoteAttributeDto> noteAttributes;

        @Nullable
        private List<LineItemDto> lineItems;

        @Nonnull
        public ShopifyOrderDtoBuilder id(@Nullable String id) {
            this.id = id;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder orderNumber(@Nullable String orderNumber) {
            this.orderNumber = orderNumber;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder email(@Nullable String email) {
            this.email = email;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder createdAt(@Nullable LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder updatedAt(@Nullable LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder totalPrice(@Nullable BigDecimal totalPrice) {
            this.totalPrice = totalPrice;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder subtotalPrice(@Nullable BigDecimal subtotalPrice) {
            this.subtotalPrice = subtotalPrice;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder totalTax(@Nullable BigDecimal totalTax) {
            this.totalTax = totalTax;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder currency(@Nullable String currency) {
            this.currency = currency;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder presentmentCurrency(@Nullable String presentmentCurrency) {
            this.presentmentCurrency = presentmentCurrency;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder financialStatus(@Nullable String financialStatus) {
            this.financialStatus = financialStatus;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder fulfillmentStatus(@Nullable String fulfillmentStatus) {
            this.fulfillmentStatus = fulfillmentStatus;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder billingAddress(@Nullable AddressDto billingAddress) {
            this.billingAddress = billingAddress;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder shippingAddress(@Nullable AddressDto shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder customer(@Nullable CustomerDto customer) {
            this.customer = customer;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder taxLines(@Nullable List<TaxLineDto> taxLines) {
            this.taxLines = taxLines;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder note(@Nullable String note) {
            this.note = note;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder noteAttributes(@Nullable List<NoteAttributeDto> noteAttributes) {
            this.noteAttributes = noteAttributes;
            return this;
        }

        @Nonnull
        public ShopifyOrderDtoBuilder lineItems(@Nullable List<LineItemDto> lineItems) {
            this.lineItems = lineItems;
            return this;
        }

        @Nonnull
        public ShopifyOrderDto build() {
            return new ShopifyOrderDto(id, orderNumber, email, createdAt, updatedAt, totalPrice, subtotalPrice,
                    totalTax, currency, presentmentCurrency, financialStatus, fulfillmentStatus, billingAddress,
                    shippingAddress, customer, taxLines, note, noteAttributes, lineItems);
        }
    }

    public static class AddressDto {

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Instance fields
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nullable
        private String firstName;

        @Nullable
        private String lastName;

        @Nullable
        private String company;

        @Nullable
        private String address1;

        @Nullable
        private String address2;

        @Nullable
        private String city;

        @Nullable
        private String province;

        @Nullable
        private String provinceCode;

        @Nullable
        private String country;

        @Nullable
        private String countryCode;

        @Nullable
        private String zip;

        @Nullable
        private String phone;

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Constructors
        //~ ----------------------------------------------------------------------------------------------------------------

        public AddressDto() {
        }

        public AddressDto(@Nullable String firstName,
                          @Nullable String lastName,
                          @Nullable String company,
                          @Nullable String address1,
                          @Nullable String address2,
                          @Nullable String city,
                          @Nullable String province,
                          @Nullable String provinceCode,
                          @Nullable String country,
                          @Nullable String countryCode,
                          @Nullable String zip,
                          @Nullable String phone) {
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

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Methods
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nonnull
        public static AddressDtoBuilder builder() {
            return new AddressDtoBuilder();
        }

        @Nullable
        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(@Nullable String firstName) {
            this.firstName = firstName;
        }

        @Nullable
        public String getLastName() {
            return lastName;
        }

        public void setLastName(@Nullable String lastName) {
            this.lastName = lastName;
        }

        @Nullable
        public String getCompany() {
            return company;
        }

        public void setCompany(@Nullable String company) {
            this.company = company;
        }

        @Nullable
        public String getAddress1() {
            return address1;
        }

        public void setAddress1(@Nullable String address1) {
            this.address1 = address1;
        }

        @Nullable
        public String getAddress2() {
            return address2;
        }

        public void setAddress2(@Nullable String address2) {
            this.address2 = address2;
        }

        @Nullable
        public String getCity() {
            return city;
        }

        public void setCity(@Nullable String city) {
            this.city = city;
        }

        @Nullable
        public String getProvince() {
            return province;
        }

        public void setProvince(@Nullable String province) {
            this.province = province;
        }

        @Nullable
        public String getProvinceCode() {
            return provinceCode;
        }

        public void setProvinceCode(@Nullable String provinceCode) {
            this.provinceCode = provinceCode;
        }

        @Nullable
        public String getCountry() {
            return country;
        }

        public void setCountry(@Nullable String country) {
            this.country = country;
        }

        @Nullable
        public String getCountryCode() {
            return countryCode;
        }

        public void setCountryCode(@Nullable String countryCode) {
            this.countryCode = countryCode;
        }

        @Nullable
        public String getZip() {
            return zip;
        }

        public void setZip(@Nullable String zip) {
            this.zip = zip;
        }

        @Nullable
        public String getPhone() {
            return phone;
        }

        public void setPhone(@Nullable String phone) {
            this.phone = phone;
        }

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Nested Classes
        //~ ----------------------------------------------------------------------------------------------------------------

        public static class AddressDtoBuilder {

            @Nullable
            private String firstName;

            @Nullable
            private String lastName;

            @Nullable
            private String company;

            @Nullable
            private String address1;

            @Nullable
            private String address2;

            @Nullable
            private String city;

            @Nullable
            private String province;

            @Nullable
            private String provinceCode;

            @Nullable
            private String country;

            @Nullable
            private String countryCode;

            @Nullable
            private String zip;

            @Nullable
            private String phone;

            @Nonnull
            public AddressDtoBuilder firstName(@Nullable String firstName) {
                this.firstName = firstName;
                return this;
            }

            @Nonnull
            public AddressDtoBuilder lastName(@Nullable String lastName) {
                this.lastName = lastName;
                return this;
            }

            @Nonnull
            public AddressDtoBuilder company(@Nullable String company) {
                this.company = company;
                return this;
            }

            @Nonnull
            public AddressDtoBuilder address1(@Nullable String address1) {
                this.address1 = address1;
                return this;
            }

            @Nonnull
            public AddressDtoBuilder address2(@Nullable String address2) {
                this.address2 = address2;
                return this;
            }

            @Nonnull
            public AddressDtoBuilder city(@Nullable String city) {
                this.city = city;
                return this;
            }

            @Nonnull
            public AddressDtoBuilder province(@Nullable String province) {
                this.province = province;
                return this;
            }

            @Nonnull
            public AddressDtoBuilder provinceCode(@Nullable String provinceCode) {
                this.provinceCode = provinceCode;
                return this;
            }

            @Nonnull
            public AddressDtoBuilder country(@Nullable String country) {
                this.country = country;
                return this;
            }

            @Nonnull
            public AddressDtoBuilder countryCode(@Nullable String countryCode) {
                this.countryCode = countryCode;
                return this;
            }

            @Nonnull
            public AddressDtoBuilder zip(@Nullable String zip) {
                this.zip = zip;
                return this;
            }

            @Nonnull
            public AddressDtoBuilder phone(@Nullable String phone) {
                this.phone = phone;
                return this;
            }

            @Nonnull
            public AddressDto build() {
                return new AddressDto(firstName, lastName, company, address1, address2, city, province,
                        provinceCode, country, countryCode, zip, phone);
            }
        }
    }

    public static class CustomerDto {

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Instance fields
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nullable
        private String id;

        @Nullable
        private String email;

        @Nullable
        private String firstName;

        @Nullable
        private String lastName;

        @Nullable
        private String phone;

        @Nullable
        private Boolean taxExempt;

        @Nullable
        private List<String> taxExemptions;

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Constructors
        //~ ----------------------------------------------------------------------------------------------------------------

        public CustomerDto() {
        }

        public CustomerDto(@Nullable String id,
                           @Nullable String email,
                           @Nullable String firstName,
                           @Nullable String lastName,
                           @Nullable String phone,
                           @Nullable Boolean taxExempt,
                           @Nullable List<String> taxExemptions) {
            this.id = id;
            this.email = email;
            this.firstName = firstName;
            this.lastName = lastName;
            this.phone = phone;
            this.taxExempt = taxExempt;
            this.taxExemptions = taxExemptions;
        }

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Methods
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nonnull
        public static CustomerDtoBuilder builder() {
            return new CustomerDtoBuilder();
        }

        @Nullable
        public String getId() {
            return id;
        }

        public void setId(@Nullable String id) {
            this.id = id;
        }

        @Nullable
        public String getEmail() {
            return email;
        }

        public void setEmail(@Nullable String email) {
            this.email = email;
        }

        @Nullable
        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(@Nullable String firstName) {
            this.firstName = firstName;
        }

        @Nullable
        public String getLastName() {
            return lastName;
        }

        public void setLastName(@Nullable String lastName) {
            this.lastName = lastName;
        }

        @Nullable
        public String getPhone() {
            return phone;
        }

        public void setPhone(@Nullable String phone) {
            this.phone = phone;
        }

        @Nullable
        public Boolean getTaxExempt() {
            return taxExempt;
        }

        public void setTaxExempt(@Nullable Boolean taxExempt) {
            this.taxExempt = taxExempt;
        }

        @Nullable
        public List<String> getTaxExemptions() {
            return taxExemptions;
        }

        public void setTaxExemptions(@Nullable List<String> taxExemptions) {
            this.taxExemptions = taxExemptions;
        }

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Nested Classes
        //~ ----------------------------------------------------------------------------------------------------------------

        public static class CustomerDtoBuilder {

            @Nullable
            private String id;

            @Nullable
            private String email;

            @Nullable
            private String firstName;

            @Nullable
            private String lastName;

            @Nullable
            private String phone;

            @Nullable
            private Boolean taxExempt;

            @Nullable
            private List<String> taxExemptions;

            @Nonnull
            public CustomerDtoBuilder id(@Nullable String id) {
                this.id = id;
                return this;
            }

            @Nonnull
            public CustomerDtoBuilder email(@Nullable String email) {
                this.email = email;
                return this;
            }

            @Nonnull
            public CustomerDtoBuilder firstName(@Nullable String firstName) {
                this.firstName = firstName;
                return this;
            }

            @Nonnull
            public CustomerDtoBuilder lastName(@Nullable String lastName) {
                this.lastName = lastName;
                return this;
            }

            @Nonnull
            public CustomerDtoBuilder phone(@Nullable String phone) {
                this.phone = phone;
                return this;
            }

            @Nonnull
            public CustomerDtoBuilder taxExempt(@Nullable Boolean taxExempt) {
                this.taxExempt = taxExempt;
                return this;
            }

            @Nonnull
            public CustomerDtoBuilder taxExemptions(@Nullable List<String> taxExemptions) {
                this.taxExemptions = taxExemptions;
                return this;
            }

            @Nonnull
            public CustomerDto build() {
                return new CustomerDto(id, email, firstName, lastName, phone, taxExempt, taxExemptions);
            }
        }
    }

    public static class TaxLineDto {

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Instance fields
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nullable
        private String title;

        @Nullable
        private BigDecimal price;

        @Nullable
        private BigDecimal rate;

        @Nullable
        private Boolean channelLiable;

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Constructors
        //~ ----------------------------------------------------------------------------------------------------------------

        public TaxLineDto() {
        }

        public TaxLineDto(@Nullable String title,
                          @Nullable BigDecimal price,
                          @Nullable BigDecimal rate,
                          @Nullable Boolean channelLiable) {
            this.title = title;
            this.price = price;
            this.rate = rate;
            this.channelLiable = channelLiable;
        }

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Methods
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nonnull
        public static TaxLineDtoBuilder builder() {
            return new TaxLineDtoBuilder();
        }

        @Nullable
        public String getTitle() {
            return title;
        }

        public void setTitle(@Nullable String title) {
            this.title = title;
        }

        @Nullable
        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(@Nullable BigDecimal price) {
            this.price = price;
        }

        @Nullable
        public BigDecimal getRate() {
            return rate;
        }

        public void setRate(@Nullable BigDecimal rate) {
            this.rate = rate;
        }

        @Nullable
        public Boolean getChannelLiable() {
            return channelLiable;
        }

        public void setChannelLiable(@Nullable Boolean channelLiable) {
            this.channelLiable = channelLiable;
        }

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Nested Classes
        //~ ----------------------------------------------------------------------------------------------------------------

        public static class TaxLineDtoBuilder {

            @Nullable
            private String title;

            @Nullable
            private BigDecimal price;

            @Nullable
            private BigDecimal rate;

            @Nullable
            private Boolean channelLiable;

            @Nonnull
            public TaxLineDtoBuilder title(@Nullable String title) {
                this.title = title;
                return this;
            }

            @Nonnull
            public TaxLineDtoBuilder price(@Nullable BigDecimal price) {
                this.price = price;
                return this;
            }

            @Nonnull
            public TaxLineDtoBuilder rate(@Nullable BigDecimal rate) {
                this.rate = rate;
                return this;
            }

            @Nonnull
            public TaxLineDtoBuilder channelLiable(@Nullable Boolean channelLiable) {
                this.channelLiable = channelLiable;
                return this;
            }

            @Nonnull
            public TaxLineDto build() {
                return new TaxLineDto(title, price, rate, channelLiable);
            }
        }
    }

    public static class NoteAttributeDto {

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Instance fields
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nullable
        private String name;

        @Nullable
        private String value;

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Constructors
        //~ ----------------------------------------------------------------------------------------------------------------

        public NoteAttributeDto() {
        }

        public NoteAttributeDto(@Nullable String name,
                                @Nullable String value) {
            this.name = name;
            this.value = value;
        }

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Methods
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nonnull
        public static NoteAttributeDtoBuilder builder() {
            return new NoteAttributeDtoBuilder();
        }

        @Nullable
        public String getName() {
            return name;
        }

        public void setName(@Nullable String name) {
            this.name = name;
        }

        @Nullable
        public String getValue() {
            return value;
        }

        public void setValue(@Nullable String value) {
            this.value = value;
        }

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Nested Classes
        //~ ----------------------------------------------------------------------------------------------------------------

        public static class NoteAttributeDtoBuilder {

            @Nullable
            private String name;

            @Nullable
            private String value;

            @Nonnull
            public NoteAttributeDtoBuilder name(@Nullable String name) {
                this.name = name;
                return this;
            }

            @Nonnull
            public NoteAttributeDtoBuilder value(@Nullable String value) {
                this.value = value;
                return this;
            }

            @Nonnull
            public NoteAttributeDto build() {
                return new NoteAttributeDto(name, value);
            }
        }
    }

    public static class LineItemDto {

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Instance fields
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nullable
        private String id;

        @Nullable
        private String title;

        @Nullable
        private Integer quantity;

        @Nullable
        private BigDecimal price;

        @Nullable
        private String sku;

        @Nullable
        private Boolean taxable;

        @Nullable
        private List<TaxLineDto> taxLines;

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Constructors
        //~ ----------------------------------------------------------------------------------------------------------------

        public LineItemDto() {
        }

        public LineItemDto(@Nullable String id,
                           @Nullable String title,
                           @Nullable Integer quantity,
                           @Nullable BigDecimal price,
                           @Nullable String sku,
                           @Nullable Boolean taxable,
                           @Nullable List<TaxLineDto> taxLines) {
            this.id = id;
            this.title = title;
            this.quantity = quantity;
            this.price = price;
            this.sku = sku;
            this.taxable = taxable;
            this.taxLines = taxLines;
        }

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Methods
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nonnull
        public static LineItemDtoBuilder builder() {
            return new LineItemDtoBuilder();
        }

        @Nullable
        public String getId() {
            return id;
        }

        public void setId(@Nullable String id) {
            this.id = id;
        }

        @Nullable
        public String getTitle() {
            return title;
        }

        public void setTitle(@Nullable String title) {
            this.title = title;
        }

        @Nullable
        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(@Nullable Integer quantity) {
            this.quantity = quantity;
        }

        @Nullable
        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(@Nullable BigDecimal price) {
            this.price = price;
        }

        @Nullable
        public String getSku() {
            return sku;
        }

        public void setSku(@Nullable String sku) {
            this.sku = sku;
        }

        @Nullable
        public Boolean getTaxable() {
            return taxable;
        }

        public void setTaxable(@Nullable Boolean taxable) {
            this.taxable = taxable;
        }

        @Nullable
        public List<TaxLineDto> getTaxLines() {
            return taxLines;
        }

        public void setTaxLines(@Nullable List<TaxLineDto> taxLines) {
            this.taxLines = taxLines;
        }

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Nested Classes
        //~ ----------------------------------------------------------------------------------------------------------------

        public static class LineItemDtoBuilder {

            @Nullable
            private String id;

            @Nullable
            private String title;

            @Nullable
            private Integer quantity;

            @Nullable
            private BigDecimal price;

            @Nullable
            private String sku;

            @Nullable
            private Boolean taxable;

            @Nullable
            private List<TaxLineDto> taxLines;

            @Nonnull
            public LineItemDtoBuilder id(@Nullable String id) {
                this.id = id;
                return this;
            }

            @Nonnull
            public LineItemDtoBuilder title(@Nullable String title) {
                this.title = title;
                return this;
            }

            @Nonnull
            public LineItemDtoBuilder quantity(@Nullable Integer quantity) {
                this.quantity = quantity;
                return this;
            }

            @Nonnull
            public LineItemDtoBuilder price(@Nullable BigDecimal price) {
                this.price = price;
                return this;
            }

            @Nonnull
            public LineItemDtoBuilder sku(@Nullable String sku) {
                this.sku = sku;
                return this;
            }

            @Nonnull
            public LineItemDtoBuilder taxable(@Nullable Boolean taxable) {
                this.taxable = taxable;
                return this;
            }

            @Nonnull
            public LineItemDtoBuilder taxLines(@Nullable List<TaxLineDto> taxLines) {
                this.taxLines = taxLines;
                return this;
            }

            @Nonnull
            public LineItemDto build() {
                return new LineItemDto(id, title, quantity, price, sku, taxable, taxLines);
            }
        }
    }
}
