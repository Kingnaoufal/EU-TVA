package com.euvatease.repository;

import com.euvatease.entity.OssReport;
import com.euvatease.entity.OssReportLine;
import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OssReportLineRepository extends JpaRepository<OssReportLine, Long> {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    void deleteByReport(@Nonnull OssReport report);

    @Nonnull
    List<OssReportLine> findByReport(@Nonnull OssReport report);

    @Query("SELECT l FROM OssReportLine l WHERE l.report = :report ORDER BY l.vatAmount DESC")
    @Nonnull
    List<OssReportLine> findByReportOrderedByVatAmount(@Nonnull @Param("report") OssReport report);

    @Nonnull
    List<OssReportLine> findByReportOrderByCountryCodeAsc(@Nonnull OssReport report);

    @Query("SELECT l.countryCode, SUM(l.vatAmount) FROM OssReportLine l WHERE l.report = :report GROUP BY l.countryCode")
    @Nonnull
    List<Object[]> sumVatByCountry(@Nonnull @Param("report") OssReport report);
}
