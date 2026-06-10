package no.entur.uttu.export.gtfs;

import no.entur.uttu.UttuIntegrationTest;
import no.entur.uttu.exporter.gtfsflex.GtfsFlexExporterService;
import no.entur.uttu.importer.gtfsflex.GtfsFlexImporterService;

import no.entur.uttu.model.IdFormat;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.geojson.Polygon;
import org.junit.jupiter.api.Test;
import org.onebusaway.gtfs.model.*;
import org.onebusaway.gtfs.model.calendar.ServiceDate;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.springframework.beans.factory.annotation.Autowired;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GtfsFlexExportTest extends UttuIntegrationTest {

    @Autowired
    private GtfsFlexImporterService importerService;

    @Autowired
    private GtfsFlexExporterService exporterService;

    private static final  String TEST_DATASET = "DAT1";
    private static final Long JOB_ID = 25L;
    private static final List<String> allowedStops = List.of("s_5674","s_5691","s_5693","s_5694","s_5695","s_5698","s_6333","s_6336","s_6337","s_6809","s_6824","s_6826","s_6827","s_6828","s_6829","s_6830","s_6831","s_6832","s_8529");



    @Test
    void test_exportFlex() throws IOException {

        Path exportedFilePath = Path.of("src/test/resources/data",GtfsFlexExporterService.EXPORT_DIR,String.valueOf(JOB_ID),JOB_ID + ".zip");
        exportedFilePath.toFile().delete();

        //STEP 1 : import file
        Path testFilePath = Path.of("src/test/resources/data/gtfs-flex.zip");
        File fileToImport = testFilePath.toFile();
        importerService.importGtfsFlex(fileToImport, TEST_DATASET);

        // STEP 2 : launch export
        exporterService.exportGtfsFlex(TEST_DATASET.toLowerCase(), JOB_ID, IdFormat.SOURCE);

        // STEP 3 : check exported file
        GtfsReader gtfsReader = importerService.readGtfsFlexEntitiesFromGtfsZip(exportedFilePath.toFile());
        checkAgency(gtfsReader);
        checkBookingRule(gtfsReader);
        checkCalendars(gtfsReader);
        checkCalendarDates(gtfsReader);
        checkLocationGroups(gtfsReader);
        checkLocationGroupStops(gtfsReader);
        checkRoutes(gtfsReader);
        checkStops(gtfsReader);
        checkStopTimes(gtfsReader);
        checkTrips(gtfsReader);
        checkLocations(gtfsReader);

    }

    private void checkLocations(GtfsReader gtfsReader) {

        Collection<Location> locations = gtfsReader.getEntityStore().getAllEntitiesForType(Location.class);
        assertEquals(1, locations.size());

        Location location = locations.stream().findFirst().get();

        assertEquals("c_5", location.getId().getId());
        assertEquals("AMBLAINVILLE", location.getName());
        assertEquals("60010", location.getDesc());

        GeoJsonObject geom = location.getGeometry();
        assertInstanceOf(Polygon.class, geom);
        Polygon polygon = (Polygon) geom;
        assertEquals(1,polygon.getCoordinates().size());
        List<LngLatAlt> firstCoord = polygon.getCoordinates().stream().findFirst().get();
        assertEquals(276,firstCoord.size());

        assertEquals(49.191634920715046,firstCoord.getFirst().getLatitude());
        assertEquals(2.125659257368852,firstCoord.getFirst().getLongitude());

        assertEquals(49.191634920715046,firstCoord.getLast().getLatitude());
        assertEquals(2.125659257368852,firstCoord.getLast().getLongitude());

    }

    private void checkTrips(GtfsReader gtfsReader) {

        Collection<Trip> trips = gtfsReader.getEntityStore().getAllEntitiesForType(Trip.class);
        assertEquals(4, trips.size());

        for (Trip trip : trips) {
            if ("401".equals(trip.getId().getId())){
                assertEquals("254", trip.getRoute().getId().getId());
                assertEquals("401", trip.getServiceId().getId());
                assertEquals("Mardi et Samedi", trip.getTripShortName());
                assertEquals("0", trip.getDirectionId());
            }else if ("402".equals(trip.getId().getId())){
                assertEquals("254", trip.getRoute().getId().getId());
                assertEquals("402", trip.getServiceId().getId());
                assertEquals("Mercredi et Jeudi", trip.getTripShortName());
                assertEquals("0", trip.getDirectionId());
            }else if ("403".equals(trip.getId().getId())){
                assertEquals("255", trip.getRoute().getId().getId());
                assertEquals("403", trip.getServiceId().getId());
                assertEquals("Mardi et Samedi", trip.getTripShortName());
                assertEquals("1", trip.getDirectionId());
            }else if ("404".equals(trip.getId().getId())){
                assertEquals("255", trip.getRoute().getId().getId());
                assertEquals("404", trip.getServiceId().getId());
                assertEquals("Mercredi et Jeudi", trip.getTripShortName());
                assertEquals("1", trip.getDirectionId());
            }
        }

    }

    private void checkStopTimes(GtfsReader gtfsReader) {
        Collection<StopTime> stopTimes = gtfsReader.getEntityStore().getAllEntitiesForType(StopTime.class);
        assertEquals(76, stopTimes.size());

        for (StopTime stopTime : stopTimes) {
            assertEquals("1", stopTime.getPickupBookingRule().getId().getId());
            assertEquals("1", stopTime.getDropOffBookingRule().getId().getId());

            if ("401".equals(stopTime.getTrip().getId().getId())){
                assertEquals("c_5", stopTime.getLocation().getId().getId());
                assertEquals(30600, stopTime.getStartPickupDropOffWindow());
                assertEquals(43200, stopTime.getEndPickupDropOffWindow());

                switch(stopTime.getStopSequence()){
                    case 0,1,2,3,4,5,6,7,8,11,12,13 -> {
                        assertEquals(1, stopTime.getPickupType());
                        assertEquals(2, stopTime.getDropOffType());
                            }
                    case 9,10,14,15,16,17,18 -> {
                        assertEquals(2, stopTime.getPickupType());
                        assertEquals(1, stopTime.getDropOffType());
                    }
                }
            }else if ("402".equals(stopTime.getTrip().getId().getId())){
                assertEquals("lg_254", stopTime.getLocationGroup().getId().getId());
                assertEquals(48600, stopTime.getStartPickupDropOffWindow());
                assertEquals(64800, stopTime.getEndPickupDropOffWindow());
                assertEquals(2, stopTime.getPickupType());
                assertEquals(2, stopTime.getDropOffType());
            }else if ("403".equals(stopTime.getTrip().getId().getId())){
                assertTrue(allowedStops.contains(stopTime.getStop().getId().getId()));
                assertEquals(30600, stopTime.getStartPickupDropOffWindow());
                assertEquals(43200, stopTime.getEndPickupDropOffWindow());

                switch(stopTime.getStopSequence()){
                    case 0,1,2,3,4,5,6,7,8,11,12,13 -> {
                        assertEquals(2, stopTime.getPickupType());
                        assertEquals(1, stopTime.getDropOffType());
                    }
                    case 9,10,14,15,16,17,18 -> {
                        assertEquals(1, stopTime.getPickupType());
                        assertEquals(2, stopTime.getDropOffType());
                    }
                }
            }else if("404".equals(stopTime.getTrip().getId().getId())){
                assertTrue(allowedStops.contains(stopTime.getStop().getId().getId()));
                assertEquals(48600, stopTime.getStartPickupDropOffWindow());
                assertEquals(64800, stopTime.getEndPickupDropOffWindow());

                switch(stopTime.getStopSequence()){
                    case 0,1,2,3,4,5,6,7,8,11,12,13 -> {
                        assertEquals(2, stopTime.getPickupType());
                        assertEquals(1, stopTime.getDropOffType());
                    }
                    case 9,10,14,15,16,17,18 -> {
                        assertEquals(1, stopTime.getPickupType());
                        assertEquals(2, stopTime.getDropOffType());
                    }
                }
            }
        }
    }

    private void checkStops(GtfsReader gtfsReader) {
        Collection<Stop> stops = gtfsReader.getEntityStore().getAllEntitiesForType(Stop.class);
        assertEquals(19, stops.size());

        for (Stop stop : stops) {
            assertTrue(allowedStops.contains(stop.getId().getId()));
        }
    }

    private void checkRoutes(GtfsReader gtfsReader) {
        Collection<Route> routes = gtfsReader.getEntityStore().getAllEntitiesForType(Route.class);
        assertEquals(2, routes.size());

        for (Route route : routes) {
            if ("254".equals(route.getId().getId())){
                assertEquals("Le Bus - C.C. du Clermontois", route.getAgency().getName());
                assertEquals("Allo le TAD Aller", route.getLongName());
                assertEquals("Allo le TAD", route.getShortName());
                assertEquals(3, route.getType());
                assertEquals("Depart d'Erquery, Fouilleuse, Lamecourt, Maimbeville, Remecourt et Saint-Aubin-sous- Erquery vers Breuil-le-Sec (Republique), Breuil-le-Vert (Marais), Clermont (Gare SNCF, Centre, Commerces, Mairie, Hopital, Clos de Cense, De Nerval), Fitz-James (CHI, Cen", route.getDesc());
            }else if("255".equals(route.getId().getId())){
                assertEquals("Le Bus - C.C. du Clermontois", route.getAgency().getName());
                assertEquals("Allo le TAD Retour", route.getLongName());
                assertEquals("Allo le TAD", route.getShortName());
                assertEquals(3, route.getType());
                assertEquals("Depart de Breuil-le-Sec (Republique), Breuil-le-Vert (Marais), Clermont (Gare SNCF, Centre, Commerces, Mairie, Hopital, Clos de Cense, De Nerval), Fitz-James (CHI, Centre Aquatique, Nelson Mandela)Vers Erquery, Fouilleuse, Lamecourt, Maimbeville, Remecour", route.getDesc());
            }
        }

    }

    private void checkLocationGroupStops(GtfsReader gtfsReader) {
        Collection<LocationGroupElement> locationGroupStops = gtfsReader.getEntityStore().getAllEntitiesForType(LocationGroupElement.class);
        assertEquals(1, locationGroupStops.size());
        LocationGroupElement locationGroupStop = locationGroupStops.stream().findFirst().get();
        assertEquals("lg_254", locationGroupStop.getLocationGroup().getId().getId());
        assertEquals("s_5674", locationGroupStop.getStop().getId().getId());
        
    }

    private void checkLocationGroups(GtfsReader gtfsReader) {
        Collection<LocationGroup> locationGroups = gtfsReader.getEntityStore().getAllEntitiesForType(LocationGroup.class);
        assertEquals(1, locationGroups.size());
        LocationGroup locationGroup = locationGroups.stream().findFirst().get();
        assertEquals("lg_254", locationGroup.getId().getId());
        assertEquals("Allo le TAD Aller", locationGroup.getName());
    }

    private void checkCalendarDates(GtfsReader gtfsReader) {
        Collection<ServiceCalendarDate> calendars = gtfsReader.getEntityStore().getAllEntitiesForType(ServiceCalendarDate.class);
        assertEquals(1, calendars.size());
        ServiceCalendarDate calendarDate = calendars.stream().findFirst().get();
        assertEquals("401", calendarDate.getServiceId().getId());
        assertEquals(new ServiceDate(2026,04,16), calendarDate.getDate());
        assertEquals(2, calendarDate.getExceptionType());

    }

    private void checkCalendars(GtfsReader gtfsReader) {

        Collection<ServiceCalendar> calendars = gtfsReader.getEntityStore().getAllEntitiesForType(ServiceCalendar.class);
        assertEquals(4, calendars.size());
        for (ServiceCalendar calendar : calendars) {
            if ("401".equals(calendar.getServiceId())){
                assertEquals(0, calendar.getMonday());
                assertEquals(1, calendar.getTuesday());
                assertEquals(0, calendar.getWednesday());
                assertEquals(0, calendar.getThursday());
                assertEquals(0, calendar.getFriday());
                assertEquals(1, calendar.getSaturday());
                assertEquals(0, calendar.getSunday());
                assertEquals(new ServiceDate(2024,1,1), calendar.getStartDate());
                assertEquals(new ServiceDate(9999,12,31), calendar.getEndDate());
            }else if ("402".equals(calendar.getServiceId())){
                assertEquals(0, calendar.getMonday());
                assertEquals(0, calendar.getTuesday());
                assertEquals(1, calendar.getWednesday());
                assertEquals(1, calendar.getThursday());
                assertEquals(0, calendar.getFriday());
                assertEquals(0, calendar.getSaturday());
                assertEquals(0, calendar.getSunday());
                assertEquals(new ServiceDate(2024,1,1), calendar.getStartDate());
                assertEquals(new ServiceDate(9999,12,31), calendar.getEndDate());
            }else if ("403".equals(calendar.getServiceId())) {
                assertEquals(0, calendar.getMonday());
                assertEquals(1, calendar.getTuesday());
                assertEquals(0, calendar.getWednesday());
                assertEquals(0, calendar.getThursday());
                assertEquals(0, calendar.getFriday());
                assertEquals(1, calendar.getSaturday());
                assertEquals(0, calendar.getSunday());
                assertEquals(new ServiceDate(2024, 1, 1), calendar.getStartDate());
                assertEquals(new ServiceDate(9999, 12, 31), calendar.getEndDate());
            }else if ("404".equals(calendar.getServiceId())) {
                assertEquals(0, calendar.getMonday());
                assertEquals(0, calendar.getTuesday());
                assertEquals(1, calendar.getWednesday());
                assertEquals(1, calendar.getThursday());
                assertEquals(0, calendar.getFriday());
                assertEquals(0, calendar.getSaturday());
                assertEquals(0, calendar.getSunday());
                assertEquals(new ServiceDate(2024, 1, 1), calendar.getStartDate());
                assertEquals(new ServiceDate(9999, 12, 31), calendar.getEndDate());
            }
        }
    }

    private void checkBookingRule(GtfsReader gtfsReader) {
        Collection<BookingRule> bookingRules = gtfsReader.getEntityStore().getAllEntitiesForType(BookingRule.class);
        assertEquals(1, bookingRules.size());
        BookingRule bookingRule = bookingRules.stream().findFirst().get();
        assertEquals(0, bookingRule.getType());
        assertEquals("the cake is a lie", bookingRule.getMessage());
        assertEquals("0123456789", bookingRule.getPhoneNumber());
        assertEquals("https://www.bookingurl.fr", bookingRule.getInfoUrl());
    }

    private void checkAgency(GtfsReader gtfsReader) {
        Collection<Agency> agencies = gtfsReader.getEntityStore().getAllEntitiesForType(Agency.class);
        assertEquals(1,agencies.size());
        Agency firstAgency = agencies.stream().findFirst().get();
        assertEquals("Le Bus - C.C. du Clermontois",firstAgency.getName());
    }
}
