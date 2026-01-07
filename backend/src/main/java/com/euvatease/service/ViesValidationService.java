package com.euvatease.service;

import com.euvatease.dto.VatValidationResult;
import com.euvatease.entity.Shop;
import com.euvatease.entity.VatValidation;
import com.euvatease.entity.VatValidation.ValidationStatus;
import com.euvatease.repository.VatValidationRepository;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.xml.soap.MessageFactory;
import jakarta.xml.soap.SOAPBody;
import jakarta.xml.soap.SOAPConnection;
import jakarta.xml.soap.SOAPConnectionFactory;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPEnvelope;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.soap.SOAPPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Service de validation des numéros de TVA intracommunautaire via le service VIES officiel.
 */
@Service
public class ViesValidationService {

    //~ ------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ViesValidationService.class);

    // Codes pays UE valides pour la TVA
    private static final Set<String> EU_COUNTRY_CODES = Set.of(
        "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR",
        "DE", "GR", "HU", "IE", "IT", "LV", "LT", "LU", "MT", "NL",
        "PL", "PT", "RO", "SK", "SI", "ES", "SE"
    );

    // Patterns de validation par pays (formats simplifiés)
    private static final Map<String, Pattern> VAT_PATTERNS = Map.ofEntries(
        Map.entry("AT", Pattern.compile("^ATU[0-9]{8}$")),
        Map.entry("BE", Pattern.compile("^BE[0-9]{10}$")),
        Map.entry("BG", Pattern.compile("^BG[0-9]{9,10}$")),
        Map.entry("HR", Pattern.compile("^HR[0-9]{11}$")),
        Map.entry("CY", Pattern.compile("^CY[0-9]{8}[A-Z]$")),
        Map.entry("CZ", Pattern.compile("^CZ[0-9]{8,10}$")),
        Map.entry("DK", Pattern.compile("^DK[0-9]{8}$")),
        Map.entry("EE", Pattern.compile("^EE[0-9]{9}$")),
        Map.entry("FI", Pattern.compile("^FI[0-9]{8}$")),
        Map.entry("FR", Pattern.compile("^FR[A-Z0-9]{2}[0-9]{9}$")),
        Map.entry("DE", Pattern.compile("^DE[0-9]{9}$")),
        Map.entry("GR", Pattern.compile("^(GR|EL)[0-9]{9}$")),
        Map.entry("HU", Pattern.compile("^HU[0-9]{8}$")),
        Map.entry("IE", Pattern.compile("^IE[0-9]{7}[A-Z]{1,2}$|^IE[0-9][A-Z][0-9]{5}[A-Z]$")),
        Map.entry("IT", Pattern.compile("^IT[0-9]{11}$")),
        Map.entry("LV", Pattern.compile("^LV[0-9]{11}$")),
        Map.entry("LT", Pattern.compile("^LT[0-9]{9,12}$")),
        Map.entry("LU", Pattern.compile("^LU[0-9]{8}$")),
        Map.entry("MT", Pattern.compile("^MT[0-9]{8}$")),
        Map.entry("NL", Pattern.compile("^NL[0-9]{9}B[0-9]{2}$")),
        Map.entry("PL", Pattern.compile("^PL[0-9]{10}$")),
        Map.entry("PT", Pattern.compile("^PT[0-9]{9}$")),
        Map.entry("RO", Pattern.compile("^RO[0-9]{2,10}$")),
        Map.entry("SK", Pattern.compile("^SK[0-9]{10}$")),
        Map.entry("SI", Pattern.compile("^SI[0-9]{8}$")),
        Map.entry("ES", Pattern.compile("^ES[A-Z0-9][0-9]{7}[A-Z0-9]$")),
        Map.entry("SE", Pattern.compile("^SE[0-9]{12}$"))
    );

    //~ ------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ------------------------------------------------------------------------------------------------

    @Nonnull
    private final VatValidationRepository vatValidationRepository;

    @Value("${vies.retry-attempts:3}")
    private int maxRetryAttempts;

    @Value("${vies.retry-delay:1000}")
    private long retryDelay;

    @Value("${vies.timeout:30000}")
    private int viesTimeout;

    @Value("${vies.wsdl-url:https://ec.europa.eu/taxation_customs/vies/checkVatService.wsdl}")
    @Nonnull
    private String viesWsdlUrl;

    //~ ------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ------------------------------------------------------------------------------------------------

    public ViesValidationService(@Nonnull VatValidationRepository vatValidationRepository) {
        this.vatValidationRepository = Objects.requireNonNull(vatValidationRepository, "vatValidationRepository must not be null");
    }

    //~ ------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ------------------------------------------------------------------------------------------------

