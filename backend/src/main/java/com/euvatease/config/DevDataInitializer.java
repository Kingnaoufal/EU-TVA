package com.euvatease.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Configuration pour charger les données de développement/test
 * uniquement lorsque app.dev-mode est activé
 */
@Configuration
public class DevDataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DevDataInitializer.class);

    @Value("${app.dev-mode:false}")
    private boolean devMode;

    @Bean
    @Order(1) // Exécute en premier
    public CommandLineRunner loadDevData(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        return args -> {
            if (!devMode) {
                log.info("Mode production - pas de chargement de données de test");
                return;
            }

            log.info("=================================================");
            log.info("MODE DÉVELOPPEMENT ACTIVÉ");
            log.info("Nettoyage et chargement des données de test...");
            log.info("=================================================");

            try {
                // D'abord, nettoyer les alertes avec des types invalides
                String validAlertTypes = "'OSS_THRESHOLD_WARNING', 'OSS_THRESHOLD_EXCEEDED', 'VAT_RATE_ERROR', " +
                        "'VAT_MISSING', 'SHOPIFY_CONFIG_ERROR', 'QUARTERLY_REMINDER', " +
                        "'SUBSCRIPTION_EXPIRING', 'B2B_VAT_INVALID', 'ORDER_ERROR'";
                int deletedAlerts = jdbcTemplate.update(
                        "DELETE FROM vat_alerts WHERE alert_type NOT IN (" + validAlertTypes + ")");
                if (deletedAlerts > 0) {
                    log.info("✓ {} alertes avec types invalides supprimées", deletedAlerts);
                }

                // Nettoyer les commandes avec des statuts invalides
                String validFinancialStatuses = "'PENDING', 'AUTHORIZED', 'PARTIALLY_PAID', 'PAID', 'PARTIALLY_REFUNDED', 'REFUNDED', 'VOIDED'";
                int deletedOrders = jdbcTemplate.update(
                        "DELETE FROM orders WHERE financial_status IS NOT NULL AND financial_status NOT IN (" + validFinancialStatuses + ")");
                if (deletedOrders > 0) {
                    log.info("✓ {} commandes avec statuts invalides supprimées", deletedOrders);
                }

                // Ensuite, charger les données de test
                ClassPathResource resource = new ClassPathResource("db/dev-data.sql");
                if (resource.exists()) {
                    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                    populator.addScript(resource);
                    populator.setContinueOnError(true); // Continue si données existent déjà
                    populator.execute(dataSource);
                    log.info("✓ Données de développement chargées avec succès");
                } else {
                    log.warn("Fichier dev-data.sql non trouvé");
                }
            } catch (Exception e) {
                log.error("Erreur lors du chargement des données de développement: {}", e.getMessage());
                // Ne pas faire échouer le démarrage
            }

            log.info("=================================================");
        };
    }
}
