package com.euvatease.controller;

import jakarta.persistence.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Contrôleur pour l'accès anticipé (landing page)
 */
@RestController
@RequestMapping("/early-access")
public class EarlyAccessController {

    private final EarlyAccessSignupRepository signupRepository;

    public EarlyAccessController(EarlyAccessSignupRepository signupRepository) {
        this.signupRepository = signupRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody EarlyAccessSignupRequest request) {
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

    @GetMapping("/count")
    public ResponseEntity<?> getSignupCount() {
        long count = signupRepository.count();
        return ResponseEntity.ok(Map.of("count", count));
    }
}

class EarlyAccessSignupRequest {
    private String email;
    private String shopUrl;
    private String country;
    private String source;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;

    public EarlyAccessSignupRequest() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getShopUrl() { return shopUrl; }
    public void setShopUrl(String shopUrl) { this.shopUrl = shopUrl; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public String getUtmSource() { return utmSource; }
    public void setUtmSource(String utmSource) { this.utmSource = utmSource; }
    public String getUtmMedium() { return utmMedium; }
    public void setUtmMedium(String utmMedium) { this.utmMedium = utmMedium; }
    public String getUtmCampaign() { return utmCampaign; }
    public void setUtmCampaign(String utmCampaign) { this.utmCampaign = utmCampaign; }
}

@Entity
@Table(name = "early_access_signups")
class EarlyAccessSignup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String shopUrl;
    private String country;
    private String source;
    private String utmSource;
    private String utmMedium;
    private String utmCampaign;
    private Boolean subscribed = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public EarlyAccessSignup() {}

    public EarlyAccessSignup(Long id, String email, String shopUrl, String country, String source,
                             String utmSource, String utmMedium, String utmCampaign, Boolean subscribed,
                             LocalDateTime createdAt) {
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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getShopUrl() { return shopUrl; }
    public String getCountry() { return country; }
    public String getSource() { return source; }
    public String getUtmSource() { return utmSource; }
    public String getUtmMedium() { return utmMedium; }
    public String getUtmCampaign() { return utmCampaign; }
    public Boolean getSubscribed() { return subscribed; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setShopUrl(String shopUrl) { this.shopUrl = shopUrl; }
    public void setCountry(String country) { this.country = country; }
    public void setSource(String source) { this.source = source; }
    public void setUtmSource(String utmSource) { this.utmSource = utmSource; }
    public void setUtmMedium(String utmMedium) { this.utmMedium = utmMedium; }
    public void setUtmCampaign(String utmCampaign) { this.utmCampaign = utmCampaign; }
    public void setSubscribed(Boolean subscribed) { this.subscribed = subscribed; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    // Builder
    public static EarlyAccessSignupBuilder builder() { return new EarlyAccessSignupBuilder(); }

    public static class EarlyAccessSignupBuilder {
        private Long id;
        private String email;
        private String shopUrl;
        private String country;
        private String source;
        private String utmSource;
        private String utmMedium;
        private String utmCampaign;
        private Boolean subscribed = true;
        private LocalDateTime createdAt;

        public EarlyAccessSignupBuilder id(Long id) { this.id = id; return this; }
        public EarlyAccessSignupBuilder email(String email) { this.email = email; return this; }
        public EarlyAccessSignupBuilder shopUrl(String shopUrl) { this.shopUrl = shopUrl; return this; }
        public EarlyAccessSignupBuilder country(String country) { this.country = country; return this; }
        public EarlyAccessSignupBuilder source(String source) { this.source = source; return this; }
        public EarlyAccessSignupBuilder utmSource(String utmSource) { this.utmSource = utmSource; return this; }
        public EarlyAccessSignupBuilder utmMedium(String utmMedium) { this.utmMedium = utmMedium; return this; }
        public EarlyAccessSignupBuilder utmCampaign(String utmCampaign) { this.utmCampaign = utmCampaign; return this; }
        public EarlyAccessSignupBuilder subscribed(Boolean subscribed) { this.subscribed = subscribed; return this; }
        public EarlyAccessSignupBuilder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public EarlyAccessSignup build() {
            return new EarlyAccessSignup(id, email, shopUrl, country, source, utmSource, utmMedium,
                    utmCampaign, subscribed, createdAt);
        }
    }
}

interface EarlyAccessSignupRepository extends org.springframework.data.jpa.repository.JpaRepository<EarlyAccessSignup, Long> {
    boolean existsByEmail(String email);
}
