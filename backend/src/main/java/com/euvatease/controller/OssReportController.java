package com.euvatease.controller;

import com.euvatease.entity.OssReport;
import com.euvatease.entity.Shop;
import com.euvatease.repository.OssReportRepository;
import com.euvatease.service.OssReportService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion des rapports OSS
 */
@RestController
@RequestMapping("/oss")
public class OssReportController {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ----------------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(OssReportController.class);

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Nonnull
    private final OssReportRepository ossReportRepository;

    @Nonnull
    private final OssReportService ossReportService;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public OssReportController(@Nonnull OssReportService ossReportService,
                               @Nonnull OssReportRepository ossReportRepository) {
        this.ossReportService = Objects.requireNonNull(ossReportService, "ossReportService must not be null");
        this.ossReportRepository = Objects.requireNonNull(ossReportRepository, "ossReportRepository must not be null");
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    /**
     * Télécharge le rapport CSV
     * GET /api/oss/report/{id}/csv
     */
    @Nonnull
    @GetMapping("/report/{id}/csv")
    public ResponseEntity<String> downloadCsv(
            @Nonnull @RequestAttribute("shop") Shop shop,
            @Nonnull @PathVariable Long id) {

        return ossReportRepository.findById(id)
            .filter(r -> r.getShop().getId().equals(shop.getId()))
            .map(report -> {
                String csv = ossReportService.generateCsv(report);
                ossReportService.markAsDownloaded(report);

                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                        String.format("attachment; filename=rapport-oss-%s.csv", report.getQuarterLabel()))
                    .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                    .body(csv);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Télécharge le rapport PDF
     * GET /api/oss/report/{id}/pdf
     */
    @Nonnull
    @GetMapping("/report/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @Nonnull @RequestAttribute("shop") Shop shop,
            @Nonnull @PathVariable Long id) {

        return ossReportRepository.findById(id)
            .filter(r -> r.getShop().getId().equals(shop.getId()))
            .map(report -> {
                byte[] pdf = ossReportService.generatePdf(report);
                ossReportService.markAsDownloaded(report);

                return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                        String.format("attachment; filename=rapport-oss-%s.pdf", report.getQuarterLabel()))
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Génère un rapport OSS pour un trimestre
     * POST /api/oss/generate
     */
    @Nonnull
    @PostMapping("/generate")
    public ResponseEntity<?> generateReport(
            @Nonnull @RequestAttribute("shop") Shop shop,
            @Nonnull @RequestBody Map<String, Integer> request) {

        int year = request.get("year");
        int quarter = request.get("quarter");

        OssReport report = ossReportService.generateReport(shop, year, quarter);

        return ResponseEntity.ok(mapReport(report));
    }

    /**
     * Prochaine échéance de déclaration
     * GET /api/oss/deadline
     */
    @Nonnull
    @GetMapping("/deadline")
    public ResponseEntity<?> getDeadline() {
        return ResponseEntity.ok(ossReportService.getNextDeadline());
    }

    /**
     * Détails d'un rapport
     * GET /api/oss/report/{id}
     */
    @Nonnull
    @GetMapping("/report/{id}")
    public ResponseEntity<?> getReport(
            @Nonnull @RequestAttribute("shop") Shop shop,
            @Nonnull @PathVariable Long id) {

        return ossReportRepository.findById(id)
            .filter(r -> r.getShop().getId().equals(shop.getId()))
            .map(report -> ResponseEntity.ok(mapReport(report)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Liste des rapports
     * GET /api/oss/reports
     */
    @Nonnull
    @GetMapping("/reports")
    public ResponseEntity<?> getReports(@Nonnull @RequestAttribute("shop") Shop shop) {
        List<OssReport> reports = ossReportService.getReportHistory(shop);

        return ResponseEntity.ok(Map.of(
            "reports", reports.stream().map(this::mapReport).collect(Collectors.toList()),
            "nextDeadline", ossReportService.getNextDeadline()
        ));
    }

    /**
     * Marque un rapport comme soumis
     * POST /api/oss/report/{id}/submit
     */
    @Nonnull
    @PostMapping("/report/{id}/submit")
    public ResponseEntity<?> markAsSubmitted(
            @Nonnull @RequestAttribute("shop") Shop shop,
            @Nonnull @PathVariable Long id,
            @Nullable @RequestBody(required = false) Map<String, String> request) {

        String notes = request != null ? request.get("notes") : null;

        return ossReportRepository.findById(id)
            .filter(r -> r.getShop().getId().equals(shop.getId()))
            .map(report -> {
                report = ossReportService.markAsSubmitted(report, notes);
                return ResponseEntity.ok(Map.of(
                    "message", "Rapport marqué comme soumis",
                    "report", mapReport(report)
                ));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Prévisualisation du rapport (sans génération)
     * POST /api/oss/preview
     */
    @Nonnull
    @PostMapping("/preview")
    public ResponseEntity<?> previewReport(
            @Nonnull @RequestAttribute("shop") Shop shop,
            @Nonnull @RequestBody Map<String, Integer> request) {

        int year = request.get("year");
        int quarter = request.get("quarter");

        // Vérifier si un rapport existe déjà
        return ossReportRepository.findByShopAndYearAndQuarter(shop, year, quarter)
            .map(report -> ResponseEntity.ok(Map.of(
                "exists", true,
                "report", mapReport(report)
            )))
            .orElse(ResponseEntity.ok(Map.of(
                "exists", false,
                "message", String.format("Aucun rapport pour T%d %d. Cliquez sur 'Générer' pour créer le rapport.", quarter, year)
            )));
    }

    @Nonnull
    private Map<String, Object> mapReport(@Nonnull OssReport report) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", report.getId());
        map.put("year", report.getYear());
        map.put("quarter", report.getQuarter());
        map.put("quarterLabel", report.getQuarterLabel());
        map.put("periodStart", report.getPeriodStart());
        map.put("periodEnd", report.getPeriodEnd());
        map.put("totalSales", report.getTotalSales());
        map.put("totalVat", report.getTotalVat());
        map.put("totalOrders", report.getTotalOrders());
        map.put("b2bOrders", report.getB2bOrders());
        map.put("b2cOrders", report.getB2cOrders());
        map.put("countriesCount", report.getCountriesCount());
        map.put("status", report.getStatus());
        map.put("statusMessage", report.getStatusMessage());
        map.put("generatedAt", report.getGeneratedAt());
        map.put("downloadedAt", report.getDownloadedAt() != null ? report.getDownloadedAt() : "");
        map.put("submittedAt", report.getSubmittedAt() != null ? report.getSubmittedAt() : "");
        return map;
    }
}
