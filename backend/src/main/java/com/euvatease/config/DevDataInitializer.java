package com.euvatease.config;

import jakarta.annotation.Nonnull;
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
import java.util.Objects;

/**
 * Configuration pour charger les données de développement/test
 * uniquement lorsque app.dev-mode est activé.
 */
@Configuration
public class DevDataInitializer {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ----------------------------------------------------------------------------------------------------------------

    private static final Logger LOG = LoggerFactory.getLogger(DevDataInitializer.class);

    private static final String VALID_ALERT_TYPES =
            "'OSS_THRESHOLD_WARNING', 'OSS_THRESHOLD_EXCEEDED', 'VAT_RATE_ERROR', " +
            "'VAT_MISSING', 'SHOPIFY_CONFIG_ERROR', 'QUARTERLY_REMINDER', " +
            "'SUBSCRIPTION_EXPIRING', 'B2B_VAT_INVALID', 'ORDER_ERROR'";

    private static final String VALID_FINANCIAL_STATUSES =
            "'PENDING', 'AUTHORIZED', 'PARTIALLY_PAID', 'PAID', 'PARTIALLY_REFUNDED', 'REFUNDED', 'VOIDED'";

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Value("${app.dev-mode:false}")
    private boolean devMode;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructeur par défaut.
     */
    public DevDataInitializer() {
        // Constructeur par défaut pour Spring
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Charge les données de développement au démarrage de l'application.
     *
     * @param dataSource   la source de données (non null)
     * @param jdbcTemplate le template JDBC pour les requêtes (non null)
     * @return le runner de commande
     */
    @Bean
    @Order(1)
    @Nonnull
    public CommandLineRunner loadDevData(@Nonnull DataSource dataSource,
                                         @Nonnull JdbcTemplate jdbcTemplate) {
        Objects.requireNonNull(dataSource, "dataSource must not be null");
        Objects.requireNonNull(jdbcTemplate, "jdbcTemplate must not be null");

        return args -> {
            if (!devMode) {
                LOG.info("Mode production - pas de chargement de données de test");
                return;
            }

            LOG.info("=================================================");
            LOG.info("MODE DÉVELOPPEMENT ACTIVÉ");
            LOG.info("Nettoyage et chargement des données de test...");
            LOG.info("=================================================");

            try {
                cleanupInvalidAlerts(jdbcTemplate);
                cleanupInvalidOrders(jdbcTemplate);
                loadDevDataFromSql(dataSource);
            } catch (Exception e) {
                LOG.error("Erreur lors du chargement des données de développement: {}", e.getMessage());
                // Ne pas faire échouer le démarrage
            }

            LOG.info("=================================================");
        };
    }

    /**
     * Nettoie les alertes avec des types invalides.
     *
     * @param jdbcTemplate le template JDBC
     */
    private void cleanupInvalidAlerts(@Nonnull JdbcTemplate jdbcTemplate) {
        int deletedAlerts = jdbcTemplate.update(
                "DELETE FROM vat_alerts WHERE alert_type NOT IN (" + VALID_ALERT_TYPES + ")");
        if (deletedAlerts > 0) {
            LOG.info("✓ {} alertes avec types invalides supprimées", deletedAlerts);
        }
    }

    /**
     * Nettoie les commandes avec des statuts invalides.
     *
     * @param jdbcTemplate le template JDBC
     */
    private void cleanupInvalidOrders(@Nonnull JdbcTemplate jdbcTemplate) {
        int deletedOrders = jdbcTemplate.update(
                "DELETE FROM orders WHERE financial_status IS NOT NULL AND financial_status NOT IN ("
                + VALID_FINANCIAL_STATUSES + ")");
        if (deletedOrders > 0) {
            LOG.info("✓ {} commandes avec statuts invalides supprimées", deletedOrders);
        }
    }

    /**
     * Charge les données de développement depuis le fichier SQL.
     *
     * @param dataSource la source de données
     */
    private void loadDevDataFromSql(@Nonnull DataSource dataSource) {
        ClassPathResource resource = new ClassPathResource("db/dev-data.sql");
        if (resource.exists()) {
            ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
            populator.addScript(resource);
            populator.setContinueOnError(true);
            populator.execute(dataSource);
            LOG.info("✓ Données de développement chargées avec succès");
        } else {
            LOG.warn("Fichier dev-data.sql non trouvé");
        }
    }
}
