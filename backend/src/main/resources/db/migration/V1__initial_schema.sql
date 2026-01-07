-- =====================================================
-- EU VAT Ease - Database Schema
-- Version: 1.0.0
-- Description: Initial schema for Shopify EU VAT OSS Plugin
-- =====================================================

-- Extension for UUID generation
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- =====================================================
-- TABLE: shops
-- Stores Shopify shop information and subscription data
-- =====================================================
CREATE TABLE shops (
    id BIGSERIAL PRIMARY KEY,
    shopify_domain VARCHAR(255) NOT NULL UNIQUE,
    shopify_shop_id VARCHAR(100) UNIQUE,
    shop_name VARCHAR(255),
    email VARCHAR(255),
    country_code VARCHAR(2),
    currency VARCHAR(3),
    access_token VARCHAR(512),
    vat_number VARCHAR(50),
    oss_registered BOOLEAN DEFAULT FALSE,
    oss_country_code VARCHAR(2),
    subscription_status VARCHAR(20) DEFAULT 'TRIAL',
    subscription_plan VARCHAR(50),
    shopify_charge_id VARCHAR(100),
    trial_ends_at TIMESTAMP,
    subscription_ends_at TIMESTAMP,
    installed_at TIMESTAMP,
    uninstalled_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    alert_email_enabled BOOLEAN DEFAULT TRUE,
    oss_threshold_alert_sent BOOLEAN DEFAULT FALSE
);

CREATE INDEX idx_shops_domain ON shops(shopify_domain);
CREATE INDEX idx_shops_active ON shops(is_active);
CREATE INDEX idx_shops_subscription ON shops(subscription_status);

-- =====================================================
-- TABLE: orders
-- Stores order data with VAT calculations
-- =====================================================
CREATE TABLE orders (
    id BIGSERIAL PRIMARY KEY,
    shop_id BIGINT NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    shopify_order_id VARCHAR(100) NOT NULL,
    order_number VARCHAR(50),
    order_date TIMESTAMP,
    customer_country_code VARCHAR(2),
    customer_country_name VARCHAR(100),
    customer_email VARCHAR(255),
    customer_vat_number VARCHAR(50),
    is_b2b BOOLEAN DEFAULT FALSE,
    total_amount DECIMAL(12, 2),
    subtotal_amount DECIMAL(12, 2),
    shipping_amount DECIMAL(12, 2),
    tax_amount DECIMAL(12, 2),
    currency VARCHAR(3),
    applied_vat_rate DECIMAL(5, 2),
    expected_vat_rate DECIMAL(5, 2),
    calculated_vat_amount DECIMAL(12, 2),
    vat_difference DECIMAL(12, 2),
    has_vat_error BOOLEAN DEFAULT FALSE,
    vat_error_type VARCHAR(50),
    vat_exempt BOOLEAN DEFAULT FALSE,
    vat_exempt_reason VARCHAR(255),
    financial_status VARCHAR(30),
    fulfillment_status VARCHAR(30),
    is_refunded BOOLEAN DEFAULT FALSE,
    refund_amount DECIMAL(12, 2),
    included_in_oss_report BOOLEAN DEFAULT FALSE,
    oss_report_id BIGINT,
    raw_data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(shop_id, shopify_order_id)
);

CREATE INDEX idx_orders_shop ON orders(shop_id);
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_orders_country ON orders(customer_country_code);
CREATE INDEX idx_orders_vat_error ON orders(has_vat_error);
CREATE INDEX idx_orders_oss_report ON orders(oss_report_id);
CREATE INDEX idx_orders_shopify_id ON orders(shopify_order_id);

