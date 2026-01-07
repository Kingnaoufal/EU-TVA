package com.euvatease.security;

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
import java.util.Base64;

/**
 * Filtre pour vérifier les signatures HMAC des requêtes Shopify
 */
@Component
public class ShopifyHmacFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(ShopifyHmacFilter.class);

    @Value("${shopify.api-secret}")
    private String apiSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Ne vérifier que les requêtes de callback Shopify
        if (path.contains("/shopify/callback") && request.getParameter("hmac") != null) {
            if (!verifyHmac(request)) {
                log.warn("HMAC invalide pour callback Shopify");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid HMAC");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean verifyHmac(HttpServletRequest request) {
        try {
            String hmac = request.getParameter("hmac");
            if (hmac == null) return false;

            // Construire la chaîne à vérifier (tous les params sauf hmac)
            StringBuilder message = new StringBuilder();
            request.getParameterMap().entrySet().stream()
                .filter(e -> !"hmac".equals(e.getKey()))
                .sorted(java.util.Map.Entry.comparingByKey())
                .forEach(e -> {
                    if (message.length() > 0) message.append("&");
                    message.append(e.getKey()).append("=").append(e.getValue()[0]);
                });

            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(apiSecret.getBytes(), "HmacSHA256");
            mac.init(secretKey);
            byte[] hash = mac.doFinal(message.toString().getBytes());

            // Convertir en hex
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hmac.equals(hexString.toString());

        } catch (Exception e) {
            log.error("Erreur vérification HMAC: {}", e.getMessage());
            return false;
        }
    }
}
