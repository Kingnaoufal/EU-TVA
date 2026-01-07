package com.euvatease.service;

import com.euvatease.entity.Shop;
import com.euvatease.entity.VatAlert;
import com.euvatease.repository.ShopRepository;
import com.euvatease.repository.VatAlertRepository;
import jakarta.annotation.Nonnull;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * Service d'envoi d'emails et de notifications
 */
@Service
public class EmailService {

    //~ ------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    //~ ------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ------------------------------------------------------------------------------------------------

    @Nonnull
    private final JavaMailSender mailSender;

    @Nonnull
    private final ShopRepository shopRepository;

    @Nonnull
    private final TemplateEngine templateEngine;

    @Nonnull
    private final VatAlertRepository vatAlertRepository;

    @Value("${spring.mail.username:noreply@euvatease.com}")
    @Nonnull
    private String fromEmail;

    @Value("${app.frontend-url}")
    @Nonnull
    private String frontendUrl;

    //~ ------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ------------------------------------------------------------------------------------------------

    public EmailService(@Nonnull JavaMailSender mailSender,
                        @Nonnull TemplateEngine templateEngine,
                        @Nonnull VatAlertRepository vatAlertRepository,
                        @Nonnull ShopRepository shopRepository) {
        this.mailSender = Objects.requireNonNull(mailSender, "mailSender must not be null");
        this.templateEngine = Objects.requireNonNull(templateEngine, "templateEngine must not be null");
        this.vatAlertRepository = Objects.requireNonNull(vatAlertRepository, "vatAlertRepository must not be null");
        this.shopRepository = Objects.requireNonNull(shopRepository, "shopRepository must not be null");
    }

    //~ ------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ------------------------------------------------------------------------------------------------

    /**
     * Envoie un email d'alerte
     */
    @Async
    public void sendAlertEmail(@Nonnull Shop shop,
                               @Nonnull VatAlert alert) {
        if (!Boolean.TRUE.equals(shop.getAlertEmailEnabled())) {
            return;
        }

        try {
            Context context = new Context();
            context.setVariable("shopName", shop.getShopName());
            context.setVariable("alertTitle", alert.getTitle());
            context.setVariable("alertMessage", alert.getMessage());
            context.setVariable("actionRequired", alert.getActionRequired());
            context.setVariable("severity", alert.getSeverity().name());
            context.setVariable("dashboardUrl", frontendUrl + "/dashboard");

            String htmlContent = templateEngine.process("email/alert", context);

            sendEmail(shop.getEmail(), getAlertSubject(alert), htmlContent);

            alert.setEmailSent(true);
            alert.setEmailSentAt(LocalDateTime.now());
            vatAlertRepository.save(alert);

            log.info("Email d'alerte envoyé à {} pour {}", shop.getEmail(), alert.getAlertType());

        } catch (Exception e) {
            log.error("Erreur envoi email alerte: {}", e.getMessage());
        }
    }

    /**
     * Envoie un rappel de déclaration OSS
     */
    @Async
    public void sendOssReminderEmail(@Nonnull Shop shop,
                                     int quarter,
                                     int year,
                                     int daysRemaining) {
        try {
            Context context = new Context();
            context.setVariable("shopName", shop.getShopName());
            context.setVariable("quarter", quarter);
            context.setVariable("year", year);
            context.setVariable("daysRemaining", daysRemaining);
            context.setVariable("reportUrl", frontendUrl + "/reports");

            String htmlContent = templateEngine.process("email/oss-reminder", context);

            sendEmail(shop.getEmail(),
                String.format("Rappel: Déclaration OSS T%d %d dans %d jours", quarter, year, daysRemaining),
                htmlContent);

            log.info("Rappel OSS envoyé à {}", shop.getEmail());

        } catch (Exception e) {
            log.error("Erreur envoi rappel OSS: {}", e.getMessage());
        }
    }

    /**
     * Job planifié pour les rappels de déclaration OSS
     */
    @Scheduled(cron = "0 0 9 * * *") // Tous les jours à 9h
    public void sendOssReminders() {
        LocalDateTime now = LocalDateTime.now();
        int currentQuarter = (now.getMonthValue() - 1) / 3 + 1;
        int deadlineMonth = currentQuarter * 3 + 1;
        int deadlineYear = now.getYear();
        if (deadlineMonth > 12) {
            deadlineMonth = 1;
            deadlineYear++;
        }

        LocalDateTime deadline = LocalDateTime.of(deadlineYear, deadlineMonth, 1, 0, 0)
            .plusMonths(1).minusDays(1);

        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(now, deadline);

        // Envoyer des rappels à J-14, J-7, J-3, J-1
        if (daysRemaining == 14 || daysRemaining == 7 || daysRemaining == 3 || daysRemaining == 1) {
            List<Shop> activeShops = shopRepository.findActiveSubscriptions();
            for (Shop shop : activeShops) {
                if (Boolean.TRUE.equals(shop.getOssRegistered()) && Boolean.TRUE.equals(shop.getAlertEmailEnabled())) {
                    sendOssReminderEmail(shop, currentQuarter, now.getYear(), (int) daysRemaining);
                }
            }
        }
    }

    /**
     * Job planifié pour envoyer les alertes en attente
     */
    @Scheduled(fixedRate = 300000) // Toutes les 5 minutes
    @Transactional(readOnly = true)
    public void sendPendingAlerts() {
        List<VatAlert> pendingAlerts = vatAlertRepository.findAlertsToSend();

        for (VatAlert alert : pendingAlerts) {
            // Le Shop est déjà chargé grâce au JOIN FETCH
            Shop shop = alert.getShop();
            sendAlertEmail(shop, alert);
        }
    }

    /**
     * Envoie un email de bienvenue
     */
    @Async
    public void sendWelcomeEmail(@Nonnull Shop shop) {
        try {
            Context context = new Context();
            context.setVariable("shopName", shop.getShopName());
            context.setVariable("trialEndDate", shop.getTrialEndsAt());
            context.setVariable("dashboardUrl", frontendUrl + "/dashboard");

            String htmlContent = templateEngine.process("email/welcome", context);

            sendEmail(shop.getEmail(), "Bienvenue sur EU VAT Ease !", htmlContent);

            log.info("Email de bienvenue envoyé à {}", shop.getEmail());

        } catch (Exception e) {
            log.error("Erreur envoi email bienvenue: {}", e.getMessage());
        }
    }

    @Nonnull
    private String getAlertSubject(@Nonnull VatAlert alert) {
        return switch (alert.getSeverity()) {
            case CRITICAL -> "🚨 ACTION URGENTE: " + alert.getTitle();
            case ERROR -> "❌ Attention: " + alert.getTitle();
            case WARNING -> "⚠️ " + alert.getTitle();
            case INFO -> "ℹ️ " + alert.getTitle();
        };
    }

    private void sendEmail(@Nonnull String to,
                           @Nonnull String subject,
                           @Nonnull String htmlContent) throws Exception {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail, "EU VAT Ease");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
