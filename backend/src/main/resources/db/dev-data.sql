-- =====================================================
-- EU VAT Ease - Données de développement/test
-- Ce fichier est exécuté uniquement en mode développement
-- Les données ne sont insérées que si elles n'existent pas
-- =====================================================

-- =====================================================
-- Shop de développement (si pas déjà créé par JwtAuthenticationFilter)
-- =====================================================
INSERT INTO shops (
    shopify_domain, shop_name, email, country_code, currency, 
    vat_number, oss_registered, oss_country_code, subscription_status, 
    subscription_plan, trial_ends_at, created_at, is_active, alert_email_enabled
)
SELECT 
    'dev-shop.myshopify.com', 'Dev Test Shop', 'dev@test.com', 'FR', 'EUR',
    'FR12345678901', TRUE, 'FR', 'ACTIVE', 
    'monthly', NOW() + INTERVAL '14 days', NOW(), TRUE, TRUE
WHERE NOT EXISTS (
    SELECT 1 FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'
);

-- =====================================================
-- Commandes de test avec différents scénarios
-- =====================================================

-- Commande normale France (domestique)
INSERT INTO orders (
    shop_id, shopify_order_id, order_number, order_date,
    customer_country_code, customer_country_name, customer_email,
    is_b2b, total_amount, subtotal_amount, tax_amount, currency,
    applied_vat_rate, expected_vat_rate, calculated_vat_amount,
    has_vat_error, financial_status, fulfillment_status
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'DEV-1001', '#1001', NOW() - INTERVAL '2 days',
    'FR', 'France', 'client1@example.fr',
    FALSE, 120.00, 100.00, 20.00, 'EUR',
    20.00, 20.00, 20.00,
    FALSE, 'PAID', 'FULFILLED'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE shopify_order_id = 'DEV-1001');

-- Commande Allemagne - TVA correcte
INSERT INTO orders (
    shop_id, shopify_order_id, order_number, order_date,
    customer_country_code, customer_country_name, customer_email,
    is_b2b, total_amount, subtotal_amount, tax_amount, currency,
    applied_vat_rate, expected_vat_rate, calculated_vat_amount,
    has_vat_error, financial_status, fulfillment_status
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'DEV-1002', '#1002', NOW() - INTERVAL '3 days',
    'DE', 'Allemagne', 'kunde@example.de',
    FALSE, 119.00, 100.00, 19.00, 'EUR',
    19.00, 19.00, 19.00,
    FALSE, 'PAID', 'FULFILLED'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE shopify_order_id = 'DEV-1002');

-- Commande Pays-Bas - ERREUR: mauvais taux TVA (17.36% au lieu de 21%)
INSERT INTO orders (
    shop_id, shopify_order_id, order_number, order_date,
    customer_country_code, customer_country_name, customer_email,
    is_b2b, total_amount, subtotal_amount, tax_amount, currency,
    applied_vat_rate, expected_vat_rate, calculated_vat_amount, vat_difference,
    has_vat_error, vat_error_type, financial_status, fulfillment_status
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'DEV-1003', '#1003', NOW() - INTERVAL '1 day',
    'NL', 'Pays-Bas', 'klant@example.nl',
    FALSE, 117.36, 100.00, 17.36, 'EUR',
    17.36, 21.00, 21.00, -3.64,
    TRUE, 'WRONG_RATE', 'PAID', 'FULFILLED'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE shopify_order_id = 'DEV-1003');

-- Commande Belgique B2B - ERREUR: TVA facturée alors que exempté
INSERT INTO orders (
    shop_id, shopify_order_id, order_number, order_date,
    customer_country_code, customer_country_name, customer_email,
    customer_vat_number, is_b2b, total_amount, subtotal_amount, tax_amount, currency,
    applied_vat_rate, expected_vat_rate, calculated_vat_amount,
    has_vat_error, vat_error_type, vat_exempt, vat_exempt_reason,
    financial_status, fulfillment_status
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'DEV-1004', '#1004', NOW() - INTERVAL '4 days',
    'BE', 'Belgique', 'business@example.be',
    'BE0123456789', TRUE, 121.00, 100.00, 21.00, 'EUR',
    21.00, 0.00, 0.00,
    TRUE, 'B2B_VAT_CHARGED', TRUE, 'B2B avec numéro TVA valide',
    'PAID', 'FULFILLED'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE shopify_order_id = 'DEV-1004');

