package no.entur.uttu.importer.gtfsflex;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.mapper.*;
import no.entur.uttu.model.QuayView;
import no.entur.uttu.model.StopPlaceView;
import no.entur.uttu.stopplace.StopPlaceRegistry;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.onebusaway.gtfs.model.*;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class GtfsFlexMapperService {

    private final AgencyMapper agencyMapper;
    private final BookingRuleMapper bookingRuleMapper;
    private final CalendarMapper calendarMapper;
    private final CalendarDateMapper calendarDateMapper;
    private final LocationMapper locationMapper;
    private final LocationGroupMapper locationGroupMapper;
    private final RouteMapper routeMapper;
    private final StopTimeMapper stopTimeMapper;
    private final TripMapper tripMapper;
    private final StopPlaceRegistry stopPlaceregistry;

    public GtfsFlexMapperService(AgencyMapper agencyMapper, BookingRuleMapper bookingRuleMapper, CalendarMapper calendarMapper, CalendarDateMapper calendarDateMapper, LocationMapper locationMapper, LocationGroupMapper locationGroupMapper, RouteMapper routeMapper, StopTimeMapper stopTimeMapper, TripMapper tripMapper, StopPlaceRegistry stopPlaceregistry) {
        this.agencyMapper = agencyMapper;
        this.bookingRuleMapper = bookingRuleMapper;
        this.calendarMapper = calendarMapper;
        this.calendarDateMapper = calendarDateMapper;
        this.locationMapper = locationMapper;
        this.locationGroupMapper = locationGroupMapper;
        this.routeMapper = routeMapper;
        this.stopTimeMapper = stopTimeMapper;
        this.tripMapper = tripMapper;
        this.stopPlaceregistry = stopPlaceregistry;
    }


    public Referential mapGtfsFlexToNetex(GtfsReader gtfsReader, String datasetId) {
        log.info("Mapping GTFS flex entities to NETEX entities");
        Referential gtfsImportReferential = new Referential(datasetId.toUpperCase());

        Collection<Agency> agencies = gtfsReader.getEntityStore().getAllEntitiesForType(Agency.class);
        if (CollectionUtils.isNotEmpty(agencies)) {
            agencies.forEach(agency -> agencyMapper.map(agency, gtfsImportReferential));
        }

        Collection<BookingRule> bookingRules = gtfsReader.getEntityStore().getAllEntitiesForType(BookingRule.class);
        if (CollectionUtils.isNotEmpty(bookingRules)) {
            bookingRules.forEach(bookingRule -> bookingRuleMapper.map(bookingRule, gtfsImportReferential));
        }

        Collection<ServiceCalendar> calendars = gtfsReader.getEntityStore().getAllEntitiesForType(ServiceCalendar.class);
        if (CollectionUtils.isNotEmpty(calendars)) {
            calendars.forEach(calendar -> calendarMapper.map(calendar, gtfsImportReferential));
        }

        Collection<ServiceCalendarDate> calendarDates = gtfsReader.getEntityStore().getAllEntitiesForType(ServiceCalendarDate.class);
        if (CollectionUtils.isNotEmpty(calendarDates)) {
            calendarDates.forEach(calendarDate -> calendarDateMapper.map(calendarDate, gtfsImportReferential));
        }

        Collection<Location> locations = gtfsReader.getEntityStore().getAllEntitiesForType(Location.class);
        if (CollectionUtils.isNotEmpty(locations)) {
            locations.forEach(location -> locationMapper.map(location, gtfsImportReferential));
        }

        Collection<LocationGroup> locationGroups = gtfsReader.getEntityStore().getAllEntitiesForType(LocationGroup.class);
        if (CollectionUtils.isNotEmpty(locationGroups)) {
            locationGroups.forEach(locationGroup -> locationGroupMapper.map(locationGroup, gtfsImportReferential));
            sendStopsToTiamat(datasetId, locationGroups);
        }

        Collection<Route> routes = gtfsReader.getEntityStore().getAllEntitiesForType(Route.class);
        if (CollectionUtils.isNotEmpty(routes)) {
            routes.forEach(route -> routeMapper.map(route, gtfsImportReferential));
        }

        Collection<StopTime> stopTimes = gtfsReader.getEntityStore().getAllEntitiesForType(StopTime.class);
        if (CollectionUtils.isNotEmpty(stopTimes)) {
            stopTimes.forEach(stopTime -> stopTimeMapper.map(stopTime, gtfsImportReferential));
            sendStopTimesToTiamat(datasetId, stopTimes);
        }

        Collection<Trip> trips = gtfsReader.getEntityStore().getAllEntitiesForType(Trip.class);
        if (CollectionUtils.isNotEmpty(trips)) {
            trips.forEach(trip -> tripMapper.map(trip, gtfsImportReferential));
        }

        log.info("Finished mapping GTFS flex entities to NETEX entities");
        return gtfsImportReferential;
    }

    private void sendStopsToTiamat(String datasetId, Collection<LocationGroup> locationGroups) {

        List<StopPlaceView> stopToSend = new ArrayList<>();

        for (LocationGroup locationGroup : locationGroups) {
            if (locationGroup.getLocations() == null){
                continue;
            }

            for (StopLocation location : locationGroup.getLocations()) {
                if (location instanceof Stop stop){
                    stopToSend.add(convertQuayToStopPlaceView(stop));
                }
            }
        }

        if (CollectionUtils.isNotEmpty(stopToSend)){
            stopPlaceregistry.createTadQuays(datasetId, stopToSend);
        }
    }

    private void sendStopTimesToTiamat(String datasetId, Collection<StopTime> stopTimes) {

        List<StopPlaceView> stopToSend = new ArrayList<>();

        for (StopTime stopTime : stopTimes) {
            if (stopTime.getStop() == null){
                continue;
            }

            if (stopTime.getStop() instanceof Stop stop){
                stopToSend.add(convertQuayToStopPlaceView(stop));
            }

        }

        if (CollectionUtils.isNotEmpty(stopToSend)){
            stopPlaceregistry.createTadQuays(datasetId, stopToSend);
        }
    }

    public StopPlaceView convertQuayToStopPlaceView(Stop stop) {
        StopPlaceView stopPlaceView = new StopPlaceView();

        String parentId = StringUtils.isEmpty(stop.getParentStation()) ? "COM_" + stop.getId().getId() : stop.getParentStation();
        stopPlaceView.setImportedId(parentId);
        stopPlaceView.setLatitude(BigDecimal.valueOf(stop.getLat()));
        stopPlaceView.setLongitude(BigDecimal.valueOf(stop.getLon()));

        List<QuayView> quays = new ArrayList<>();
        QuayView quayView = new QuayView();
        quayView.setImportedId(stop.getId().getId());
        quayView.setName(stop.getName());
        quayView.setLatitude(BigDecimal.valueOf(stop.getLat()));
        quayView.setLongitude(BigDecimal.valueOf(stop.getLon()));
        quays.add(quayView);
        stopPlaceView.setQuays(quays);
        return stopPlaceView;
    }
}
