package no.entur.uttu.exporter.gtfsflex;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.model.DayType;
import no.entur.uttu.model.ServiceJourney;
import org.apache.commons.collections4.CollectionUtils;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Route;
import org.onebusaway.gtfs.model.Trip;

@Slf4j
public class ServiceJourneyMapper {

    public static Trip map(ServiceJourney serviceJourney){
        Trip trip = new Trip();
        AgencyAndId tripId = new AgencyAndId();
        tripId.setId(serviceJourney.getOriginalId());
        trip.setId(tripId);

        if (CollectionUtils.isNotEmpty(serviceJourney.getDayTypes())){
            DayType dayType = serviceJourney.getDayTypes().iterator().next();
            AgencyAndId serviceId = new AgencyAndId();
            serviceId.setId(dayType.getOriginalId());
            trip.setServiceId(serviceId);
        }

        if (serviceJourney.getJourneyPattern() != null){
            trip.setTripShortName(serviceJourney.getJourneyPattern().getShortName());
            String lineId = serviceJourney.getJourneyPattern().getLine().getOriginalId();
            Route tripRoute = new Route();
            AgencyAndId routeId = new AgencyAndId();
            routeId.setId(lineId);
            tripRoute.setId(routeId);
            trip.setRoute(tripRoute);
            trip.setDirectionId(serviceJourney.getJourneyPattern().getDirectionType().toDirectionId());
        }else{
            log.warn("Trip without journey pattern : {}", serviceJourney.getPk());
        }








        return trip;
    }
}
