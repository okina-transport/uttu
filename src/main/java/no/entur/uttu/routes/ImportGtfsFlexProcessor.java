package no.entur.uttu.routes;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.config.Context;
import no.entur.uttu.importer.gtfsflex.GtfsFlexImportReportFactory;
import no.entur.uttu.importer.gtfsflex.GtfsFlexImporterService;
import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.job.JobService;
import no.entur.uttu.model.job.ImportReport;
import no.entur.uttu.model.job.Job;
import no.entur.uttu.model.job.JobStatus;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;

import static no.entur.uttu.Constants.*;

@Component
@Slf4j
public class ImportGtfsFlexProcessor implements Processor {

    private final JobService jobService;
    private final GtfsFlexImporterService importService;
    private final GtfsFlexImportReportFactory importReportFactory;
    private final Path gtfsFlexImportFolder;

    public ImportGtfsFlexProcessor(JobService jobService, GtfsFlexImporterService importService, GtfsFlexImportReportFactory importReportFactory, @Value("${uttu.storage.path:/tmp/uttu}") Path gtfsFlexImportFolder) {
        this.jobService = jobService;
        this.importService = importService;
        this.importReportFactory = importReportFactory;
        this.gtfsFlexImportFolder = gtfsFlexImportFolder;
    }

    @Override
    public void process(Exchange exchange) {
        String referential = exchange.getIn().getHeader(OKINA_REFERENTIAL, String.class).replace("mobiiti_", "").toUpperCase();
        String user = exchange.getIn().getHeader(USER_HEADER, String.class);
        String fileName = exchange.getIn().getHeader(GTFS_FLEX_FILE, String.class);
        String correlationId = exchange.getIn().getHeader("RutebankenCorrelationId", String.class);
        log.info("Launching gtfs flex import for provider: {} (correlationId: {})", referential, correlationId);

        File gtfsZipFile = this.gtfsFlexImportFolder.resolve(fileName).toFile();
        Job job = jobService.createImportJob(fileName, gtfsFlexImportFolder.toString(), user, referential, correlationId);
        exchange.getIn().setHeader(JOB_ID, job.getId());
        try {
            Context.setUsername(user);
            Context.setProvider(referential.toLowerCase());
            Referential gtfsImportReferential = importService.importGtfsFlex(gtfsZipFile, referential);
            updateJobSafely(job, JobStatus.FINISHED, gtfsZipFile, null);
            saveSuccessReportSafely(job, gtfsZipFile, gtfsImportReferential);
            log.info("Gtfs flex import finished (job.id: {})", job.getId());
            exchange.getIn().setHeader(UTTU_IMPORT_STATUS, "OK");
        } catch (Exception e) {
            log.error("Gtfs flex import failed (job.id: {})", job.getId(), e);
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getClass().getSimpleName();
            updateJobSafely(job, JobStatus.FAILED, gtfsZipFile, errorMessage);
            saveFailureReportSafely(job, gtfsZipFile, e);
            exchange.getIn().setHeader(UTTU_IMPORT_STATUS, "ERROR");
        } finally {
            Context.clear();
        }
    }

    private void updateJobSafely(Job job, JobStatus status, File gtfsZipFile, String errorMessage) {
        try {
            jobService.updateJob(job, status, gtfsZipFile.getName(), errorMessage);
        } catch (Exception e) {
            log.error("Failed to update job status to {} (job.id: {})", status, job.getId(), e);
        }
    }

    private void saveSuccessReportSafely(Job job, File gtfsZipFile, Referential gtfsImportReferential) {
        try {
            ImportReport report = importReportFactory.buildSuccessReport(gtfsZipFile, gtfsImportReferential);
            jobService.saveImportReport(job.getId(), report);
        } catch (Exception e) {
            log.error("Failed to save import report (job.id: {})", job.getId(), e);
        }
    }

    private void saveFailureReportSafely(Job job, File gtfsZipFile, Exception error) {
        try {
            ImportReport report = importReportFactory.buildFailureReport(gtfsZipFile, error);
            jobService.saveImportReport(job.getId(), report);
        } catch (Exception e) {
            log.error("Failed to save import report (job.id: {})", job.getId(), e);
        }
    }
}