-- =====================================================
-- TABLE: vat_validations
-- Stores VIES VAT number validation results (legal proof)
-- =====================================================
CREATE TABLE vat_validations (
    id BIGSERIAL PRIMARY KEY,
    shop_id BIGINT NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    order_id BIGINT,
    shopify_order_id VARCHAR(100),
    vat_number VARCHAR(50) NOT NULL,
    country_code VARCHAR(2) NOT NULL,
    vat_number_without_country VARCHAR(50),
    validation_status VARCHAR(20) NOT NULL,
    company_name VARCHAR(255),
    company_address TEXT,
    vies_request_id VARCHAR(100),
    vies_response TEXT,
    error_message VARCHAR(500),
    retry_count INTEGER DEFAULT 0,
    validation_date TIMESTAMP NOT NULL,
    next_retry_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vat_validations_shop ON vat_validations(shop_id);
CREATE INDEX idx_vat_validations_order ON vat_validations(order_id);
CREATE INDEX idx_vat_validations_vat_number ON vat_validations(vat_number);
CREATE INDEX idx_vat_validations_status ON vat_validations(validation_status);
CREATE INDEX idx_vat_validations_date ON vat_validations(validation_date);

-- =====================================================
-- TABLE: oss_reports
-- Stores generated OSS quarterly reports
-- =====================================================
CREATE TABLE oss_reports (
    id BIGSERIAL PRIMARY KEY,
    shop_id BIGINT NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    year INTEGER NOT NULL,
    quarter INTEGER NOT NULL CHECK (quarter BETWEEN 1 AND 4),
    period_start TIMESTAMP,
    period_end TIMESTAMP,
    total_sales DECIMAL(14, 2),
    total_vat DECIMAL(14, 2),
    total_orders INTEGER,
    b2b_orders INTEGER,
    b2c_orders INTEGER,
    exempt_orders INTEGER,
    countries_count INTEGER,
    status VARCHAR(20) DEFAULT 'DRAFT',
    csv_file_path VARCHAR(500),
    pdf_file_path VARCHAR(500),
    csv_file_url VARCHAR(500),
    pdf_file_url VARCHAR(500),
    report_data TEXT,
    generated_at TIMESTAMP,
    downloaded_at TIMESTAMP,
    submitted_at TIMESTAMP,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(shop_id, year, quarter)
);

CREATE INDEX idx_oss_reports_shop ON oss_reports(shop_id);
CREATE INDEX idx_oss_reports_period ON oss_reports(year, quarter);
CREATE INDEX idx_oss_reports_status ON oss_reports(status);

-- =====================================================
-- TABLE: oss_report_lines
-- Stores per-country breakdown for OSS reports
-- =====================================================
CREATE TABLE oss_report_lines (
    id BIGSERIAL PRIMARY KEY,
    report_id BIGINT NOT NULL REFERENCES oss_reports(id) ON DELETE CASCADE,
    country_code VARCHAR(2) NOT NULL,
    country_name VARCHAR(100),
    vat_rate DECIMAL(5, 2) NOT NULL,
    taxable_amount DECIMAL(14, 2),
    vat_amount DECIMAL(14, 2),
    total_amount DECIMAL(14, 2),
    orders_count INTEGER,
    b2c_amount DECIMAL(14, 2),
    b2b_exempt_amount DECIMAL(14, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_oss_report_lines_report ON oss_report_lines(report_id);
CREATE INDEX idx_oss_report_lines_country ON oss_report_lines(country_code);

-- =====================================================
-- TABLE: vat_alerts
-- Stores alerts and notifications for users
-- =====================================================
CREATE TABLE vat_alerts (
    id BIGSERIAL PRIMARY KEY,
    shop_id BIGINT NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    alert_type VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT,
    action_required VARCHAR(500),
    related_order_id BIGINT,
    related_country_code VARCHAR(2),
    is_read BOOLEAN DEFAULT FALSE,
    is_resolved BOOLEAN DEFAULT FALSE,
    email_sent BOOLEAN DEFAULT FALSE,
    email_sent_at TIMESTAMP,
    resolved_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_vat_alerts_shop ON vat_alerts(shop_id);
CREATE INDEX idx_vat_alerts_type ON vat_alerts(alert_type);
CREATE INDEX idx_vat_alerts_read ON vat_alerts(is_read);
CREATE INDEX idx_vat_alerts_resolved ON vat_alerts(is_resolved);

-- =====================================================
-- TABLE: audit_logs
-- Stores audit trail for compliance
-- =====================================================
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    shop_id BIGINT NOT NULL REFERENCES shops(id) ON DELETE CASCADE,
    action_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50),
    entity_id BIGINT,
    description TEXT,
    old_value TEXT,
    new_value TEXT,
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_shop ON audit_logs(shop_id);
CREATE INDEX idx_audit_logs_action ON audit_logs(action_type);
CREATE INDEX idx_audit_logs_date ON audit_logs(created_at);

-- =====================================================
-- TABLE: eu_vat_rates
-- Reference table for EU VAT rates by country
-- =====================================================
CREATE TABLE eu_vat_rates (
    id BIGSERIAL PRIMARY KEY,
    country_code VARCHAR(2) NOT NULL,
    country_name VARCHAR(100) NOT NULL,
    standard_rate DECIMAL(5, 2) NOT NULL,
    reduced_rate DECIMAL(5, 2),
    reduced_rate_2 DECIMAL(5, 2),
    super_reduced_rate DECIMAL(5, 2),
    parking_rate DECIMAL(5, 2),
    is_eu_member BOOLEAN DEFAULT TRUE,
    effective_from DATE NOT NULL,
    effective_to DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_eu_vat_rates_country ON eu_vat_rates(country_code);
CREATE INDEX idx_eu_vat_rates_effective ON eu_vat_rates(effective_from, effective_to);

-- =====================================================
-- TABLE: early_access_signups
-- Stores landing page early access signups
-- =====================================================
CREATE TABLE early_access_signups (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    shop_url VARCHAR(255),
    country VARCHAR(100),
    source VARCHAR(50),
    utm_source VARCHAR(100),
    utm_medium VARCHAR(100),
    utm_campaign VARCHAR(100),
    subscribed BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_early_access_email ON early_access_signups(email);

-- =====================================================
-- INSERT: Default EU VAT rates (2024)
-- =====================================================
INSERT INTO eu_vat_rates (country_code, country_name, standard_rate, reduced_rate, effective_from) VALUES
('AT', 'Autriche', 20.00, 10.00, '2024-01-01'),
('BE', 'Belgique', 21.00, 6.00, '2024-01-01'),
('BG', 'Bulgarie', 20.00, 9.00, '2024-01-01'),
('HR', 'Croatie', 25.00, 13.00, '2024-01-01'),
('CY', 'Chypre', 19.00, 5.00, '2024-01-01'),
('CZ', 'République tchèque', 21.00, 12.00, '2024-01-01'),
('DK', 'Danemark', 25.00, NULL, '2024-01-01'),
('EE', 'Estonie', 22.00, 9.00, '2024-01-01'),
('FI', 'Finlande', 24.00, 10.00, '2024-01-01'),
('FR', 'France', 20.00, 5.50, '2024-01-01'),
('DE', 'Allemagne', 19.00, 7.00, '2024-01-01'),
('GR', 'Grèce', 24.00, 6.00, '2024-01-01'),
('HU', 'Hongrie', 27.00, 5.00, '2024-01-01'),
('IE', 'Irlande', 23.00, 9.00, '2024-01-01'),
('IT', 'Italie', 22.00, 10.00, '2024-01-01'),
('LV', 'Lettonie', 21.00, 12.00, '2024-01-01'),
('LT', 'Lituanie', 21.00, 9.00, '2024-01-01'),
('LU', 'Luxembourg', 17.00, 8.00, '2024-01-01'),
('MT', 'Malte', 18.00, 5.00, '2024-01-01'),
('NL', 'Pays-Bas', 21.00, 9.00, '2024-01-01'),
('PL', 'Pologne', 23.00, 8.00, '2024-01-01'),
('PT', 'Portugal', 23.00, 6.00, '2024-01-01'),
('RO', 'Roumanie', 19.00, 9.00, '2024-01-01'),
('SK', 'Slovaquie', 20.00, 10.00, '2024-01-01'),
('SI', 'Slovénie', 22.00, 9.50, '2024-01-01'),
('ES', 'Espagne', 21.00, 10.00, '2024-01-01'),
('SE', 'Suède', 25.00, 12.00, '2024-01-01');

-- =====================================================
-- Update trigger function
-- =====================================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply triggers
CREATE TRIGGER update_shops_updated_at BEFORE UPDATE ON shops FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_vat_validations_updated_at BEFORE UPDATE ON vat_validations FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_oss_reports_updated_at BEFORE UPDATE ON oss_reports FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
CREATE TRIGGER update_eu_vat_rates_updated_at BEFORE UPDATE ON eu_vat_rates FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();
