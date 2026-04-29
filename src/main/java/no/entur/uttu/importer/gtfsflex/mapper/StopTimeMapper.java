package no.entur.uttu.importer.gtfsflex.mapper;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.model.*;
import org.apache.commons.lang3.StringUtils;
import org.onebusaway.gtfs.model.StopTime;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

@Slf4j
@Component
public class StopTimeMapper implements Mapper<StopTime> {

    private static final int GTFS_NO_PICKUP = 1;
    private static final int GTFS_NO_DROP_OFF = 1;
    private static final long ONE_DAY_IN_SECONDS = Duration.ofDays(1).toSeconds();

    @Override
    public void map(StopTime entity, Referential gtfsImportReferential) {
        log.info("Mapping stop time {}/{}", entity.getTrip().getId().getId(), entity.getStopSequence());
        TimetabledPassingTime timetabledPassingTime = gtfsImportReferential.getTimetabledPassingTime(entity.getTrip().getId().getId(), entity.getStopSequence());

        timetabledPassingTime.setOrder(entity.getStopSequence() + 1);

        if (entity.getArrivalTime() != StopTime.MISSING_VALUE) {
            timetabledPassingTime.setArrivalTime(LocalTime.ofSecondOfDay(entity.getArrivalTime() % ONE_DAY_IN_SECONDS));
            if (entity.getArrivalTime() > ONE_DAY_IN_SECONDS) {
                timetabledPassingTime.setArrivalDayOffset(1);
            }
        }

        if (entity.getDepartureTime() != StopTime.MISSING_VALUE) {
            timetabledPassingTime.setDepartureTime(LocalTime.ofSecondOfDay(entity.getDepartureTime() % ONE_DAY_IN_SECONDS));
            if (entity.getDepartureTime() > ONE_DAY_IN_SECONDS) {
                timetabledPassingTime.setDepartureDayOffset(1);
            }
        }

        if (entity.getStartPickupDropOffWindow() != StopTime.MISSING_VALUE) {
            LocalTime startPickupTime = LocalTime.ofSecondOfDay(entity.getStartPickupDropOffWindow() % ONE_DAY_IN_SECONDS);
            timetabledPassingTime.setEarliestDepartureTime(startPickupTime);
            if (entity.getStartPickupDropOffWindow() > ONE_DAY_IN_SECONDS) {
                timetabledPassingTime.setEarliestDepartureDayOffset(1);
            }
        }

        if (entity.getEndPickupDropOffWindow() != StopTime.MISSING_VALUE) {
            LocalTime endPickupTime = LocalTime.ofSecondOfDay(entity.getEndPickupDropOffWindow() % ONE_DAY_IN_SECONDS);
            timetabledPassingTime.setLatestArrivalTime(endPickupTime);
            if (entity.getEndPickupDropOffWindow() > ONE_DAY_IN_SECONDS) {
                timetabledPassingTime.setLatestArrivalDayOffset(1);
            }
        }

        ServiceJourney serviceJourney = gtfsImportReferential.getServiceJourney(entity.getTrip().getId().getId());
        serviceJourney.addPassingTime(timetabledPassingTime);
        timetabledPassingTime.setProvider(gtfsImportReferential.getProvider(entity.getTrip().getId().getAgencyId()));

        StopPointInJourneyPattern stopPointInJourneyPattern = gtfsImportReferential.getStopPointInJourneyPattern(entity.getTrip().getId().getId(), entity.getStopSequence());
        stopPointInJourneyPattern.setOrder(entity.getStopSequence() + 1);

        String frontText = StringUtils.isNotBlank(entity.getStopHeadsign()) ? entity.getStopHeadsign() : entity.getTrip().getTripHeadsign();
        if (StringUtils.isNotBlank(frontText)) {
            DestinationDisplay destinationDisplay = gtfsImportReferential.getDestinationDisplay(entity.getTrip().getId().getId(), entity.getStopSequence());
            destinationDisplay.setProvider(gtfsImportReferential.getProvider(entity.getTrip().getId().getAgencyId()));
            destinationDisplay.setFrontText(frontText);
            stopPointInJourneyPattern.setDestinationDisplay(destinationDisplay);
        }

        stopPointInJourneyPattern.setProvider(gtfsImportReferential.getProvider(entity.getTrip().getId().getAgencyId()));
        // there is only one booking arrangement in NETEX but two booking rules in GTFS
        // map either pickup_booking_rule or drop_off_booking_rule
        if (entity.getPickupBookingRule() != null) {
            stopPointInJourneyPattern.setBookingArrangement(gtfsImportReferential.getBookingArrangement(entity.getPickupBookingRule().getId().getId()));
        }
        if (entity.getDropOffBookingRule() != null) {
            stopPointInJourneyPattern.setBookingArrangement(gtfsImportReferential.getBookingArrangement(entity.getDropOffBookingRule().getId().getId()));
        }
        if (entity.getStop() != null) {
            Stop stop = gtfsImportReferential.getStop(entity.getStop().getId().getId());
            stop.setProvider(gtfsImportReferential.getProvider(entity.getStop().getId().getAgencyId()));
            stopPointInJourneyPattern.setStop(stop);
        } else if (entity.getLocation() != null) {
            FlexibleStopPlace flexibleStopPlace = gtfsImportReferential.getFlexibleStopPlace(entity.getLocation().getId().getId());
            flexibleStopPlace.addStopPointInJourneyPattern(stopPointInJourneyPattern);
        } else if (entity.getLocationGroup() != null) {
            FlexibleStopPlace flexibleStopPlace = gtfsImportReferential.getFlexibleStopPlace(entity.getLocationGroup().getId().getId());
            flexibleStopPlace.addStopPointInJourneyPattern(stopPointInJourneyPattern);
        }
        stopPointInJourneyPattern.setForBoarding(entity.getPickupType() != GTFS_NO_PICKUP);
        stopPointInJourneyPattern.setForAlighting(entity.getDropOffType() != GTFS_NO_DROP_OFF);

        JourneyPattern journeyPattern = gtfsImportReferential.getJourneyPattern(entity.getTrip().getId().getId());
        journeyPattern.addPointInSequence(stopPointInJourneyPattern);
    }


}
