package no.entur.uttu.export.resource;

import io.swagger.annotations.Api;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import no.entur.uttu.model.DTO.HasDataDTO;
import no.entur.uttu.model.FlexibleLine;
import no.entur.uttu.repository.FlexibleLineRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Api
@Path("/admin")
public class AdminResource {


    private final FlexibleLineRepository flexibleLineRepository;

    public AdminResource(FlexibleLineRepository flexibleLineRepository) {
        this.flexibleLineRepository = flexibleLineRepository;
    }

    @GET
    @Path("/has-data/{providerId}")
    @Produces({MediaType.APPLICATION_OCTET_STREAM, MediaType.APPLICATION_JSON})
    public Response hasData(@PathParam("providerId") String providerId) {
        long nbOfLines;

        if ("TECHNIQUE".equals(providerId)){
             nbOfLines = flexibleLineRepository.count();
        }else{
            nbOfLines = flexibleLineRepository.countByProviderCode(providerId.toLowerCase());
        }

        HasDataDTO hasDataDTO = new HasDataDTO(providerId, nbOfLines > 0);
        return Response.ok(hasDataDTO).build();
    }
}
