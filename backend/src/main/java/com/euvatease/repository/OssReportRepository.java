package com.euvatease.repository;

import com.euvatease.entity.OssReport;
import com.euvatease.entity.Shop;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OssReportRepository extends JpaRepository<OssReport, Long> {

    Optional<OssReport> findByShopAndYearAndQuarter(Shop shop, Integer year, Integer quarter);

    Page<OssReport> findByShopOrderByYearDescQuarterDesc(Shop shop, Pageable pageable);

    List<OssReport> findByShopAndStatus(Shop shop, OssReport.ReportStatus status);

    @Query("SELECT r FROM OssReport r WHERE r.shop = :shop ORDER BY r.year DESC, r.quarter DESC")
    List<OssReport> findLatestReports(@Param("shop") Shop shop, Pageable pageable);

    @Query("SELECT r FROM OssReport r WHERE r.shop = :shop AND r.status = 'READY'")
    List<OssReport> findReadyReports(@Param("shop") Shop shop);

    @Query("SELECT COUNT(r) FROM OssReport r WHERE r.shop = :shop")
    long countReports(@Param("shop") Shop shop);

    @Query("SELECT r FROM OssReport r WHERE r.shop = :shop AND r.year = :year")
    List<OssReport> findByShopAndYear(@Param("shop") Shop shop, @Param("year") Integer year);

    boolean existsByShopAndYearAndQuarter(Shop shop, Integer year, Integer quarter);

    @Query("SELECT r FROM OssReport r WHERE r.shop = :shop AND r.status IN ('READY', 'DOWNLOADED') ORDER BY r.year DESC, r.quarter DESC")
    List<OssReport> findPendingSubmission(@Param("shop") Shop shop);
}
