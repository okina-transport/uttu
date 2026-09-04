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
import java.util.function.Consumer;

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
        mapEntities(agencies, agency -> agencyMapper.map(agency, gtfsImportReferential), "agency.txt");

        Collection<BookingRule> bookingRules = gtfsReader.getEntityStore().getAllEntitiesForType(BookingRule.class);
        mapEntities(bookingRules, bookingRule -> bookingRuleMapper.map(bookingRule, gtfsImportReferential), "booking_rules.txt");

        Collection<ServiceCalendar> calendars = gtfsReader.getEntityStore().getAllEntitiesForType(ServiceCalendar.class);
        mapEntities(calendars, calendar -> calendarMapper.map(calendar, gtfsImportReferential), "calendar.txt");

        Collection<ServiceCalendarDate> calendarDates = gtfsReader.getEntityStore().getAllEntitiesForType(ServiceCalendarDate.class);
        mapEntities(calendarDates, calendarDate -> calendarDateMapper.map(calendarDate, gtfsImportReferential), "calendar_dates.txt");

        Collection<Location> locations = gtfsReader.getEntityStore().getAllEntitiesForType(Location.class);
        mapEntities(locations, location -> locationMapper.map(location, gtfsImportReferential), "locations.geojson");

        Collection<LocationGroup> locationGroups = gtfsReader.getEntityStore().getAllEntitiesForType(LocationGroup.class);
        mapEntities(locationGroups, locationGroup -> locationGroupMapper.map(locationGroup, gtfsImportReferential), "location_groups.txt");
        if (CollectionUtils.isNotEmpty(locationGroups)) {
            sendStopsToTiamat(datasetId, locationGroups);
        }

        Collection<Route> routes = gtfsReader.getEntityStore().getAllEntitiesForType(Route.class);
        mapEntities(routes, route -> routeMapper.map(route, gtfsImportReferential), "routes.txt");

        Collection<StopTime> stopTimes = gtfsReader.getEntityStore().getAllEntitiesForType(StopTime.class);
        mapEntities(stopTimes, stopTime -> stopTimeMapper.map(stopTime, gtfsImportReferential), "stop_times.txt");
        if (CollectionUtils.isNotEmpty(stopTimes)) {
            sendStopTimesToTiamat(datasetId, stopTimes);
        }

        Collection<Trip> trips = gtfsReader.getEntityStore().getAllEntitiesForType(Trip.class);
        mapEntities(trips, trip -> tripMapper.map(trip, gtfsImportReferential), "trips.txt");

        log.info("Finished mapping GTFS flex entities to NETEX entities");
        return gtfsImportReferential;
    }

    private <T> void mapEntities(Collection<T> entities, Consumer<T> mapperFn, String fileName) {
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        for (T entity : entities) {
            try {
                mapperFn.accept(entity);
            } catch (RuntimeException e) {
                throw new GtfsFileMappingException(fileName, e);
            }
        }
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
