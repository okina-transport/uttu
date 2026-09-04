package no.entur.uttu.importer.gtfsflex.validation;

import org.onebusaway.gtfs.model.Route;
import org.springframework.stereotype.Component;

@Component
public class RouteValidator {

    public void validate(Route entity) {
        if (entity.getAgency() == null) {
            throw new IllegalArgumentException(String.format(
                    "Invalid routes.txt row for route_id=%s: agency_id could not be resolved to a known agency "
                            + "(the column is either empty with more than one agency declared in agency.txt, or "
                            + "references an id that does not exist in agency.txt)",
                    entity.getId().getId()));
        }
    }
}
