package no.entur.uttu.routes;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.export.model.ExportException;
import no.entur.uttu.importer.ImportService;
import no.entur.uttu.job.JobService;
import no.entur.uttu.model.job.Job;
import no.entur.uttu.model.job.JobStatus;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.io.*;

import static no.entur.uttu.Constants.*;

@Component
@Slf4j
public class ImportGtfsFlexProcessor implements Processor {

    private final JobService jobService;
    private final ImportService importService;

    public ImportGtfsFlexProcessor(JobService jobService, ImportService importService) {
        this.jobService = jobService;
        this.importService = importService;
    }

    @Override
    public void process(Exchange exchange) throws IOException {
        String provider = exchange.getIn().getHeader(CHOUETTE_REFERENTIAL, String.class).replace("mobiiti_", "").toUpperCase();
        String user = exchange.getIn().getHeader(USER_HEADER, String.class);
        String fileName = exchange.getIn().getHeader(FILE_NAME, String.class);
        String subFolder = exchange.getIn().getHeader(SUB_FOLDER, String.class);
        log.info("Launching gtfs flex import for provider: {}", provider);

        String filePath = exchange.getIn().getHeader("GTFS_FLEX_FILE_PATH", String.class);

        // todo : à supprimer ou modifier, uniquement présent pour tester la communication marduk/uttu
        if (filePath != null) {
            File flexFile = new File(filePath);

            if (flexFile.exists()) {
                //...
            } else {
                throw new FileNotFoundException("Fichier introuvable sur ce noeud : " + filePath);
            }
        } else {
            throw new IllegalStateException("Header GTFS_FLEX_FILE_PATH manquant dans le message JMS.");
        }


        Job job = jobService.createImportJob(fileName, subFolder, user, provider);
        exchange.getIn().setHeader(JOB_ID, job.getId());
        try {
            String zipFileName = importService.importGtfsFlex(); // todo : à relier au parsing développé dans un autre ticket
            jobService.updateJob(job, JobStatus.FINISHED, zipFileName, null);
            log.info("Gtfs flex import finished (job.id: {})", job.getId());
            exchange.getIn().setHeader(UTTU_IMPORT_STATUS, "OK");
        } catch (ExportException e) {
            log.error("Gtfs flex import failed (job.id: {})", job.getId(), e);
            jobService.updateJob(job, JobStatus.FAILED, null, e.getMessage());
            exchange.getIn().setHeader(UTTU_IMPORT_STATUS, "ERROR");
        } catch (Exception e) {
            log.error("Gtfs flex import failed (job.id: {})", job.getId(), e);
            jobService.updateJob(job, JobStatus.FAILED, null, "Erreur technique");
            exchange.getIn().setHeader(UTTU_IMPORT_STATUS, "ERROR");
        }
    }
}