    /**
     * Job planifié pour retenter les validations en échec
     */
    @Scheduled(fixedRate = 900000) // Toutes les 15 minutes
    @Transactional
    public void retryFailedValidations() {
        List<VatValidation> retryable = vatValidationRepository.findRetryableValidations(
            maxRetryAttempts, LocalDateTime.now());

        for (VatValidation validation : retryable) {
            try {
                log.info("Retry validation pour {}", maskVatNumber(validation.getVatNumber()));
                String countryCode = validation.getCountryCode();
                String vatNumberWithoutCountry = validation.getVatNumberWithoutCountry();

                ViesResponse response = executeViesRequest(countryCode, vatNumberWithoutCountry);

                if (response.isValid()) {
                    validation.setValidationStatus(ValidationStatus.VALID);
                    validation.setCompanyName(response.getCompanyName());
                    validation.setCompanyAddress(response.getCompanyAddress());
                } else {
                    validation.setValidationStatus(ValidationStatus.INVALID);
                }
                validation.setValidationDate(LocalDateTime.now());
                validation.setNextRetryAt(null);
                vatValidationRepository.save(validation);

            } catch (ViesUnavailableException e) {
                validation.setRetryCount(validation.getRetryCount() + 1);
                validation.setNextRetryAt(LocalDateTime.now().plusMinutes(30));
                vatValidationRepository.save(validation);
            } catch (Exception e) {
                log.error("Erreur retry validation: {}", e.getMessage());
                validation.setValidationStatus(ValidationStatus.ERROR);
                validation.setErrorMessage(e.getMessage());
                vatValidationRepository.save(validation);
            }
        }
    }

    /**
     * Valide un numéro de TVA intracommunautaire.
     * Cette méthode est le point d'entrée principal pour la validation.
     */
    @Transactional
    @Nonnull
    public VatValidationResult validateVatNumber(@Nonnull Shop shop,
                                                 @Nonnull String vatNumber,
                                                 @Nullable String shopifyOrderId) {
        log.info("Validation TVA demandée pour shop={}, vatNumber={}", shop.getShopifyDomain(), maskVatNumber(vatNumber));

        // Normalisation du numéro
        String normalizedVat = normalizeVatNumber(vatNumber);

        // Extraction du code pays
        String countryCode = extractCountryCode(normalizedVat);

        if (countryCode == null || !EU_COUNTRY_CODES.contains(countryCode)) {
            return createValidationResult(shop, normalizedVat, null, shopifyOrderId,
                ValidationStatus.FORMAT_ERROR, "Code pays non reconnu ou non-UE", null, null);
        }

        // Vérification du format
        if (!isValidFormat(countryCode, normalizedVat)) {
            return createValidationResult(shop, normalizedVat, countryCode, shopifyOrderId,
                ValidationStatus.FORMAT_ERROR, "Format du numéro de TVA incorrect", null, null);
        }

        // Vérifier si une validation récente existe (cache de 24h)
        Optional<VatValidation> recentValidation = findRecentValidation(normalizedVat);
        if (recentValidation.isPresent() && recentValidation.get().getValidationStatus() == ValidationStatus.VALID) {
            log.info("Validation en cache trouvée pour {}", maskVatNumber(normalizedVat));
            return toValidationResult(recentValidation.get());
        }

        // Appel au service VIES
        return callViesService(shop, normalizedVat, countryCode, shopifyOrderId);
    }

    /**
     * Validation asynchrone (ne bloque pas le checkout)
     */
    @Async
    public void validateVatNumberAsync(@Nonnull Shop shop,
                                       @Nonnull String vatNumber,
                                       @Nullable String shopifyOrderId) {
        try {
            validateVatNumber(shop, vatNumber, shopifyOrderId);
        } catch (Exception e) {
            log.error("Erreur validation async: {}", e.getMessage());
        }
    }

