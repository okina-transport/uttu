package no.entur.uttu.exporter.gtfsflex;

import no.entur.uttu.model.FlexibleLine;
import org.onebusaway.gtfs.model.Agency;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;

public class FlexibleLineMapper {

    public static Route map(FlexibleLine line){
        Route route = new Route();
        AgencyAndId routeId = new AgencyAndId();
        routeId.setId(line.getOriginalId());
        route.setId(routeId);

        route.setShortName(line.getShortName());
        route.setLongName(line.getName());
        route.setDesc(line.getDescription());
        route.setType(3);
        Agency agency = new Agency();
        agency.setId(String.valueOf(line.getNetwork().getPk()));
        route.setAgency(agency);

        return route;
    }
}
