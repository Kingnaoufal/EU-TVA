package com.euvatease.repository;

import com.euvatease.entity.OssReport;
import com.euvatease.entity.OssReportLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OssReportLineRepository extends JpaRepository<OssReportLine, Long> {

    List<OssReportLine> findByReport(OssReport report);

    List<OssReportLine> findByReportOrderByCountryCodeAsc(OssReport report);

    @Query("SELECT l FROM OssReportLine l WHERE l.report = :report ORDER BY l.vatAmount DESC")
    List<OssReportLine> findByReportOrderedByVatAmount(@Param("report") OssReport report);

    void deleteByReport(OssReport report);

    @Query("SELECT l.countryCode, SUM(l.vatAmount) FROM OssReportLine l WHERE l.report = :report GROUP BY l.countryCode")
    List<Object[]> sumVatByCountry(@Param("report") OssReport report);
}
