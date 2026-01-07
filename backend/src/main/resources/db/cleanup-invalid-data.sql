-- Script de nettoyage des données invalides
-- Exécutez ce script manuellement dans pgAdmin ou psql

-- Supprimer les alertes avec des types invalides
DELETE FROM vat_alerts WHERE alert_type NOT IN (
    'OSS_THRESHOLD_WARNING', 
    'OSS_THRESHOLD_EXCEEDED', 
    'VAT_RATE_ERROR', 
    'VAT_MISSING', 
    'SHOPIFY_CONFIG_ERROR', 
    'QUARTERLY_REMINDER', 
    'SUBSCRIPTION_EXPIRING', 
    'B2B_VAT_INVALID', 
    'ORDER_ERROR'
);

-- Vérifier les alertes restantes
SELECT id, alert_type, title FROM vat_alerts;
