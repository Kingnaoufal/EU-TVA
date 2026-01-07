package com.euvatease.service;

import com.euvatease.entity.*;
import com.euvatease.repository.*;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service de génération des rapports OSS trimestriels.
 * Génère des rapports conformes au format OSS de l'UE.
 */
@Service
public class OssReportService {

    private static final Logger log = LoggerFactory.getLogger(OssReportService.class);

    private final OssReportRepository ossReportRepository;
    private final OssReportLineRepository ossReportLineRepository;
    private final OrderRepository orderRepository;
    private final EuVatRateRepository euVatRateRepository;
    private final AuditLogService auditLogService;

    public OssReportService(OssReportRepository ossReportRepository, OssReportLineRepository ossReportLineRepository,
                            OrderRepository orderRepository, EuVatRateRepository euVatRateRepository,
                            AuditLogService auditLogService) {
        this.ossReportRepository = ossReportRepository;
        this.ossReportLineRepository = ossReportLineRepository;
        this.orderRepository = orderRepository;
        this.euVatRateRepository = euVatRateRepository;
        this.auditLogService = auditLogService;
    }

    @Value("${app.frontend-url:https://app.euvatease.com}")
    private String frontendUrl;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Génère un rapport OSS pour un trimestre donné
     */
    @Transactional
    public OssReport generateReport(Shop shop, int year, int quarter) {
        log.info("Génération rapport OSS: shop={}, période=T{} {}", shop.getShopifyDomain(), quarter, year);

        // Vérifier si un rapport existe déjà
        Optional<OssReport> existing = ossReportRepository.findByShopAndYearAndQuarter(shop, year, quarter);
        if (existing.isPresent() && existing.get().getStatus() != OssReport.ReportStatus.DRAFT) {
            log.warn("Un rapport OSS existe déjà pour cette période");
            return existing.get();
        }

        // Calculer les dates du trimestre
        LocalDateTime periodStart = getQuarterStart(year, quarter);
        LocalDateTime periodEnd = getQuarterEnd(year, quarter);

        // Récupérer les commandes de la période
        List<Order> orders = orderRepository.findOrdersForOssReport(shop, periodStart, periodEnd);
        
        // Filtrer uniquement les commandes UE B2C (OSS ne concerne pas B2B exonéré)
        List<Order> ossOrders = orders.stream()
            .filter(o -> isOssEligible(o, shop))
            .collect(Collectors.toList());

        // Créer ou mettre à jour le rapport
        OssReport report = existing.orElse(OssReport.builder()
            .shop(shop)
            .year(year)
            .quarter(quarter)
            .build());

        report.setPeriodStart(periodStart);
        report.setPeriodEnd(periodEnd);
        report.setGeneratedAt(LocalDateTime.now());
        report.setStatus(OssReport.ReportStatus.GENERATED);

        // Calculer les totaux
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalVat = BigDecimal.ZERO;
        int totalOrders = ossOrders.size();
        int b2bOrders = 0;
        int b2cOrders = 0;
        int exemptOrders = 0;

        // Grouper par pays et taux de TVA
        Map<String, Map<BigDecimal, List<Order>>> ordersByCountryAndRate = new HashMap<>();

        for (Order order : ossOrders) {
            String country = order.getCustomerCountryCode();
            BigDecimal vatRate = order.getExpectedVatRate() != null ? order.getExpectedVatRate() : BigDecimal.ZERO;

            ordersByCountryAndRate
                .computeIfAbsent(country, k -> new HashMap<>())
                .computeIfAbsent(vatRate, k -> new ArrayList<>())
                .add(order);

            totalSales = totalSales.add(order.getSubtotalAmount() != null ? order.getSubtotalAmount() : BigDecimal.ZERO);
            totalVat = totalVat.add(order.getCalculatedVatAmount() != null ? order.getCalculatedVatAmount() : BigDecimal.ZERO);

            if (Boolean.TRUE.equals(order.getIsB2b())) b2bOrders++;
            else b2cOrders++;
            
            if (Boolean.TRUE.equals(order.getVatExempt())) exemptOrders++;
        }

        report.setTotalSales(totalSales);
        report.setTotalVat(totalVat);
        report.setTotalOrders(totalOrders);
        report.setB2bOrders(b2bOrders);
        report.setB2cOrders(b2cOrders);
        report.setExemptOrders(exemptOrders);
        report.setCountriesCount(ordersByCountryAndRate.size());

        // Sauvegarder le rapport
        report = ossReportRepository.save(report);

        // Supprimer les anciennes lignes si mise à jour
        ossReportLineRepository.deleteByReport(report);

        // Créer les lignes du rapport
        createReportLines(report, ordersByCountryAndRate);

        // Marquer les commandes comme incluses dans le rapport
        final Long reportId = report.getId();
        ossOrders.forEach(o -> {
            o.setIncludedInOssReport(true);
            o.setOssReportId(reportId);
        });
        orderRepository.saveAll(ossOrders);

        // Audit log
        auditLogService.log(shop, AuditLog.ActionType.REPORT_GENERATED, "OssReport", report.getId(),
            String.format("Rapport OSS T%d %d généré: %d commandes, %.2f€ TVA", quarter, year, totalOrders, totalVat));

        log.info("Rapport OSS généré: id={}, commandes={}, TVA={}", report.getId(), totalOrders, totalVat);
        return report;
    }

