package com.euvatease.controller;

import com.euvatease.entity.OssReport;
import com.euvatease.entity.Shop;
import com.euvatease.repository.OssReportRepository;
import com.euvatease.service.OssReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur pour la gestion des rapports OSS
 */
@RestController
@RequestMapping("/oss")
public class OssReportController {

    private static final Logger log = LoggerFactory.getLogger(OssReportController.class);

    private final OssReportService ossReportService;
    private final OssReportRepository ossReportRepository;

    public OssReportController(OssReportService ossReportService, OssReportRepository ossReportRepository) {
        this.ossReportService = ossReportService;
        this.ossReportRepository = ossReportRepository;
    }

    /**
     * Génère un rapport OSS pour un trimestre
     * POST /api/oss/generate
     */
    @PostMapping("/generate")
    public ResponseEntity<?> generateReport(
            @RequestAttribute("shop") Shop shop,
            @RequestBody Map<String, Integer> request) {

        int year = request.get("year");
        int quarter = request.get("quarter");

        OssReport report = ossReportService.generateReport(shop, year, quarter);

        return ResponseEntity.ok(mapReport(report));
    }

    /**
     * Télécharge le rapport CSV
     * GET /api/oss/report/{id}/csv
     */
    @GetMapping("/report/{id}/csv")
    public ResponseEntity<String> downloadCsv(
            @RequestAttribute("shop") Shop shop,
            @PathVariable Long id) {

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
    @GetMapping("/report/{id}/pdf")
    public ResponseEntity<byte[]> downloadPdf(
            @RequestAttribute("shop") Shop shop,
            @PathVariable Long id) {

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
     * Liste des rapports
     * GET /api/oss/reports
     */
    @GetMapping("/reports")
    public ResponseEntity<?> getReports(@RequestAttribute("shop") Shop shop) {
        List<OssReport> reports = ossReportService.getReportHistory(shop);

        return ResponseEntity.ok(Map.of(
            "reports", reports.stream().map(this::mapReport).collect(Collectors.toList()),
            "nextDeadline", ossReportService.getNextDeadline()
        ));
    }

    /**
     * Détails d'un rapport
     * GET /api/oss/report/{id}
     */
    @GetMapping("/report/{id}")
    public ResponseEntity<?> getReport(
            @RequestAttribute("shop") Shop shop,
            @PathVariable Long id) {

        return ossReportRepository.findById(id)
            .filter(r -> r.getShop().getId().equals(shop.getId()))
            .map(report -> ResponseEntity.ok(mapReport(report)))
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Marque un rapport comme soumis
     * POST /api/oss/report/{id}/submit
     */
    @PostMapping("/report/{id}/submit")
    public ResponseEntity<?> markAsSubmitted(
            @RequestAttribute("shop") Shop shop,
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> request) {

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
     * Prochaine échéance de déclaration
     * GET /api/oss/deadline
     */
    @GetMapping("/deadline")
    public ResponseEntity<?> getDeadline() {
        return ResponseEntity.ok(ossReportService.getNextDeadline());
    }

    /**
     * Prévisualisation du rapport (sans génération)
     * POST /api/oss/preview
     */
    @PostMapping("/preview")
    public ResponseEntity<?> previewReport(
            @RequestAttribute("shop") Shop shop,
            @RequestBody Map<String, Integer> request) {

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

    // Méthodes utilitaires

    private Map<String, Object> mapReport(OssReport report) {
        Map<String, Object> map = new java.util.HashMap<>();
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
