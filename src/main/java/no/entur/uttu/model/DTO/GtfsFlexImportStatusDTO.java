package no.entur.uttu.model.DTO;

import lombok.Data;
import no.entur.uttu.model.job.Job;
import no.entur.uttu.model.job.JobStatus;

@Data
public class GtfsFlexImportStatusDTO {

    private final Long id;
    private final String status;

    public GtfsFlexImportStatusDTO(Job job) {
        this.id = job.getId();
        this.status = toMardukState(job.getStatus());
    }

    private static String toMardukState(JobStatus status) {
        return switch (status) {
            case PROCESSING -> "STARTED";
            case FINISHED -> "OK";
            case FAILED -> "FAILED";
        };
    }
}
