package no.entur.uttu.importer.gtfsflex.mapper;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.model.FlexibleStopPlace;
import no.entur.uttu.model.Stop;
import no.entur.uttu.model.VehicleModeEnumeration;
import org.onebusaway.gtfs.model.LocationGroup;
import org.onebusaway.gtfs.model.StopLocation;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class LocationGroupMapper implements Mapper<LocationGroup> {

    @Override
    public void map(LocationGroup gtfsEntity, Referential gtfsImportReferential) {
        log.info("Mapping location group {}", gtfsEntity.getId().getId());

        FlexibleStopPlace flexibleStopPlace = gtfsImportReferential.getFlexibleStopPlace(gtfsEntity.getId().getId());
        flexibleStopPlace.setName(gtfsEntity.getName());
        flexibleStopPlace.setTransportMode(VehicleModeEnumeration.BUS);
        flexibleStopPlace.setProvider(gtfsImportReferential.getProvider(gtfsEntity.getId().getAgencyId()));

        List<Stop> stops = new ArrayList<>();
        for (StopLocation stopLocation : gtfsEntity.getLocations()) {
            Stop stop = gtfsImportReferential.getStop(stopLocation.getId().getId());
            stop.setProvider(gtfsImportReferential.getProvider(stopLocation.getId().getAgencyId()));
            stops.add(stop);
        }
        flexibleStopPlace.setStops(stops);

        log.debug("gtfsEntity {}", gtfsEntity);
        log.debug("flexibleStopPlace {}", flexibleStopPlace);
    }

}