-- Commande Italie - TVA manquante
INSERT INTO orders (
    shop_id, shopify_order_id, order_number, order_date,
    customer_country_code, customer_country_name, customer_email,
    is_b2b, total_amount, subtotal_amount, tax_amount, currency,
    applied_vat_rate, expected_vat_rate, calculated_vat_amount, vat_difference,
    has_vat_error, vat_error_type, financial_status, fulfillment_status
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'DEV-1005', '#1005', NOW() - INTERVAL '5 days',
    'IT', 'Italie', 'cliente@example.it',
    FALSE, 100.00, 100.00, 0.00, 'EUR',
    0.00, 22.00, 22.00, -22.00,
    TRUE, 'MISSING_VAT', 'PAID', 'FULFILLED'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE shopify_order_id = 'DEV-1005');

-- Commande Espagne - correcte
INSERT INTO orders (
    shop_id, shopify_order_id, order_number, order_date,
    customer_country_code, customer_country_name, customer_email,
    is_b2b, total_amount, subtotal_amount, tax_amount, currency,
    applied_vat_rate, expected_vat_rate, calculated_vat_amount,
    has_vat_error, financial_status, fulfillment_status
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'DEV-1006', '#1006', NOW() - INTERVAL '6 days',
    'ES', 'Espagne', 'cliente@example.es',
    FALSE, 121.00, 100.00, 21.00, 'EUR',
    21.00, 21.00, 21.00,
    FALSE, 'PAID', 'FULFILLED'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE shopify_order_id = 'DEV-1006');

-- Commande Portugal - correcte
INSERT INTO orders (
    shop_id, shopify_order_id, order_number, order_date,
    customer_country_code, customer_country_name, customer_email,
    is_b2b, total_amount, subtotal_amount, tax_amount, currency,
    applied_vat_rate, expected_vat_rate, calculated_vat_amount,
    has_vat_error, financial_status, fulfillment_status
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'DEV-1007', '#1007', NOW() - INTERVAL '7 days',
    'PT', 'Portugal', 'cliente@example.pt',
    FALSE, 123.00, 100.00, 23.00, 'EUR',
    23.00, 23.00, 23.00,
    FALSE, 'PAID', 'FULFILLED'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE shopify_order_id = 'DEV-1007');

-- Commande Autriche - correcte
INSERT INTO orders (
    shop_id, shopify_order_id, order_number, order_date,
    customer_country_code, customer_country_name, customer_email,
    is_b2b, total_amount, subtotal_amount, tax_amount, currency,
    applied_vat_rate, expected_vat_rate, calculated_vat_amount,
    has_vat_error, financial_status, fulfillment_status
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'DEV-1008', '#1008', NOW() - INTERVAL '8 days',
    'AT', 'Autriche', 'kunde@example.at',
    FALSE, 120.00, 100.00, 20.00, 'EUR',
    20.00, 20.00, 20.00,
    FALSE, 'PAID', 'FULFILLED'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE shopify_order_id = 'DEV-1008');

-- Commande Pologne - TVA excessive
INSERT INTO orders (
    shop_id, shopify_order_id, order_number, order_date,
    customer_country_code, customer_country_name, customer_email,
    is_b2b, total_amount, subtotal_amount, tax_amount, currency,
    applied_vat_rate, expected_vat_rate, calculated_vat_amount, vat_difference,
    has_vat_error, vat_error_type, financial_status, fulfillment_status
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'DEV-1009', '#1009', NOW() - INTERVAL '9 days',
    'PL', 'Pologne', 'klient@example.pl',
    FALSE, 127.00, 100.00, 27.00, 'EUR',
    27.00, 23.00, 23.00, 4.00,
    TRUE, 'EXCESSIVE_VAT', 'PAID', 'FULFILLED'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE shopify_order_id = 'DEV-1009');

