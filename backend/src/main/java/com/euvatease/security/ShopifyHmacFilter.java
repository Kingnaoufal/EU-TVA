package com.euvatease.security;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;

/**
 * Filtre pour vérifier les signatures HMAC des requêtes Shopify.
 * Valide l'authenticité des callbacks provenant de Shopify.
 */
@Component
public class ShopifyHmacFilter extends OncePerRequestFilter {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ----------------------------------------------------------------------------------------------------------------

    private static final Logger LOG = LoggerFactory.getLogger(ShopifyHmacFilter.class);

    private static final String HMAC_ALGORITHM = "HmacSHA256";
    private static final String HMAC_PARAM = "hmac";
    private static final String SHOPIFY_CALLBACK_PATH = "/shopify/callback";

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Value("${shopify.api-secret}")
    private String apiSecret;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Constructeur par défaut.
     */
    public ShopifyHmacFilter() {
        // Constructeur par défaut pour Spring
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request,
                                    @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain)
            throws ServletException, IOException {
        Objects.requireNonNull(request, "request must not be null");
        Objects.requireNonNull(response, "response must not be null");
        Objects.requireNonNull(filterChain, "filterChain must not be null");

        String path = request.getRequestURI();

        // Ne vérifier que les requêtes de callback Shopify
        if (path.contains(SHOPIFY_CALLBACK_PATH) && request.getParameter(HMAC_PARAM) != null) {
            if (!verifyHmac(request)) {
                LOG.warn("HMAC invalide pour callback Shopify");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid HMAC");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Vérifie la signature HMAC de la requête Shopify.
     *
     * @param request la requête HTTP contenant les paramètres à vérifier
     * @return true si la signature HMAC est valide, false sinon
     */
    private boolean verifyHmac(@Nonnull HttpServletRequest request) {
        try {
            String hmac = request.getParameter(HMAC_PARAM);
            if (hmac == null) {
                return false;
            }

            String message = buildMessageFromParameters(request.getParameterMap());
            String computedHmac = computeHmac(message);

            return hmac.equals(computedHmac);

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOG.error("Erreur vérification HMAC: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Construit la chaîne de message à partir des paramètres de la requête.
     *
     * @param parameterMap la map des paramètres
     * @return la chaîne construite pour le calcul HMAC
     */
    @Nonnull
    private String buildMessageFromParameters(@Nonnull Map<String, String[]> parameterMap) {
        StringBuilder message = new StringBuilder();
        parameterMap.entrySet().stream()
            .filter(e -> !HMAC_PARAM.equals(e.getKey()))
            .sorted(Map.Entry.comparingByKey())
            .forEach(e -> {
                if (message.length() > 0) {
                    message.append("&");
                }
                message.append(e.getKey()).append("=").append(e.getValue()[0]);
            });
        return message.toString();
    }

    /**
     * Calcule le HMAC SHA256 du message.
     *
     * @param message le message à signer
     * @return la signature HMAC en hexadécimal
     * @throws NoSuchAlgorithmException si l'algorithme n'est pas disponible
     * @throws InvalidKeyException      si la clé est invalide
     */
    @Nonnull
    private String computeHmac(@Nonnull String message)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        SecretKeySpec secretKey = new SecretKeySpec(
                apiSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
        mac.init(secretKey);
        byte[] hash = mac.doFinal(message.getBytes(StandardCharsets.UTF_8));

        return bytesToHex(hash);
    }

    /**
     * Convertit un tableau de bytes en chaîne hexadécimale.
     *
     * @param bytes le tableau de bytes à convertir
     * @return la chaîne hexadécimale
     */
    @Nonnull
    private String bytesToHex(@Nonnull byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
