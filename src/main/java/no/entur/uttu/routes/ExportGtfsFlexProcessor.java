package no.entur.uttu.routes;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.config.Context;
import no.entur.uttu.exporter.gtfsflex.GtfsFlexExporterService;
import no.entur.uttu.job.JobService;
import no.entur.uttu.model.job.Job;
import no.entur.uttu.model.job.JobStatus;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

import static no.entur.uttu.Constants.*;

@Component
@Slf4j
public class ExportGtfsFlexProcessor implements Processor {

    private final JobService jobService;
    private final Path gtfsFlexImportFolder;
    private final GtfsFlexExporterService gtfsFlexExporterService;

    public ExportGtfsFlexProcessor(JobService jobService, @Value("${uttu.storage.path:/tmp/uttu}") Path gtfsFlexImportFolder, GtfsFlexExporterService gtfsFlexExporterService) {
        this.jobService = jobService;
        this.gtfsFlexImportFolder = gtfsFlexImportFolder;
        this.gtfsFlexExporterService = gtfsFlexExporterService;
    }

    @Override
    public void process(Exchange exchange) {
        String referential = exchange.getIn().getHeader(OKINA_REFERENTIAL, String.class).replace("mobiiti_", "").toUpperCase();
        String user = exchange.getIn().getHeader(USER_HEADER, String.class);
        String fileName = exchange.getIn().getHeader(GTFS_FLEX_FILE, String.class);
        log.info("Launching gtfs flex import for provider: {}", referential);


        Job job = jobService.createExportJob(fileName, gtfsFlexImportFolder.toString(), user, referential);
        exchange.getIn().setHeader(JOB_ID, job.getId());
        try {
            Context.setUsername(user);
            Context.setProvider(referential.toLowerCase());
            gtfsFlexExporterService.exportGtfsFlex(referential, job.getId());

            jobService.updateJob(job, JobStatus.FINISHED, "", null);
            log.info("Gtfs flex export finished (job.id: {})", job.getId());
            exchange.getIn().setHeader(FLEX_EXPORT_STATUS, "OK");
        } catch (Exception e) {
            log.error("Gtfs flex export failed (job.id: {})", job.getId(), e);
            jobService.updateJob(job, JobStatus.FAILED, "", "Erreur technique");
            exchange.getIn().setHeader(FLEX_EXPORT_STATUS, "ERROR");
        } finally {
            Context.clear();
        }
    }
}
