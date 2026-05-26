package no.entur.uttu.exporter.gtfsflex;

import no.entur.uttu.model.FlexibleStopPlace;

import no.entur.uttu.model.Stop;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.LocationGroup;
import org.onebusaway.gtfs.model.LocationGroupElement;


import java.util.ArrayList;
import java.util.List;

public class FlexibleStopPlaceToLocationGroupMapper {

    public static LocationGroup map(FlexibleStopPlace flexibleStopPlace) {

        LocationGroup locationGroup = new LocationGroup();
        AgencyAndId groupId = new AgencyAndId();
        groupId.setId(flexibleStopPlace.getOriginalId());
        locationGroup.setId(groupId);
        locationGroup.setName(flexibleStopPlace.getName());

        return locationGroup;
    }

    public static List<LocationGroupElement> getLocationGroupStops(FlexibleStopPlace flexibleStopPlace) {
        List<LocationGroupElement> groupStops = new ArrayList<>();
        AgencyAndId groupId = new AgencyAndId();
        groupId.setId(flexibleStopPlace.getOriginalId());
        LocationGroup locationGroup = new LocationGroup();
        locationGroup.setId(groupId);

        for (Stop stop : flexibleStopPlace.getStops()) {
            org.onebusaway.gtfs.model.Stop stopInGroup = new org.onebusaway.gtfs.model.Stop();
            AgencyAndId stopAgencyAndId = new AgencyAndId();
            stopAgencyAndId.setId(stop.getOriginalId());
            stopInGroup.setId(stopAgencyAndId);
            LocationGroupElement locationGroupStop = new LocationGroupElement();
            locationGroupStop.setId(groupId);
            locationGroupStop.setStop(stopInGroup);
            locationGroupStop.setLocationGroup(locationGroup);
            groupStops.add(locationGroupStop);
        }
        return groupStops;
    }
}