-- Commande Irlande - correcte
INSERT INTO orders (
    shop_id, shopify_order_id, order_number, order_date,
    customer_country_code, customer_country_name, customer_email,
    is_b2b, total_amount, subtotal_amount, tax_amount, currency,
    applied_vat_rate, expected_vat_rate, calculated_vat_amount,
    has_vat_error, financial_status, fulfillment_status
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'DEV-1010', '#1010', NOW() - INTERVAL '10 days',
    'IE', 'Irlande', 'customer@example.ie',
    FALSE, 123.00, 100.00, 23.00, 'EUR',
    23.00, 23.00, 23.00,
    FALSE, 'PAID', 'FULFILLED'
WHERE NOT EXISTS (SELECT 1 FROM orders WHERE shopify_order_id = 'DEV-1010');

-- =====================================================
-- Alertes de test
-- =====================================================

-- Alerte critique - Erreur TVA Pays-Bas
INSERT INTO vat_alerts (
    shop_id, alert_type, severity, title, message, 
    action_required, related_order_id, is_read, is_resolved, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'VAT_RATE_ERROR', 'CRITICAL', 'Erreur de taux TVA critique',
    'La commande #1003 a un taux de TVA de 17.36% au lieu des 21% attendus pour les Pays-Bas. Différence: 3.64€',
    'Vérifiez la configuration TVA pour les Pays-Bas dans Shopify',
    (SELECT id FROM orders WHERE shopify_order_id = 'DEV-1003'),
    FALSE, FALSE, NOW() - INTERVAL '1 day'
WHERE NOT EXISTS (
    SELECT 1 FROM vat_alerts WHERE title = 'Erreur de taux TVA critique' 
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

-- Alerte warning - B2B TVA facturée
INSERT INTO vat_alerts (
    shop_id, alert_type, severity, title, message,
    action_required, related_order_id, is_read, is_resolved, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'B2B_VAT_INVALID', 'WARNING', 'TVA facturée en B2B',
    'La commande #1004 (Belgique) a été facturée avec TVA alors que le client a un numéro de TVA validé (BE0123456789).',
    'Émettez un avoir pour corriger la TVA facturée à tort',
    (SELECT id FROM orders WHERE shopify_order_id = 'DEV-1004'),
    FALSE, FALSE, NOW() - INTERVAL '2 days'
WHERE NOT EXISTS (
    SELECT 1 FROM vat_alerts WHERE title = 'TVA facturée en B2B'
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

-- Alerte warning - Seuil OSS
INSERT INTO vat_alerts (
    shop_id, alert_type, severity, title, message,
    action_required, is_read, is_resolved, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'OSS_THRESHOLD_WARNING', 'WARNING', 'Seuil OSS approchant',
    'Vous avez atteint 85% du seuil OSS (8 500€ / 10 000€). Préparez votre inscription au régime OSS.',
    'Inscrivez-vous au régime OSS avant de dépasser le seuil de 10 000€',
    FALSE, FALSE, NOW() - INTERVAL '3 days'
WHERE NOT EXISTS (
    SELECT 1 FROM vat_alerts WHERE title = 'Seuil OSS approchant'
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

-- Alerte info - Service VIES
INSERT INTO vat_alerts (
    shop_id, alert_type, severity, title, message,
    action_required, is_read, is_resolved, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'ORDER_ERROR', 'INFO', 'Service VIES indisponible',
    'La validation du numéro FR12345678901 a échoué car le service VIES était indisponible. Nouvelle tentative programmée.',
    'Aucune action requise - nouvelle tentative automatique',
    FALSE, FALSE, NOW() - INTERVAL '4 days'
WHERE NOT EXISTS (
    SELECT 1 FROM vat_alerts WHERE title = 'Service VIES indisponible'
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

-- Alerte warning - Échéance OSS
INSERT INTO vat_alerts (
    shop_id, alert_type, severity, title, message,
    action_required, is_read, is_resolved, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'QUARTERLY_REMINDER', 'WARNING', 'Échéance OSS T4 2025',
    'La date limite de déclaration OSS T4 2025 est le 31 janvier 2026. Il vous reste 24 jours.',
    'Générez et soumettez votre déclaration OSS T4 2025',
    FALSE, FALSE, NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM vat_alerts WHERE title = 'Échéance OSS T4 2025'
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

-- Alerte résolue - TVA manquante (pour tester l'onglet résolues)
INSERT INTO vat_alerts (
    shop_id, alert_type, severity, title, message,
    action_required, related_order_id, is_read, is_resolved, resolved_at, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'VAT_MISSING', 'WARNING', 'TVA manquante corrigée',
    'La commande #1005 (Italie) n''a pas de TVA alors qu''elle devrait être facturée à 22%.',
    'Corrigez la configuration TVA pour l''Italie',
    (SELECT id FROM orders WHERE shopify_order_id = 'DEV-1005'),
    TRUE, TRUE, NOW() - INTERVAL '1 day', NOW() - INTERVAL '5 days'
WHERE NOT EXISTS (
    SELECT 1 FROM vat_alerts WHERE title = 'TVA manquante corrigée'
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

-- =====================================================
-- Validations VIES de test
-- =====================================================

-- Validation réussie
INSERT INTO vat_validations (
    shop_id, vat_number, country_code, vat_number_without_country,
    validation_status, company_name, company_address,
    validation_date, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'BE0123456789', 'BE', '0123456789',
    'VALID', 'Example Company BVBA', 'Rue de la Loi 42, 1000 Bruxelles',
    NOW() - INTERVAL '4 days', NOW() - INTERVAL '4 days'
WHERE NOT EXISTS (
    SELECT 1 FROM vat_validations WHERE vat_number = 'BE0123456789'
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

-- Validation invalide
INSERT INTO vat_validations (
    shop_id, vat_number, country_code, vat_number_without_country,
    validation_status, error_message,
    validation_date, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'DE999999999', 'DE', '999999999',
    'INVALID', 'Numéro de TVA non trouvé dans le registre VIES',
    NOW() - INTERVAL '2 days', NOW() - INTERVAL '2 days'
WHERE NOT EXISTS (
    SELECT 1 FROM vat_validations WHERE vat_number = 'DE999999999'
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

-- Validation en attente (service indisponible)
INSERT INTO vat_validations (
    shop_id, vat_number, country_code, vat_number_without_country,
    validation_status, error_message, retry_count, next_retry_at,
    validation_date, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'FR12345678901', 'FR', '12345678901',
    'UNAVAILABLE', 'Service VIES temporairement indisponible',
    2, NOW() + INTERVAL '1 hour',
    NOW() - INTERVAL '1 day', NOW() - INTERVAL '1 day'
WHERE NOT EXISTS (
    SELECT 1 FROM vat_validations WHERE vat_number = 'FR12345678901'
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

-- =====================================================
-- Rapport OSS de test (T4 2025)
-- =====================================================

INSERT INTO oss_reports (
    shop_id, year, quarter, period_start, period_end,
    total_sales, total_vat, total_orders, b2b_orders, b2c_orders, exempt_orders,
    countries_count, status, generated_at, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    2025, 4, '2025-10-01 00:00:00', '2025-12-31 23:59:59',
    42350.00, 8540.25, 110, 12, 98, 5,
    8, 'GENERATED', NOW() - INTERVAL '5 days', NOW() - INTERVAL '5 days'
WHERE NOT EXISTS (
    SELECT 1 FROM oss_reports WHERE year = 2025 AND quarter = 4
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

-- Lignes du rapport OSS T4 2025
INSERT INTO oss_report_lines (
    report_id, country_code, country_name, vat_rate, 
    taxable_amount, vat_amount, total_amount, orders_count
)
SELECT 
    (SELECT id FROM oss_reports WHERE year = 2025 AND quarter = 4 
     AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')),
    'DE', 'Allemagne', 19.00,
    12500.00, 2375.00, 14875.00, 32
WHERE NOT EXISTS (
    SELECT 1 FROM oss_report_lines WHERE country_code = 'DE' 
    AND report_id = (SELECT id FROM oss_reports WHERE year = 2025 AND quarter = 4 
                     AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'))
);

INSERT INTO oss_report_lines (
    report_id, country_code, country_name, vat_rate, 
    taxable_amount, vat_amount, total_amount, orders_count
)
SELECT 
    (SELECT id FROM oss_reports WHERE year = 2025 AND quarter = 4 
     AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')),
    'BE', 'Belgique', 21.00,
    8200.00, 1722.00, 9922.00, 21
WHERE NOT EXISTS (
    SELECT 1 FROM oss_report_lines WHERE country_code = 'BE' 
    AND report_id = (SELECT id FROM oss_reports WHERE year = 2025 AND quarter = 4 
                     AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'))
);

INSERT INTO oss_report_lines (
    report_id, country_code, country_name, vat_rate, 
    taxable_amount, vat_amount, total_amount, orders_count
)
SELECT 
    (SELECT id FROM oss_reports WHERE year = 2025 AND quarter = 4 
     AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')),
    'ES', 'Espagne', 21.00,
    5400.00, 1134.00, 6534.00, 15
WHERE NOT EXISTS (
    SELECT 1 FROM oss_report_lines WHERE country_code = 'ES' 
    AND report_id = (SELECT id FROM oss_reports WHERE year = 2025 AND quarter = 4 
                     AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'))
);

INSERT INTO oss_report_lines (
    report_id, country_code, country_name, vat_rate, 
    taxable_amount, vat_amount, total_amount, orders_count
)
SELECT 
    (SELECT id FROM oss_reports WHERE year = 2025 AND quarter = 4 
     AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')),
    'IT', 'Italie', 22.00,
    5500.00, 1210.00, 6710.00, 14
WHERE NOT EXISTS (
    SELECT 1 FROM oss_report_lines WHERE country_code = 'IT' 
    AND report_id = (SELECT id FROM oss_reports WHERE year = 2025 AND quarter = 4 
                     AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'))
);

INSERT INTO oss_report_lines (
    report_id, country_code, country_name, vat_rate, 
    taxable_amount, vat_amount, total_amount, orders_count
)
SELECT 
    (SELECT id FROM oss_reports WHERE year = 2025 AND quarter = 4 
     AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')),
    'NL', 'Pays-Bas', 21.00,
    6250.00, 1312.50, 7562.50, 18
WHERE NOT EXISTS (
    SELECT 1 FROM oss_report_lines WHERE country_code = 'NL' 
    AND report_id = (SELECT id FROM oss_reports WHERE year = 2025 AND quarter = 4 
                     AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'))
);

INSERT INTO oss_report_lines (
    report_id, country_code, country_name, vat_rate, 
    taxable_amount, vat_amount, total_amount, orders_count
)
SELECT 
    (SELECT id FROM oss_reports WHERE year = 2025 AND quarter = 4 
     AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')),
    'AT', 'Autriche', 20.00,
    4500.00, 900.00, 5400.00, 10
WHERE NOT EXISTS (
    SELECT 1 FROM oss_report_lines WHERE country_code = 'AT' 
    AND report_id = (SELECT id FROM oss_reports WHERE year = 2025 AND quarter = 4 
                     AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'))
);

-- Rapport OSS T3 2025 (soumis)
INSERT INTO oss_reports (
    shop_id, year, quarter, period_start, period_end,
    total_sales, total_vat, total_orders, b2b_orders, b2c_orders, exempt_orders,
    countries_count, status, generated_at, submitted_at, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    2025, 3, '2025-07-01 00:00:00', '2025-09-30 23:59:59',
    38750.00, 7820.50, 95, 8, 87, 3,
    7, 'SUBMITTED', '2025-10-02 10:00:00', '2025-10-15 14:30:00', '2025-10-02 10:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM oss_reports WHERE year = 2025 AND quarter = 3
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

-- Rapport OSS T2 2025 (soumis)
INSERT INTO oss_reports (
    shop_id, year, quarter, period_start, period_end,
    total_sales, total_vat, total_orders, b2b_orders, b2c_orders, exempt_orders,
    countries_count, status, generated_at, submitted_at, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    2025, 2, '2025-04-01 00:00:00', '2025-06-30 23:59:59',
    35200.00, 7105.00, 88, 10, 78, 4,
    6, 'SUBMITTED', '2025-07-02 10:00:00', '2025-07-18 09:15:00', '2025-07-02 10:00:00'
WHERE NOT EXISTS (
    SELECT 1 FROM oss_reports WHERE year = 2025 AND quarter = 2
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

-- =====================================================
-- Données de log d'audit
-- =====================================================

INSERT INTO audit_logs (
    shop_id, action_type, entity_type, description, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'REPORT_GENERATED', 'OSS_REPORT', 'Rapport OSS T4 2025 généré',
    NOW() - INTERVAL '5 days'
WHERE NOT EXISTS (
    SELECT 1 FROM audit_logs WHERE description = 'Rapport OSS T4 2025 généré'
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

INSERT INTO audit_logs (
    shop_id, action_type, entity_type, description, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'VAT_VALIDATION', 'VIES', 'Validation VIES réussie pour BE0123456789',
    NOW() - INTERVAL '4 days'
WHERE NOT EXISTS (
    SELECT 1 FROM audit_logs WHERE description = 'Validation VIES réussie pour BE0123456789'
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);

INSERT INTO audit_logs (
    shop_id, action_type, entity_type, description, created_at
)
SELECT 
    (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com'),
    'ALERT_CREATED', 'VAT_ALERT', 'Alerte créée: Erreur de taux TVA critique',
    NOW() - INTERVAL '1 day'
WHERE NOT EXISTS (
    SELECT 1 FROM audit_logs WHERE description = 'Alerte créée: Erreur de taux TVA critique'
    AND shop_id = (SELECT id FROM shops WHERE shopify_domain = 'dev-shop.myshopify.com')
);
