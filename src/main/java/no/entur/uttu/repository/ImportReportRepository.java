package no.entur.uttu.repository;

import no.entur.uttu.model.job.ImportReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImportReportRepository extends JpaRepository<ImportReport, Long> {
}
