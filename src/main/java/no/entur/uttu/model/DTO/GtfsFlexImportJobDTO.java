package no.entur.uttu.model.DTO;

import lombok.Getter;
import no.entur.uttu.model.job.Job;

import java.time.Instant;

@Getter
public class GtfsFlexImportJobDTO {

    private final Long id;
    private final String status;
    private final String fileName;
    private final String message;
    private final Instant started;
    private final Instant finished;
    private final String userName;
    private final String provider;
    private final String correlationId;
    private final ImportReportDTO report;

    public GtfsFlexImportJobDTO(Job job) {
        this.id = job.getId();
        this.status = job.getStatus() != null ? job.getStatus().name() : null;
        this.fileName = job.getFileName();
        this.message = job.getMessage();
        this.started = job.getStarted();
        this.finished = job.getFinished();
        this.userName = job.getUserName();
        this.provider = job.getProvider();
        this.correlationId = job.getCorrelationId();
        this.report = job.getImportReport() != null ? new ImportReportDTO(job.getImportReport()) : null;
    }
}