    /**
     * Appel au service VIES avec gestion des retries
     */
    @Nonnull
    private VatValidationResult callViesService(@Nonnull Shop shop,
                                                @Nonnull String vatNumber,
                                                @Nonnull String countryCode,
                                                @Nullable String shopifyOrderId) {
        String vatNumberWithoutCountry = vatNumber.substring(2);
        String requestId = UUID.randomUUID().toString();
        int attempts = 0;
        Exception lastException = null;

        while (attempts < maxRetryAttempts) {
            attempts++;
            try {
                ViesResponse response = executeViesRequest(countryCode, vatNumberWithoutCountry);

                if (response.isValid()) {
                    return createValidationResult(shop, vatNumber, countryCode, shopifyOrderId,
                        ValidationStatus.VALID, null, response, requestId);
                } else {
                    return createValidationResult(shop, vatNumber, countryCode, shopifyOrderId,
                        ValidationStatus.INVALID, "Numéro de TVA non enregistré dans VIES", response, requestId);
                }
            } catch (ViesUnavailableException e) {
                lastException = e;
                log.warn("VIES indisponible, tentative {}/{}", attempts, maxRetryAttempts);
                if (attempts < maxRetryAttempts) {
                    try {
                        Thread.sleep(retryDelay * attempts);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } catch (Exception e) {
                lastException = e;
                log.error("Erreur lors de l'appel VIES: {}", e.getMessage());
                break;
            }
        }

        // Échec après retries - stocker pour retry ultérieur
        return createValidationResult(shop, vatNumber, countryCode, shopifyOrderId,
            ValidationStatus.UNAVAILABLE, "Service de vérification temporairement indisponible", null, requestId);
    }

    /**
     * Crée et persiste le résultat de validation
     */
    @Transactional
    @Nonnull
    protected VatValidationResult createValidationResult(@Nonnull Shop shop,
                                                         @Nonnull String vatNumber,
                                                         @Nullable String countryCode,
                                                         @Nullable String shopifyOrderId,
                                                         @Nonnull ValidationStatus status,
                                                         @Nullable String errorMessage,
                                                         @Nullable ViesResponse viesResponse,
                                                         @Nullable String requestId) {
        VatValidation validation = VatValidation.builder()
            .shop(shop)
            .vatNumber(vatNumber)
            .countryCode(countryCode)
            .vatNumberWithoutCountry(countryCode != null ? vatNumber.substring(2) : null)
            .shopifyOrderId(shopifyOrderId)
            .validationStatus(status)
            .viesRequestId(requestId)
            .errorMessage(errorMessage)
            .validationDate(LocalDateTime.now())
            .retryCount(0)
            .build();

        if (viesResponse != null) {
            validation.setCompanyName(viesResponse.getCompanyName());
            validation.setCompanyAddress(viesResponse.getCompanyAddress());
            validation.setViesResponse(viesResponse.toString());
        }

        if (status == ValidationStatus.UNAVAILABLE) {
            validation.setNextRetryAt(LocalDateTime.now().plusMinutes(15));
        }

        VatValidation saved = vatValidationRepository.save(validation);
        log.info("Validation TVA enregistrée: status={}, vatNumber={}", status, maskVatNumber(vatNumber));

        return toValidationResult(saved);
    }

    /**
     * Exécute la requête SOAP vers VIES
     */
    @Nonnull
    private ViesResponse executeViesRequest(@Nonnull String countryCode,
                                            @Nonnull String vatNumber) throws Exception {
        // Adapter le code pays pour la Grèce (EL dans VIES)
        String viesCountryCode = "GR".equals(countryCode) ? "EL" : countryCode;

        try {
            // Création de la connexion SOAP
            SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = soapConnectionFactory.createConnection();

            try {
                // Création du message SOAP
                MessageFactory messageFactory = MessageFactory.newInstance();
                SOAPMessage soapMessage = messageFactory.createMessage();
                SOAPPart soapPart = soapMessage.getSOAPPart();

                // Construction de l'enveloppe
                SOAPEnvelope envelope = soapPart.getEnvelope();
                envelope.addNamespaceDeclaration("urn", "urn:ec.europa.eu:taxud:vies:services:checkVat:types");

                // Construction du body
                SOAPBody soapBody = envelope.getBody();
                SOAPElement checkVat = soapBody.addChildElement("checkVat", "urn");
                checkVat.addChildElement("countryCode", "urn").addTextNode(viesCountryCode);
                checkVat.addChildElement("vatNumber", "urn").addTextNode(vatNumber);

                soapMessage.saveChanges();

                // Envoi de la requête
                String endpoint = "https://ec.europa.eu/taxation_customs/vies/services/checkVatService";
                SOAPMessage response = connection.call(soapMessage, endpoint);

                // Parsing de la réponse
                return parseViesResponse(response);

            } finally {
                connection.close();
            }
        } catch (SOAPException e) {
            if (isViesUnavailable(e)) {
                throw new ViesUnavailableException("VIES service unavailable", e);
            }
            throw e;
        }
    }

    @Nullable
    private String extractCountryCode(@Nonnull String vatNumber) {
        if (vatNumber.length() < 2) {
            return null;
        }
        String prefix = vatNumber.substring(0, 2);
        // Cas spécial Grèce: EL -> GR
        if ("EL".equals(prefix)) {
            return "GR";
        }
        return EU_COUNTRY_CODES.contains(prefix) ? prefix : null;
    }

    @Nonnull
    private Optional<VatValidation> findRecentValidation(@Nonnull String vatNumber) {
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        return vatValidationRepository.findLatestByVatNumber(vatNumber,
            PageRequest.of(0, 1))
            .stream()
            .filter(v -> v.getValidationDate().isAfter(threshold))
            .findFirst();
    }

    private boolean isValidFormat(@Nonnull String countryCode,
                                  @Nonnull String vatNumber) {
        Pattern pattern = VAT_PATTERNS.get(countryCode);
        if (pattern == null) {
            return true; // Accepter si pas de pattern défini
        }
        return pattern.matcher(vatNumber).matches();
    }

    private boolean isViesUnavailable(@Nonnull SOAPException e) {
        String message = e.getMessage();
        return message != null && (
            message.contains("MS_UNAVAILABLE") ||
            message.contains("SERVICE_UNAVAILABLE") ||
            message.contains("timeout") ||
            message.contains("Connection refused")
        );
    }

    @Nonnull
    private String maskVatNumber(@Nullable String vatNumber) {
        if (vatNumber == null || vatNumber.length() < 6) {
            return "***";
        }
        return vatNumber.substring(0, 4) + "***" + vatNumber.substring(vatNumber.length() - 2);
    }

    @Nonnull
    private String normalizeVatNumber(@Nullable String vatNumber) {
        if (vatNumber == null) {
            return "";
        }
        return vatNumber.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }

    /**
     * Parse la réponse VIES
     */
    @Nonnull
    private ViesResponse parseViesResponse(@Nonnull SOAPMessage response) throws SOAPException {
        SOAPBody responseBody = response.getSOAPBody();

        if (responseBody.hasFault()) {
            String faultString = responseBody.getFault().getFaultString();
            if (faultString.contains("MS_UNAVAILABLE") || faultString.contains("SERVICE_UNAVAILABLE")) {
                throw new ViesUnavailableException("VIES member state unavailable");
            }
            throw new RuntimeException("VIES fault: " + faultString);
        }

        ViesResponse viesResponse = new ViesResponse();

        NodeList nodeList = responseBody.getElementsByTagName("*");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            String nodeName = node.getLocalName();
            String nodeValue = node.getTextContent();

            if (nodeName != null) {
                switch (nodeName) {
                    case "valid" -> viesResponse.setValid("true".equals(nodeValue));
                    case "name" -> viesResponse.setCompanyName(nodeValue);
                    case "address" -> viesResponse.setCompanyAddress(nodeValue);
                    case "requestDate" -> viesResponse.setRequestDate(nodeValue);
                }
            }
        }

        return viesResponse;
    }

    /**
     * Convertit l'entité en DTO résultat
     */
    @Nonnull
    private VatValidationResult toValidationResult(@Nonnull VatValidation validation) {
        return VatValidationResult.builder()
            .id(validation.getId())
            .vatNumber(validation.getVatNumber())
            .countryCode(validation.getCountryCode())
            .status(validation.getValidationStatus())
            .companyName(validation.getCompanyName())
            .companyAddress(validation.getCompanyAddress())
            .validationDate(validation.getValidationDate())
            .userMessage(validation.getUserFriendlyMessage())
            .isLegalProof(validation.isLegalProof())
            .requestId(validation.getViesRequestId())
            .build();
    }

    //~ ------------------------------------------------------------------------------------------------
    //~ Nested Classes
    //~ ------------------------------------------------------------------------------------------------

    private static class ViesResponse {

        private String companyAddress;
        private String companyName;
        private String requestDate;
        private boolean valid;

        @Nullable
        public String getCompanyAddress() {
            return companyAddress;
        }

        public void setCompanyAddress(@Nullable String companyAddress) {
            this.companyAddress = companyAddress;
        }

        @Nullable
        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(@Nullable String companyName) {
            this.companyName = companyName;
        }

        @Nullable
        public String getRequestDate() {
            return requestDate;
        }

        public void setRequestDate(@Nullable String requestDate) {
            this.requestDate = requestDate;
        }

        public boolean isValid() {
            return valid;
        }

        public void setValid(boolean valid) {
            this.valid = valid;
        }

        @Override
        @Nonnull
        public String toString() {
            return String.format("ViesResponse{valid=%s, name='%s', address='%s', date='%s'}",
                valid, companyName, companyAddress, requestDate);
        }
    }

    private static class ViesUnavailableException extends RuntimeException {

        public ViesUnavailableException(@Nonnull String message) {
            super(message);
        }

        public ViesUnavailableException(@Nonnull String message,
                                        @Nonnull Throwable cause) {
            super(message, cause);
        }
    }

}
