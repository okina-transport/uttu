package no.entur.uttu.export.resource;

import io.swagger.annotations.Api;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import no.entur.uttu.job.JobService;
import no.entur.uttu.model.DTO.GtfsFlexImportStatusDTO;
import org.springframework.stereotype.Component;

@Component
@Api
@Path("/job/")
public class JobResource {

    private final JobService jobService;

    public JobResource(JobService jobService) {
        this.jobService = jobService;
    }

    @GET
    @Path("correlation/{correlationId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getImportStatus(@PathParam("correlationId") String correlationId) {
        return jobService.getJobByCorrelationId(correlationId)
                .map(job -> Response.ok(new GtfsFlexImportStatusDTO(job)).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }

    @GET
    @Path("{jobId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getJob(@PathParam("jobId") Long jobId) {
        return jobService.getJobDetails(jobId)
                .map(dto -> Response.ok(dto).build())
                .orElseGet(() -> Response.status(Response.Status.NOT_FOUND).build());
    }
}
