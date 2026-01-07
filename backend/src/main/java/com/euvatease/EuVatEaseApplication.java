package com.euvatease;

import jakarta.annotation.Nonnull;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Objects;

/**
 * Classe principale de l'application EU VAT Ease.
 * Application Spring Boot pour la gestion de la TVA européenne pour les boutiques Shopify.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class EuVatEaseApplication {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructeur par défaut.
     */
    public EuVatEaseApplication() {
        // Constructeur par défaut pour Spring Boot
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Point d'entrée principal de l'application.
     *
     * @param args les arguments de ligne de commande (non null)
     */
    public static void main(@Nonnull String[] args) {
        Objects.requireNonNull(args, "args must not be null");
        SpringApplication.run(EuVatEaseApplication.class, args);
    }
}