    /**
     * Génère le CSV du rapport
     */
    public String generateCsv(OssReport report) {
        List<OssReportLine> lines = ossReportLineRepository.findByReportOrderByCountryCodeAsc(report);
        
        StringWriter stringWriter = new StringWriter();
        try (CSVWriter csvWriter = new CSVWriter(stringWriter)) {
            // En-tête
            csvWriter.writeNext(new String[]{
                "Code Pays", "Pays", "Taux TVA (%)", "Base imposable (€)", 
                "TVA due (€)", "Nombre de ventes"
            });

            // Données
            for (OssReportLine line : lines) {
                csvWriter.writeNext(new String[]{
                    line.getCountryCode(),
                    line.getCountryName(),
                    line.getVatRate().toString(),
                    line.getTaxableAmount().setScale(2, RoundingMode.HALF_UP).toString(),
                    line.getVatAmount().setScale(2, RoundingMode.HALF_UP).toString(),
                    String.valueOf(line.getOrdersCount())
                });
            }

            // Totaux
            csvWriter.writeNext(new String[]{});
            csvWriter.writeNext(new String[]{
                "TOTAL", "", "",
                report.getTotalSales().setScale(2, RoundingMode.HALF_UP).toString(),
                report.getTotalVat().setScale(2, RoundingMode.HALF_UP).toString(),
                String.valueOf(report.getTotalOrders())
            });

            // Métadonnées
            csvWriter.writeNext(new String[]{});
            csvWriter.writeNext(new String[]{"Période", report.getQuarterLabel()});
            csvWriter.writeNext(new String[]{"Généré le", report.getGeneratedAt().format(DATETIME_FORMATTER)});
            csvWriter.writeNext(new String[]{"Boutique", report.getShop().getShopName()});
            csvWriter.writeNext(new String[]{"N° TVA", report.getShop().getVatNumber() != null ? report.getShop().getVatNumber() : "Non renseigné"});

        } catch (Exception e) {
            log.error("Erreur génération CSV: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la génération du CSV", e);
        }

        return stringWriter.toString();
    }

    /**
     * Génère le PDF du rapport
     */
    public byte[] generatePdf(OssReport report) {
        List<OssReportLine> lines = ossReportLineRepository.findByReportOrderByCountryCodeAsc(report);
        
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Titre
            document.add(new Paragraph("RAPPORT OSS - DÉCLARATION TVA UE")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph(report.getQuarterLabel())
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER));

            document.add(new Paragraph("\n"));

            // Informations boutique
            document.add(new Paragraph("Informations déclarant")
                .setFontSize(12)
                .setBold());
            
            document.add(new Paragraph(String.format("Boutique: %s", report.getShop().getShopName())));
            document.add(new Paragraph(String.format("N° TVA: %s", 
                report.getShop().getVatNumber() != null ? report.getShop().getVatNumber() : "Non renseigné")));
            document.add(new Paragraph(String.format("Pays d'établissement: %s", report.getShop().getCountryCode())));
            document.add(new Paragraph(String.format("Période: %s - %s", 
                report.getPeriodStart().format(DATE_FORMATTER),
                report.getPeriodEnd().format(DATE_FORMATTER))));

