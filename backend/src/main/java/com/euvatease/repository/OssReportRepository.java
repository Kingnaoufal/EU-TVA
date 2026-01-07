package com.euvatease.repository;

import com.euvatease.entity.OssReport;
import com.euvatease.entity.Shop;
import jakarta.annotation.Nonnull;
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

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @Query("SELECT COUNT(r) FROM OssReport r WHERE r.shop = :shop")
    long countReports(@Nonnull @Param("shop") Shop shop);

    boolean existsByShopAndYearAndQuarter(@Nonnull Shop shop,
                                          @Nonnull Integer year,
                                          @Nonnull Integer quarter);

    @Nonnull
    List<OssReport> findByShopAndStatus(@Nonnull Shop shop,
                                        @Nonnull OssReport.ReportStatus status);

    @Query("SELECT r FROM OssReport r WHERE r.shop = :shop AND r.year = :year")
    @Nonnull
    List<OssReport> findByShopAndYear(@Nonnull @Param("shop") Shop shop,
                                      @Nonnull @Param("year") Integer year);

    @Nonnull
    Optional<OssReport> findByShopAndYearAndQuarter(@Nonnull Shop shop,
                                                    @Nonnull Integer year,
                                                    @Nonnull Integer quarter);

    @Nonnull
    Page<OssReport> findByShopOrderByYearDescQuarterDesc(@Nonnull Shop shop,
                                                         @Nonnull Pageable pageable);

    @Query("SELECT r FROM OssReport r WHERE r.shop = :shop ORDER BY r.year DESC, r.quarter DESC")
    @Nonnull
    List<OssReport> findLatestReports(@Nonnull @Param("shop") Shop shop,
                                      @Nonnull Pageable pageable);

    @Query("SELECT r FROM OssReport r WHERE r.shop = :shop AND r.status IN ('READY', 'DOWNLOADED') ORDER BY r.year DESC, r.quarter DESC")
    @Nonnull
    List<OssReport> findPendingSubmission(@Nonnull @Param("shop") Shop shop);

    @Query("SELECT r FROM OssReport r WHERE r.shop = :shop AND r.status = 'READY'")
    @Nonnull
    List<OssReport> findReadyReports(@Nonnull @Param("shop") Shop shop);
}
