package no.entur.uttu.routes;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.GtfsFlexImporterService;
import no.entur.uttu.job.JobService;
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
    private final Path gtfsFlexImportFolder;

    public ImportGtfsFlexProcessor(JobService jobService, GtfsFlexImporterService importService, @Value("${uttu.storage.path:/tmp/uttu}") Path gtfsFlexImportFolder) {
        this.jobService = jobService;
        this.importService = importService;
        this.gtfsFlexImportFolder = gtfsFlexImportFolder;
    }

    @Override
    public void process(Exchange exchange) {
        String referential = exchange.getIn().getHeader(OKINA_REFERENTIAL, String.class).replace("mobiiti_", "").toUpperCase();
        String user = exchange.getIn().getHeader(USER_HEADER, String.class);
        String fileName = exchange.getIn().getHeader(GTFS_FLEX_FILE, String.class);
        log.info("Launching gtfs flex import for provider: {}", referential);

        File gtfsZipFile = this.gtfsFlexImportFolder.resolve(fileName).toFile();
        Job job = jobService.createImportJob(fileName, gtfsFlexImportFolder.toString(), user, referential);
        exchange.getIn().setHeader(JOB_ID, job.getId());
        try {
            importService.importGtfsFlex(gtfsZipFile, referential);
            jobService.updateJob(job, JobStatus.FINISHED, gtfsZipFile.getName(), null);
            log.info("Gtfs flex import finished (job.id: {})", job.getId());
            exchange.getIn().setHeader(UTTU_IMPORT_STATUS, "OK");
        } catch (Exception e) {
            log.error("Gtfs flex import failed (job.id: {})", job.getId(), e);
            jobService.updateJob(job, JobStatus.FAILED, gtfsZipFile.getName(), "Erreur technique");
            exchange.getIn().setHeader(UTTU_IMPORT_STATUS, "ERROR");
        }
    }
}
