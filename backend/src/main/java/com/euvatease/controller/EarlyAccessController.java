package com.euvatease.controller;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Contrôleur pour l'accès anticipé (landing page)
 */
@RestController
@RequestMapping("/early-access")
public class EarlyAccessController {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    private final EarlyAccessSignupRepository signupRepository;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public EarlyAccessController(@Nonnull EarlyAccessSignupRepository signupRepository) {
        this.signupRepository = Objects.requireNonNull(signupRepository, "signupRepository must not be null");
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    @GetMapping("/count")
    public ResponseEntity<?> getSignupCount() {
        long count = signupRepository.count();
        return ResponseEntity.ok(Map.of("count", count));
    }

    @Nonnull
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Nonnull @RequestBody EarlyAccessSignupRequest request) {
        // Vérifier si l'email existe déjà
        if (signupRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Vous êtes déjà inscrit ! Nous vous contacterons bientôt."
            ));
        }

        EarlyAccessSignup signup = EarlyAccessSignup.builder()
            .email(request.getEmail())
            .shopUrl(request.getShopUrl())
            .country(request.getCountry())
            .source(request.getSource())
            .utmSource(request.getUtmSource())
            .utmMedium(request.getUtmMedium())
            .utmCampaign(request.getUtmCampaign())
            .build();

        signupRepository.save(signup);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "message", "Merci ! Vous serez parmi les premiers à accéder à EU VAT Ease. " +
                       "Surveillez votre boîte mail pour des offres exclusives early adopters."
        ));
    }
}

class EarlyAccessSignupRequest {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nullable
    private String country;

    @Nullable
    private String email;

    @Nullable
    private String shopUrl;

    @Nullable
    private String source;

    @Nullable
    private String utmCampaign;

    @Nullable
    private String utmMedium;

    @Nullable
    private String utmSource;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public EarlyAccessSignupRequest() {}

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nullable
    public String getCountry() { return country; }

    @Nullable
    public String getEmail() { return email; }

    @Nullable
    public String getShopUrl() { return shopUrl; }

    @Nullable
    public String getSource() { return source; }

    @Nullable
    public String getUtmCampaign() { return utmCampaign; }

    @Nullable
    public String getUtmMedium() { return utmMedium; }

    @Nullable
    public String getUtmSource() { return utmSource; }

    public void setCountry(@Nullable String country) { this.country = country; }

    public void setEmail(@Nullable String email) { this.email = email; }

    public void setShopUrl(@Nullable String shopUrl) { this.shopUrl = shopUrl; }

    public void setSource(@Nullable String source) { this.source = source; }

    public void setUtmCampaign(@Nullable String utmCampaign) { this.utmCampaign = utmCampaign; }

    public void setUtmMedium(@Nullable String utmMedium) { this.utmMedium = utmMedium; }

    public void setUtmSource(@Nullable String utmSource) { this.utmSource = utmSource; }
}

@Entity
@Table(name = "early_access_signups")
class EarlyAccessSignup {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nullable
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Nullable
    private String country;

    @Nullable
    @Column(unique = true, nullable = false)
    private String email;

    @Nullable
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Nullable
    private String shopUrl;

    @Nullable
    private String source;

    @Nullable
    private Boolean subscribed = true;

    @Nullable
    private String utmCampaign;

    @Nullable
    private String utmMedium;

    @Nullable
    private String utmSource;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public EarlyAccessSignup() {}

