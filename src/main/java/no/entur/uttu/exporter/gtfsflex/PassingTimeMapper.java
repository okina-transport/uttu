package no.entur.uttu.exporter.gtfsflex;

import no.entur.uttu.model.ServiceJourney;
import no.entur.uttu.model.StopPointInJourneyPattern;
import no.entur.uttu.model.TimetabledPassingTime;
import org.apache.commons.collections4.CollectionUtils;
import org.onebusaway.gtfs.model.*;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static no.entur.uttu.util.DateUtils.ONE_DAY_IN_SECONDS;

public class PassingTimeMapper {

    public static List<StopTime> map(ServiceJourney serviceJourney){
        List<StopTime> stopTimes = new ArrayList<>();
        if (CollectionUtils.isEmpty(serviceJourney.getPassingTimes())){
            return stopTimes;
        }

        Trip trip = new Trip();
        AgencyAndId tripId = new AgencyAndId();
        tripId.setId(serviceJourney.getOriginalId());
        trip.setId(tripId);


        List<StopPointInJourneyPattern> pointsInPattern = serviceJourney.getJourneyPattern().getPointsInSequence();



        for (TimetabledPassingTime passingTime : serviceJourney.getPassingTimes()) {
            StopTime stopTime = new StopTime();
            stopTime.setStopSequence(passingTime.getOrder() - 1);
            stopTime.setTrip(trip);

            Optional<StopPointInJourneyPattern> stopInPatternOpt = getStopWithOrder(pointsInPattern, passingTime.getOrder());
            if (!stopInPatternOpt.isPresent()){
                continue;
            }

            StopPointInJourneyPattern pointInPattern = stopInPatternOpt.get();

            if (pointInPattern.getFlexibleStopPlace() != null){
                Stop location = new Stop();
                AgencyAndId locationId = new AgencyAndId();
                locationId.setId(pointInPattern.getFlexibleStopPlace().getOriginalId());
                location.setId(locationId);
                if (CollectionUtils.isEmpty(pointInPattern.getFlexibleStopPlace().getStops())){
                    stopTime.setLocation(location);
                }else{
                    stopTime.setLocationGroup(location);
                }
            }else if (pointInPattern.getStop() != null){
                Stop stop = new Stop();
                AgencyAndId stopId = new AgencyAndId();
                stopId.setId(pointInPattern.getStop().getOriginalId());
                stop.setId(stopId);
                stopTime.setStop(stop);
            }

            stopTime.setPickupType(Boolean.TRUE.equals(pointInPattern.getForBoarding()) ? 2 : 1);
            stopTime.setDropOffType(Boolean.TRUE.equals(pointInPattern.getForAlighting()) ? 2 : 1);

            if (passingTime.getEarliestDepartureTime() != null){
                stopTime.setStartPickupDropOffWindow(getNbofSecondsFromLocalTime(passingTime.getEarliestDepartureTime(), passingTime.getEarliestDepartureDayOffset()));
            }

            if (passingTime.getLatestArrivalTime() != null){
                stopTime.setEndPickupDropOffWindow(getNbofSecondsFromLocalTime(passingTime.getLatestArrivalTime(), passingTime.getLatestArrivalDayOffset()));
            }

            if (pointInPattern.getBookingArrangement() != null){
                BookingRule bookingRule = new BookingRule();
                AgencyAndId bookingRuleId = new AgencyAndId();
                bookingRuleId.setId(String.valueOf(pointInPattern.getBookingArrangement().getPk()));
                bookingRule.setId(bookingRuleId);
                // one booking rule in DB but 2 in GTFS. Setting the same booking rule for dropoff and pickup
                stopTime.setPickupBookingRule(bookingRule);
                stopTime.setDropOffBookingRule(bookingRule);
            }
            stopTimes.add(stopTime);
        }
        return stopTimes;
    }

    private static int getNbofSecondsFromLocalTime(LocalTime timeToConvert, int dayOffset){
        int nbOfSeconds = timeToConvert.toSecondOfDay();
        if (dayOffset > 0){
            nbOfSeconds = nbOfSeconds + dayOffset * ONE_DAY_IN_SECONDS;
        }
        return nbOfSeconds;
    }

    private static Optional<StopPointInJourneyPattern> getStopWithOrder(List<StopPointInJourneyPattern> pointsInPattern, int order){
        return pointsInPattern.stream().filter(point -> point.getOrder() == order).findFirst();
    }
}
