package no.entur.uttu.importer;

import no.entur.uttu.UttuIntegrationTest;
import no.entur.uttu.importer.gtfsflex.GtfsFlexImporterService;
import no.entur.uttu.model.*;
import no.entur.uttu.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GtfsFlexImporterServiceTest extends UttuIntegrationTest {

    private static final File GTFS_FLEX = new File("src/test/resources/data/gtfs-flex.zip");
    private static final File GTFS_FLEX_UPDATED = new File("src/test/resources/data/gtfs-flex-updated.zip");

    @Autowired
    private GtfsFlexImporterService tested;
    @Autowired
    private BookingArrangementRepository bookingArrangementRepository;
    @Autowired
    private CodespaceRepository codespaceRepository;
    @Autowired
    private ContactRepository contactRepository;
    @Autowired
    private DayTypeAssignmentRepository dayTypeAssignmentRepository;
    @Autowired
    private DayTypeRepository dayTypeRepository;
    @Autowired
    private FlexibleAreaRepository flexibleAreaRepository;
    @Autowired
    private FlexibleLineRepository flexibleLineRepository;
    @Autowired
    private FlexibleStopPlaceRepository flexibleStopPlaceRepository;
    @Autowired
    private JourneyPatternRepository journeyPatternRepository;
    @Autowired
    private RemoteNetworkRepository networkRepository;
    @Autowired
    private OperatingPeriodRepository operatingPeriodRepository;
    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private ServiceJourneyRepository serviceJourneyRepository;
    @Autowired
    private StopPointInJourneyPatternRepository stopPointInJourneyPatternRepository;
    @Autowired
    private StopRepository stopRepository;
    @Autowired
    private TimetabledPassingTimeRepository timetabledPassingTimeRepository;

    @Test
    void test_importGtfsFlex_whenImportingForFirstTime_thenCreatesNetexEntities() throws IOException {
        tested.importGtfsFlex(GTFS_FLEX, "test");

        assertTrue(providerRepository.findByCode("test").isPresent(), "should create provider test");
        assertTrue(codespaceRepository.findByXmlns("TEST").isPresent(), "should create codespace TEST");
        assertNotNull(networkRepository.findByName("TEST"), "network TEST should exist");

        assertEquals(1, bookingArrangementRepository.count(), "should create X booking arrangements");
        assertTrue(bookingArrangementRepository.findByDatasetIdAndOriginalId("TEST", "1").isPresent(), "should create" +
                " booking arrangement TEST 1");

        assertEquals(1, contactRepository.count(), "should create X contacts");
        assertTrue(contactRepository.findByDatasetIdAndOriginalId("TEST", "1").isPresent(), "should create contact " +
                "TEST 1");

        assertEquals(4, dayTypeRepository.count(), "should create X day types");
        for (String netexId : List.of("TEST:DayType:401", "TEST:DayType:402", "TEST:DayType:403", "TEST:DayType:404")) {
            assertNotNull(dayTypeRepository.findByNetexId(netexId), "should create day type " + netexId);
        }

        assertEquals(5, dayTypeAssignmentRepository.count(), "should create X day type assignments");
        for (String originalId : List.of("401", "401_20260416", "402", "403", "404")) {
            assertTrue(dayTypeAssignmentRepository.findByDatasetIdAndOriginalId("TEST", originalId).isPresent(),
                    "should create day type assignment TEST " + originalId);
        }

        assertEquals(19, stopRepository.count(), "should create X stops");
        for (String originalId : List.of(
                "s_5674",
                "s_5691",
                "s_5693",
                "s_5694",
                "s_5695",
                "s_5698",
                "s_6333",
                "s_6336",
                "s_6337",
                "s_6809",
                "s_6824",
                "s_6826",
                "s_6827",
                "s_6828",
                "s_6829",
                "s_6830",
                "s_6831",
                "s_6832",
                "s_8529")) {
            assertTrue(stopRepository.findByDatasetIdAndOriginalId("TEST", originalId).isPresent(), "should create " +
                    "stop TEST " + originalId);
        }

        // there is already one FSP in BDD from data.sql script
        assertEquals(3, flexibleStopPlaceRepository.count(), "should create X flexible stop place(s)");
        for (String netexId : List.of(
                "TEST:FlexibleStopPlace:c_5",
                "TEST:FlexibleStopPlace:lg_254"
        )) {
            assertNotNull(flexibleStopPlaceRepository.findByNetexId(netexId), "should create flexible stop place " + netexId);
        }

        assertEquals(1, flexibleAreaRepository.count(), "should create X flexible area(s)");
        assertNotNull(flexibleAreaRepository.findByDatasetIdAndOriginalId("TEST", "c5"), "should create flexible area TEST c5");

        assertEquals(2, flexibleLineRepository.count(), "should create X flexible line(s)");
        for (String netexId : List.of("TEST:Line:254", "TEST:Line:255")) {
            assertNotNull(flexibleLineRepository.findByNetexId(netexId), "should create flexible line " + netexId);
        }

        assertEquals(76, timetabledPassingTimeRepository.count(), "should create X timetabled passing time(s)");
        for (String netexId : List.of(
                "TEST:TimetabledPassingTime:401_0",
                "TEST:TimetabledPassingTime:401_1",
                "TEST:TimetabledPassingTime:401_2",
                "TEST:TimetabledPassingTime:401_3",
                "TEST:TimetabledPassingTime:401_4",
                "TEST:TimetabledPassingTime:401_5",
                "TEST:TimetabledPassingTime:401_6",
                "TEST:TimetabledPassingTime:401_7",
                "TEST:TimetabledPassingTime:401_8",
                "TEST:TimetabledPassingTime:401_9",
                "TEST:TimetabledPassingTime:401_10",
                "TEST:TimetabledPassingTime:401_11",
                "TEST:TimetabledPassingTime:401_12",
                "TEST:TimetabledPassingTime:401_13",
                "TEST:TimetabledPassingTime:401_14",
                "TEST:TimetabledPassingTime:401_15",
                "TEST:TimetabledPassingTime:401_16",
                "TEST:TimetabledPassingTime:401_17",
                "TEST:TimetabledPassingTime:401_18",
                "TEST:TimetabledPassingTime:402_0",
                "TEST:TimetabledPassingTime:402_1",
                "TEST:TimetabledPassingTime:402_2",
                "TEST:TimetabledPassingTime:402_3",
                "TEST:TimetabledPassingTime:402_4",
                "TEST:TimetabledPassingTime:402_5",
                "TEST:TimetabledPassingTime:402_6",
                "TEST:TimetabledPassingTime:402_7",
                "TEST:TimetabledPassingTime:402_8",
                "TEST:TimetabledPassingTime:402_9",
                "TEST:TimetabledPassingTime:402_10",
                "TEST:TimetabledPassingTime:402_11",
                "TEST:TimetabledPassingTime:402_12",
                "TEST:TimetabledPassingTime:402_13",
                "TEST:TimetabledPassingTime:402_14",
                "TEST:TimetabledPassingTime:402_15",
                "TEST:TimetabledPassingTime:402_16",
                "TEST:TimetabledPassingTime:402_17",
                "TEST:TimetabledPassingTime:402_18",
                "TEST:TimetabledPassingTime:403_0",
                "TEST:TimetabledPassingTime:403_1",
                "TEST:TimetabledPassingTime:403_2",
                "TEST:TimetabledPassingTime:403_3",
                "TEST:TimetabledPassingTime:403_4",
                "TEST:TimetabledPassingTime:403_5",
                "TEST:TimetabledPassingTime:403_6",
                "TEST:TimetabledPassingTime:403_7",
                "TEST:TimetabledPassingTime:403_8",
                "TEST:TimetabledPassingTime:403_9",
                "TEST:TimetabledPassingTime:403_10",
                "TEST:TimetabledPassingTime:403_11",
                "TEST:TimetabledPassingTime:403_12",
                "TEST:TimetabledPassingTime:403_13",
                "TEST:TimetabledPassingTime:403_14",
                "TEST:TimetabledPassingTime:403_15",
                "TEST:TimetabledPassingTime:403_16",
                "TEST:TimetabledPassingTime:403_17",
                "TEST:TimetabledPassingTime:403_18",
                "TEST:TimetabledPassingTime:404_0",
                "TEST:TimetabledPassingTime:404_1",
                "TEST:TimetabledPassingTime:404_2",
                "TEST:TimetabledPassingTime:404_3",
                "TEST:TimetabledPassingTime:404_4",
                "TEST:TimetabledPassingTime:404_5",
                "TEST:TimetabledPassingTime:404_6",
                "TEST:TimetabledPassingTime:404_7",
                "TEST:TimetabledPassingTime:404_8",
                "TEST:TimetabledPassingTime:404_9",
                "TEST:TimetabledPassingTime:404_10",
                "TEST:TimetabledPassingTime:404_11",
                "TEST:TimetabledPassingTime:404_12",
                "TEST:TimetabledPassingTime:404_13",
                "TEST:TimetabledPassingTime:404_14",
                "TEST:TimetabledPassingTime:404_15",
                "TEST:TimetabledPassingTime:404_16",
                "TEST:TimetabledPassingTime:404_17",
                "TEST:TimetabledPassingTime:404_18"
        )) {
            assertNotNull(timetabledPassingTimeRepository.findByNetexId(netexId), "should create timetabled passing time " + netexId);
        }

        assertEquals(76, stopPointInJourneyPatternRepository.count(), "should create X stop point in journey pattern time(s)");
        for (String netexId : List.of(
                "TEST:StopPointInJourneyPattern:401_0",
                "TEST:StopPointInJourneyPattern:401_1",
                "TEST:StopPointInJourneyPattern:401_2",
                "TEST:StopPointInJourneyPattern:401_3",
                "TEST:StopPointInJourneyPattern:401_4",
                "TEST:StopPointInJourneyPattern:401_5",
                "TEST:StopPointInJourneyPattern:401_6",
                "TEST:StopPointInJourneyPattern:401_7",
                "TEST:StopPointInJourneyPattern:401_8",
                "TEST:StopPointInJourneyPattern:401_9",
                "TEST:StopPointInJourneyPattern:401_10",
                "TEST:StopPointInJourneyPattern:401_11",
                "TEST:StopPointInJourneyPattern:401_12",
                "TEST:StopPointInJourneyPattern:401_13",
                "TEST:StopPointInJourneyPattern:401_14",
                "TEST:StopPointInJourneyPattern:401_15",
                "TEST:StopPointInJourneyPattern:401_16",
                "TEST:StopPointInJourneyPattern:401_17",
                "TEST:StopPointInJourneyPattern:401_18",
                "TEST:StopPointInJourneyPattern:402_0",
                "TEST:StopPointInJourneyPattern:402_1",
                "TEST:StopPointInJourneyPattern:402_2",
                "TEST:StopPointInJourneyPattern:402_3",
                "TEST:StopPointInJourneyPattern:402_4",
                "TEST:StopPointInJourneyPattern:402_5",
                "TEST:StopPointInJourneyPattern:402_6",
                "TEST:StopPointInJourneyPattern:402_7",
                "TEST:StopPointInJourneyPattern:402_8",
                "TEST:StopPointInJourneyPattern:402_9",
                "TEST:StopPointInJourneyPattern:402_10",
                "TEST:StopPointInJourneyPattern:402_11",
                "TEST:StopPointInJourneyPattern:402_12",
                "TEST:StopPointInJourneyPattern:402_13",
                "TEST:StopPointInJourneyPattern:402_14",
                "TEST:StopPointInJourneyPattern:402_15",
                "TEST:StopPointInJourneyPattern:402_16",
                "TEST:StopPointInJourneyPattern:402_17",
                "TEST:StopPointInJourneyPattern:402_18",
                "TEST:StopPointInJourneyPattern:403_0",
                "TEST:StopPointInJourneyPattern:403_1",
                "TEST:StopPointInJourneyPattern:403_2",
                "TEST:StopPointInJourneyPattern:403_3",
                "TEST:StopPointInJourneyPattern:403_4",
                "TEST:StopPointInJourneyPattern:403_5",
                "TEST:StopPointInJourneyPattern:403_6",
                "TEST:StopPointInJourneyPattern:403_7",
                "TEST:StopPointInJourneyPattern:403_8",
                "TEST:StopPointInJourneyPattern:403_9",
                "TEST:StopPointInJourneyPattern:403_10",
                "TEST:StopPointInJourneyPattern:403_11",
                "TEST:StopPointInJourneyPattern:403_12",
                "TEST:StopPointInJourneyPattern:403_13",
                "TEST:StopPointInJourneyPattern:403_14",
                "TEST:StopPointInJourneyPattern:403_15",
                "TEST:StopPointInJourneyPattern:403_16",
                "TEST:StopPointInJourneyPattern:403_17",
                "TEST:StopPointInJourneyPattern:403_18",
                "TEST:StopPointInJourneyPattern:404_0",
                "TEST:StopPointInJourneyPattern:404_1",
                "TEST:StopPointInJourneyPattern:404_2",
                "TEST:StopPointInJourneyPattern:404_3",
                "TEST:StopPointInJourneyPattern:404_4",
                "TEST:StopPointInJourneyPattern:404_5",
                "TEST:StopPointInJourneyPattern:404_6",
                "TEST:StopPointInJourneyPattern:404_7",
                "TEST:StopPointInJourneyPattern:404_8",
                "TEST:StopPointInJourneyPattern:404_9",
                "TEST:StopPointInJourneyPattern:404_10",
                "TEST:StopPointInJourneyPattern:404_11",
                "TEST:StopPointInJourneyPattern:404_12",
                "TEST:StopPointInJourneyPattern:404_13",
                "TEST:StopPointInJourneyPattern:404_14",
                "TEST:StopPointInJourneyPattern:404_15",
                "TEST:StopPointInJourneyPattern:404_16",
                "TEST:StopPointInJourneyPattern:404_17",
                "TEST:StopPointInJourneyPattern:404_18"
        )) {
            assertNotNull(stopPointInJourneyPatternRepository.findByNetexId(netexId), "should create stop point in journey pattern " + netexId);
        }

        assertEquals(4, journeyPatternRepository.count(), "should create X journey pattern(s)");
        for (String netexId : List.of("TEST:JourneyPattern:401", "TEST:JourneyPattern:402", "TEST:JourneyPattern:403", "TEST:JourneyPattern:404")) {
            assertNotNull(journeyPatternRepository.findByNetexId(netexId), "should create journey pattern " + netexId);
        }

        assertEquals(4, serviceJourneyRepository.count(), "should create X service journey(s)");
        for (String netexId : List.of("TEST:ServiceJourney:401", "TEST:ServiceJourney:402", "TEST:ServiceJourney:403", "TEST:ServiceJourney:404")) {
            assertNotNull(serviceJourneyRepository.findByNetexId(netexId), "should create service journey " + netexId);
        }

        assertEquals(4, operatingPeriodRepository.count(), "should create X operating period(s)");
        for (String originalId : List.of("401", "402", "403", "404")) {
            assertNotNull(operatingPeriodRepository.findByDatasetIdAndOriginalId("TEST", originalId), "should create operating period TEST " + originalId);
        }
    }

    @Test
    void test_importGtfsFlex_whenReimporting_shouldNotUpdateEntities() throws IOException {
        tested.importGtfsFlex(GTFS_FLEX, "test");
        tested.importGtfsFlex(GTFS_FLEX, "test");

        bookingArrangementRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        codespaceRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        contactRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        dayTypeAssignmentRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        dayTypeRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        flexibleAreaRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        flexibleLineRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        flexibleStopPlaceRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        journeyPatternRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        operatingPeriodRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        providerRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        serviceJourneyRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        stopPointInJourneyPatternRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        stopRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });

        timetabledPassingTimeRepository.findAll().forEach(entity -> {
            assertEquals(0, entity.getVersion(), "should not update entity");
        });
    }

    @Test
    void test_importGtfsFlex_whenImportingUpdatedGtfs_shouldUpdateEntities() throws IOException {
        tested.importGtfsFlex(GTFS_FLEX, "test");
        tested.importGtfsFlex(GTFS_FLEX_UPDATED, "test");

        Optional<Provider> provider = providerRepository.findByDatasetIdAndOriginalId("TEST", "LEBUS:CLT_LB");
        assertTrue(provider.isPresent(), "should create provider");
        assertEquals(1, provider.get().getVersion(), "should update version");
        assertEquals("Le Bus - C.C. du Clermontois 2", provider.get().getName(), "should update version");

        Optional<OperatingPeriod> operatingPeriod = operatingPeriodRepository.findByDatasetIdAndOriginalId("TEST", "401");
        assertTrue(operatingPeriod.isPresent(), "should create operating period");
        assertEquals(1, operatingPeriod.get().getVersion(), "should update version");
        assertEquals(LocalDate.of(2025, 1, 1), operatingPeriod.get().getFromDate(), "should update from date");

        Optional<DayTypeAssignment> dayTypeAssignment = dayTypeAssignmentRepository.findByDatasetIdAndOriginalId(
                "TEST", "401_20270416");
        assertTrue(dayTypeAssignment.isPresent(), "should create DTA");
        assertEquals(0, dayTypeAssignment.get().getVersion(), "should create new DTA");
        assertEquals(dayTypeAssignment.get().getDate(), LocalDate.of(2027, 4, 16), "should update date");

        Optional<FlexibleStopPlace> flexibleStopPlace = flexibleStopPlaceRepository.findByDatasetIdAndOriginalId(
                "TEST", "lg_254");
        assertTrue(flexibleStopPlace.isPresent(), "should create flexible stop place");
        assertEquals(1, flexibleStopPlace.get().getVersion(), "should update version");
        assertEquals("Allo le TAD Aller 2", flexibleStopPlace.get().getName());

        Optional<FlexibleLine> flexibleLine = flexibleLineRepository.findByDatasetIdAndOriginalId("TEST", "254");
        assertTrue(flexibleLine.isPresent(), "should create flexible line");
        assertEquals(1, flexibleLine.get().getVersion(), "should update version");
        assertEquals("Allo le TAD 2", flexibleLine.get().getShortName());
        assertEquals("Allo le TAD Aller 2", flexibleLine.get().getName());

        flexibleLine = flexibleLineRepository.findByDatasetIdAndOriginalId("TEST", "255");
        assertTrue(flexibleLine.isPresent(), "should create flexible line");
        assertEquals(1, flexibleLine.get().getVersion(), "should update version");
        assertEquals("Allo le TAD 2", flexibleLine.get().getShortName());
        assertEquals("Allo le TAD Retour 2", flexibleLine.get().getName());

        Optional<TimetabledPassingTime> timetabledPassingTime =
                timetabledPassingTimeRepository.findByDatasetIdAndOriginalId("TEST", "401_1");
        assertTrue(timetabledPassingTime.isPresent(), "should create timetabled passing time");
        assertEquals(1, timetabledPassingTime.get().getVersion(), "should update version");
        assertEquals(LocalTime.of(9, 0, 0), timetabledPassingTime.get().getEarliestDepartureTime());
        assertEquals(LocalTime.of(12, 30, 0), timetabledPassingTime.get().getLatestArrivalTime());

        timetabledPassingTime =
                timetabledPassingTimeRepository.findByDatasetIdAndOriginalId("TEST", "402_1");
        assertTrue(timetabledPassingTime.isPresent(), "should create timetabled passing time");
        assertEquals(1, timetabledPassingTime.get().getVersion(), "should update version");
        assertEquals(LocalTime.of(14, 0, 0), timetabledPassingTime.get().getEarliestDepartureTime());
        assertEquals(LocalTime.of(18, 30, 0), timetabledPassingTime.get().getLatestArrivalTime());
    }

}