    public EarlyAccessSignup(@Nullable Long id,
                             @Nullable String email,
                             @Nullable String shopUrl,
                             @Nullable String country,
                             @Nullable String source,
                             @Nullable String utmSource,
                             @Nullable String utmMedium,
                             @Nullable String utmCampaign,
                             @Nullable Boolean subscribed,
                             @Nullable LocalDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.shopUrl = shopUrl;
        this.country = country;
        this.source = source;
        this.utmSource = utmSource;
        this.utmMedium = utmMedium;
        this.utmCampaign = utmCampaign;
        this.subscribed = subscribed;
        this.createdAt = createdAt;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    public static EarlyAccessSignupBuilder builder() { return new EarlyAccessSignupBuilder(); }

    @Nullable
    public String getCountry() { return country; }

    @Nullable
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Nullable
    public String getEmail() { return email; }

    @Nullable
    public Long getId() { return id; }

    @Nullable
    public String getShopUrl() { return shopUrl; }

    @Nullable
    public String getSource() { return source; }

    @Nullable
    public Boolean getSubscribed() { return subscribed; }

    @Nullable
    public String getUtmCampaign() { return utmCampaign; }

    @Nullable
    public String getUtmMedium() { return utmMedium; }

    @Nullable
    public String getUtmSource() { return utmSource; }

    public void setCountry(@Nullable String country) { this.country = country; }

    public void setCreatedAt(@Nullable LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void setEmail(@Nullable String email) { this.email = email; }

    public void setId(@Nullable Long id) { this.id = id; }

    public void setShopUrl(@Nullable String shopUrl) { this.shopUrl = shopUrl; }

    public void setSource(@Nullable String source) { this.source = source; }

    public void setSubscribed(@Nullable Boolean subscribed) { this.subscribed = subscribed; }

    public void setUtmCampaign(@Nullable String utmCampaign) { this.utmCampaign = utmCampaign; }

    public void setUtmMedium(@Nullable String utmMedium) { this.utmMedium = utmMedium; }

    public void setUtmSource(@Nullable String utmSource) { this.utmSource = utmSource; }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Nested Classes
    //~ ----------------------------------------------------------------------------------------------------------------

    public static class EarlyAccessSignupBuilder {

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Instance fields
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nullable
        private String country;

        @Nullable
        private LocalDateTime createdAt;

        @Nullable
        private String email;

        @Nullable
        private Long id;

        @Nullable
        private String shopUrl;

        @Nullable
        private String source;

        @Nullable
        private Boolean subscribed = true;

        @Nullable
        private String utmCampaign;

        @Nullable
        private String utmMedium;

        @Nullable
        private String utmSource;

        //~ ----------------------------------------------------------------------------------------------------------------
        //~ Methods
        //~ ----------------------------------------------------------------------------------------------------------------

        @Nonnull
        public EarlyAccessSignup build() {
            return new EarlyAccessSignup(id, email, shopUrl, country, source, utmSource, utmMedium,
                    utmCampaign, subscribed, createdAt);
        }

        @Nonnull
        public EarlyAccessSignupBuilder country(@Nullable String country) { this.country = country; return this; }

        @Nonnull
        public EarlyAccessSignupBuilder createdAt(@Nullable LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        @Nonnull
        public EarlyAccessSignupBuilder email(@Nullable String email) { this.email = email; return this; }

        @Nonnull
        public EarlyAccessSignupBuilder id(@Nullable Long id) { this.id = id; return this; }

        @Nonnull
        public EarlyAccessSignupBuilder shopUrl(@Nullable String shopUrl) { this.shopUrl = shopUrl; return this; }

        @Nonnull
        public EarlyAccessSignupBuilder source(@Nullable String source) { this.source = source; return this; }

        @Nonnull
        public EarlyAccessSignupBuilder subscribed(@Nullable Boolean subscribed) { this.subscribed = subscribed; return this; }

        @Nonnull
        public EarlyAccessSignupBuilder utmCampaign(@Nullable String utmCampaign) { this.utmCampaign = utmCampaign; return this; }

        @Nonnull
        public EarlyAccessSignupBuilder utmMedium(@Nullable String utmMedium) { this.utmMedium = utmMedium; return this; }

        @Nonnull
        public EarlyAccessSignupBuilder utmSource(@Nullable String utmSource) { this.utmSource = utmSource; return this; }
    }
}

interface EarlyAccessSignupRepository extends org.springframework.data.jpa.repository.JpaRepository<EarlyAccessSignup, Long> {
    boolean existsByEmail(@Nonnull String email);
}