            document.add(new Paragraph("\n"));

            // Tableau des ventes par pays
            document.add(new Paragraph("Détail par pays de consommation")
                .setFontSize(12)
                .setBold());

            Table table = new Table(UnitValue.createPercentArray(new float[]{15, 25, 15, 20, 20, 15}));
            table.setWidth(UnitValue.createPercentValue(100));

            // En-tête tableau
            String[] headers = {"Code", "Pays", "Taux TVA", "Base HT (€)", "TVA (€)", "Ventes"};
            for (String header : headers) {
                table.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setBold())
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            // Lignes
            for (OssReportLine line : lines) {
                table.addCell(line.getCountryCode());
                table.addCell(line.getCountryName());
                table.addCell(line.getVatRate() + "%");
                table.addCell(formatCurrency(line.getTaxableAmount()));
                table.addCell(formatCurrency(line.getVatAmount()));
                table.addCell(String.valueOf(line.getOrdersCount()));
            }

            // Total
            table.addCell(new Cell().add(new Paragraph("TOTAL").setBold()));
            table.addCell("");
            table.addCell("");
            table.addCell(new Cell().add(new Paragraph(formatCurrency(report.getTotalSales())).setBold()));
            table.addCell(new Cell().add(new Paragraph(formatCurrency(report.getTotalVat())).setBold()));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(report.getTotalOrders())).setBold()));

            document.add(table);

            document.add(new Paragraph("\n"));

            // Résumé
            document.add(new Paragraph("Résumé")
                .setFontSize(12)
                .setBold());
            
            document.add(new Paragraph(String.format("Nombre total de ventes: %d", report.getTotalOrders())));
            document.add(new Paragraph(String.format("  - Ventes B2C: %d", report.getB2cOrders())));
            document.add(new Paragraph(String.format("  - Ventes B2B (exonérées): %d", report.getB2bOrders())));
            document.add(new Paragraph(String.format("Nombre de pays concernés: %d", report.getCountriesCount())));
            document.add(new Paragraph(String.format("TVA totale due: %s €", formatCurrency(report.getTotalVat()))));

            document.add(new Paragraph("\n"));

            // Pied de page
            document.add(new Paragraph(String.format("Document généré le %s par EU VAT Ease", 
                report.getGeneratedAt().format(DATETIME_FORMATTER)))
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER));
            
            document.add(new Paragraph("Ce document est fourni à titre informatif. " +
                "Vérifiez auprès de votre administration fiscale pour la déclaration officielle.")
                .setFontSize(8)
                .setTextAlignment(TextAlignment.CENTER));

            document.close();
            return baos.toByteArray();

        } catch (Exception e) {
            log.error("Erreur génération PDF: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la génération du PDF", e);
        }
    }

    /**
     * Marque un rapport comme téléchargé
     */
    @Transactional
    public OssReport markAsDownloaded(OssReport report) {
        report.setStatus(OssReport.ReportStatus.DOWNLOADED);
        report.setDownloadedAt(LocalDateTime.now());
        
        auditLogService.log(report.getShop(), AuditLog.ActionType.REPORT_DOWNLOADED, 
            "OssReport", report.getId(), "Rapport OSS téléchargé");
        
        return ossReportRepository.save(report);
    }

    /**
     * Marque un rapport comme soumis
     */
    @Transactional
    public OssReport markAsSubmitted(OssReport report, String notes) {
        report.setStatus(OssReport.ReportStatus.SUBMITTED);
        report.setSubmittedAt(LocalDateTime.now());
        report.setNotes(notes);
        
        auditLogService.log(report.getShop(), AuditLog.ActionType.REPORT_SUBMITTED, 
            "OssReport", report.getId(), "Rapport OSS marqué comme soumis");
        
        return ossReportRepository.save(report);
    }

    /**
     * Obtient l'historique des rapports d'une boutique
     */
    public List<OssReport> getReportHistory(Shop shop) {
        return ossReportRepository.findByShopOrderByYearDescQuarterDesc(shop, 
            org.springframework.data.domain.PageRequest.of(0, 20)).getContent();
    }

    /**
     * Calcule les dates de rappel pour les déclarations
     */
    public Map<String, Object> getNextDeadline() {
        int currentQuarter = (LocalDateTime.now().getMonthValue() - 1) / 3 + 1;
        int currentYear = LocalDateTime.now().getYear();
        
        // La deadline est le dernier jour du mois suivant la fin du trimestre
        int deadlineMonth = currentQuarter * 3 + 1;
        int deadlineYear = currentYear;
        if (deadlineMonth > 12) {
            deadlineMonth = 1;
            deadlineYear++;
        }
        
        LocalDateTime deadline = LocalDateTime.of(deadlineYear, deadlineMonth, 1, 0, 0)
            .plusMonths(1).minusDays(1);
        
        Map<String, Object> result = new HashMap<>();
        result.put("quarter", currentQuarter);
        result.put("year", currentYear);
        result.put("deadline", deadline);
        result.put("daysRemaining", java.time.temporal.ChronoUnit.DAYS.between(LocalDateTime.now(), deadline));
        
        return result;
    }

    // Méthodes privées

    private LocalDateTime getQuarterStart(int year, int quarter) {
        int startMonth = (quarter - 1) * 3 + 1;
        return LocalDateTime.of(year, startMonth, 1, 0, 0, 0);
    }

    private LocalDateTime getQuarterEnd(int year, int quarter) {
        int endMonth = quarter * 3;
        LocalDateTime endOfMonth = LocalDateTime.of(year, endMonth, 1, 23, 59, 59);
        return endOfMonth.withDayOfMonth(endOfMonth.toLocalDate().lengthOfMonth());
    }

    private boolean isOssEligible(Order order, Shop shop) {
        // OSS concerne les ventes B2C UE vers d'autres pays que le pays d'établissement
        String customerCountry = order.getCustomerCountryCode();
        String shopCountry = shop.getCountryCode();
        
        if (customerCountry == null || shopCountry == null) return false;
        
        // Même pays = pas OSS
        if (customerCountry.equals(shopCountry)) return false;
        
        // B2B exonéré = pas OSS (autoliquidation)
        if (Boolean.TRUE.equals(order.getIsB2b()) && Boolean.TRUE.equals(order.getVatExempt())) {
            return false;
        }
        
        // Hors UE = pas OSS
        Set<String> euCountries = Set.of(
            "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR",
            "DE", "GR", "HU", "IE", "IT", "LV", "LT", "LU", "MT", "NL",
            "PL", "PT", "RO", "SK", "SI", "ES", "SE"
        );
        
        return euCountries.contains(customerCountry);
    }

    private void createReportLines(OssReport report, Map<String, Map<BigDecimal, List<Order>>> ordersByCountryAndRate) {
        var euRates = euVatRateRepository.findByIsEuMemberTrueOrderByCountryNameAsc();
        Map<String, String> countryNames = euRates.stream()
            .collect(Collectors.toMap(
                r -> r.getCountryCode(),
                r -> r.getCountryName(),
                (a, b) -> a
            ));

        for (Map.Entry<String, Map<BigDecimal, List<Order>>> countryEntry : ordersByCountryAndRate.entrySet()) {
            String countryCode = countryEntry.getKey();
            
            for (Map.Entry<BigDecimal, List<Order>> rateEntry : countryEntry.getValue().entrySet()) {
                BigDecimal vatRate = rateEntry.getKey();
                List<Order> countryOrders = rateEntry.getValue();

                BigDecimal taxableAmount = countryOrders.stream()
                    .map(o -> o.getSubtotalAmount() != null ? o.getSubtotalAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal vatAmount = countryOrders.stream()
                    .map(o -> o.getCalculatedVatAmount() != null ? o.getCalculatedVatAmount() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal totalAmount = taxableAmount.add(vatAmount);

                OssReportLine line = OssReportLine.builder()
                    .report(report)
                    .countryCode(countryCode)
                    .countryName(countryNames.getOrDefault(countryCode, countryCode))
                    .vatRate(vatRate)
                    .taxableAmount(taxableAmount)
                    .vatAmount(vatAmount)
                    .totalAmount(totalAmount)
                    .ordersCount(countryOrders.size())
                    .b2cAmount(taxableAmount)
                    .b2bExemptAmount(BigDecimal.ZERO)
                    .build();

                ossReportLineRepository.save(line);
            }
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0.00";
        return amount.setScale(2, RoundingMode.HALF_UP).toString();
    }
}
