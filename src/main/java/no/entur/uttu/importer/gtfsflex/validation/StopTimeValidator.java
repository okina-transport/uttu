package no.entur.uttu.importer.gtfsflex.validation;

import no.entur.uttu.model.StopPointInJourneyPattern;
import org.onebusaway.gtfs.model.StopTime;
import org.springframework.stereotype.Component;

@Component
public class StopTimeValidator {

    private static final int GTFS_REGULARLY_SCHEDULED = 0;

    public void validate(StopTime entity, StopPointInJourneyPattern stopPointInJourneyPattern) {
        boolean hasStop = entity.getStop() != null;
        boolean hasFlexReference = entity.getLocation() != null || entity.getLocationGroup() != null;

        if (!hasStop && !hasFlexReference) {
            throw error(entity, "none of stop_id, location_id or location_group_id could be resolved to a known "
                    + "stop/location/location group (the column is either empty or references an id that does not "
                    + "exist in stops.txt/locations.geojson/location_groups.txt)");
        }
        if (hasStop && hasFlexReference) {
            throw error(entity, "stop_id and location_id/location_group_id cannot both be set on the same row");
        }
        if (hasFlexReference && stopPointInJourneyPattern.getStop() != null
                || hasStop && stopPointInJourneyPattern.getFlexibleStopPlace() != null) {
            throw error(entity, "conflicting rows resolving to both a stop_id and a location_id/location_group_id "
                    + "for the same trip_id/stop_sequence");
        }

        boolean hasStartWindow = entity.getStartPickupDropOffWindow() != StopTime.MISSING_VALUE;
        boolean hasEndWindow = entity.getEndPickupDropOffWindow() != StopTime.MISSING_VALUE;
        boolean hasWindow = hasStartWindow || hasEndWindow;
        boolean hasFixedTime = entity.getArrivalTime() != StopTime.MISSING_VALUE
                || entity.getDepartureTime() != StopTime.MISSING_VALUE;

        if (hasWindow && hasFixedTime) {
            throw error(entity, "start_pickup_drop_off_window/end_pickup_drop_off_window are forbidden when "
                    + "arrival_time or departure_time is defined on the same row");
        }
        if (hasStartWindow != hasEndWindow) {
            throw error(entity, "start_pickup_drop_off_window and end_pickup_drop_off_window must both be set, "
                    + "or neither");
        }
        if (hasFlexReference && !hasWindow) {
            throw error(entity, "start_pickup_drop_off_window and end_pickup_drop_off_window are required when "
                    + "location_id or location_group_id is set");
        }
        if (hasWindow && entity.getPickupType() == GTFS_REGULARLY_SCHEDULED) {
            throw error(entity, "pickup_type cannot be 0 (or empty, 'regularly scheduled pickup') when "
                    + "start_pickup_drop_off_window/end_pickup_drop_off_window are defined");
        }
        if (hasWindow && entity.getDropOffType() == GTFS_REGULARLY_SCHEDULED) {
            throw error(entity, "drop_off_type cannot be 0 (or empty, 'regularly scheduled drop off') when "
                    + "start_pickup_drop_off_window/end_pickup_drop_off_window are defined");
        }
    }

    private static IllegalArgumentException error(StopTime entity, String message) {
        return new IllegalArgumentException(String.format(
                "Invalid stop_times.txt row for trip_id=%s, stop_sequence=%d: %s",
                entity.getTrip().getId().getId(), entity.getStopSequence(), message));
    }
}
