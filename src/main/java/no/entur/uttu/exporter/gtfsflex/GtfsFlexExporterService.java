package no.entur.uttu.exporter.gtfsflex;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.cache.TadQuayIdCache;
import no.entur.uttu.model.*;
import no.entur.uttu.repository.BookingArrangementRepository;
import no.entur.uttu.repository.FlexibleLineRepository;
import no.entur.uttu.repository.FlexibleStopPlaceRepository;
import no.entur.uttu.repository.ServiceJourneyRepository;
import no.entur.uttu.stopplace.StopPlaceRegistry;
import no.entur.uttu.util.FileUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.onebusaway.gtfs.model.*;
import org.onebusaway.gtfs.model.Stop;
import org.onebusaway.gtfs.serialization.GtfsWriter;
import org.onebusaway.gtfs.serialization.LocationsGeoJSONWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static no.entur.uttu.Constants.QUAY_KEY;

@Service
@Slf4j
public class GtfsFlexExporterService {
    private final FlexibleStopPlaceRepository flexibleStopPlaceRepository;
    private final BookingArrangementRepository bookingArrangementRepository;
    private final ServiceJourneyRepository serviceJourneyRepository;
    private final FlexibleLineRepository flexibleLineRepository;
    private final StopPlaceRegistry stopPlaceregistry;
    public static final String EXPORT_DIR = "gtfsExports";
    private final TadQuayIdCache tadQuayIdCache;

    private final Path exportPath;

    public GtfsFlexExporterService(FlexibleStopPlaceRepository flexibleStopPlaceRepository, BookingArrangementRepository bookingArrangementRepository, ServiceJourneyRepository serviceJourneyRepository, FlexibleLineRepository flexibleLineRepository, StopPlaceRegistry stopPlaceregistry, TadQuayIdCache tadQuayIdCache, @Value("${uttu.storage.path:/tmp/uttu}") Path uttuStoragePath) {
        this.flexibleStopPlaceRepository = flexibleStopPlaceRepository;
        this.bookingArrangementRepository = bookingArrangementRepository;
        this.serviceJourneyRepository = serviceJourneyRepository;
        this.flexibleLineRepository = flexibleLineRepository;
        this.stopPlaceregistry = stopPlaceregistry;
        this.tadQuayIdCache = tadQuayIdCache;
        this.exportPath = uttuStoragePath.resolve(EXPORT_DIR);
    }

    @Transactional
    public void exportGtfsFlex(String provider, Long jobId, IdFormat formatId) throws IOException {
        Path jobPath = exportPath.resolve(String.valueOf(jobId));
        File outputDir = new File(jobPath.toUri());
        outputDir.getParentFile().mkdirs();

        GtfsWriter writer = new GtfsWriter();
        writer.setOutputLocation(outputDir);
        generateAgencies(provider, writer);
        generateLocations(provider, jobId);
        generateLocationGroups(provider, writer, formatId);
        generateBookingArrangements(provider, writer);
        generateTrips(provider, writer);
        generateStopTimes(provider, writer, formatId);
        generateRoutes(provider, writer);
        generateCalendars(provider, writer);
        writer.close();

        FileUtils.zipFilesInDirectory(jobPath.toAbsolutePath().toString(), jobId + ".zip");
        FileUtils.deleteFilesByExtension(jobPath.toAbsolutePath().toString(), ".txt");
        FileUtils.deleteFilesByExtension(jobPath.toAbsolutePath().toString(), ".geojson");

    }

    private void applyTridentId(String provider, AgencyAndId agencyAndId) {
        String mobiItiId = tadQuayIdCache.getMobiItiNetexIdOrFallback(provider + QUAY_KEY + agencyAndId.getId());
        agencyAndId.setId(mobiItiId);
    }

    private void generateAgencies(String provider, GtfsWriter writer) {
        List<FlexibleLine> lines = getLinesForProvider(provider);
        if (CollectionUtils.isEmpty(lines)){
            return;
        }

        List<String> alreadyExportedAgencies = new ArrayList<>();
        for (FlexibleLine line : lines) {
            if (line.getNetwork() != null && !alreadyExportedAgencies.contains(line.getNetwork().getNetexId())){
                Agency agency = new Agency();
                agency.setId(String.valueOf(line.getNetwork().getPk()));
                agency.setName(line.getNetwork().getName());
                agency.setUrl("emptyURL");
                agency.setTimezone("Europe/Paris");
                writer.handleEntity(agency);
                alreadyExportedAgencies.add(line.getNetwork().getNetexId());
            }
        }
    }



    private void generateCalendars(String provider, GtfsWriter writer) {
        List<ServiceJourney> serviceJourneys = getServiceJourneysForProvider(provider);

        if (CollectionUtils.isEmpty(serviceJourneys)){
            return;
        }

        for (ServiceJourney serviceJourney : serviceJourneys) {
            if (CollectionUtils.isEmpty(serviceJourney.getDayTypes())){
                continue;
            }

            DayType dayType = serviceJourney.getDayTypes().iterator().next();
            ServiceCalendar calendar = DayTypeMapper.map(dayType);
            writer.handleEntity(calendar);
            List<ServiceCalendarDate> calendarDates = CalendarExceptionMapper.map(dayType);
            if (CollectionUtils.isNotEmpty(calendarDates)){
                calendarDates.forEach(writer::handleEntity);
            }
        }
    }

    private void generateRoutes(String provider, GtfsWriter writer) {
        List<FlexibleLine> lines = getLinesForProvider(provider);
        
        if (CollectionUtils.isEmpty(lines)){
            return;
        }

        for (FlexibleLine line : lines) {
            Route route = FlexibleLineMapper.map(line);
            writer.handleEntity(route);
        }
        
    }

    private List<FlexibleLine> getLinesForProvider(String provider){
        List<FlexibleLine> lines = new ArrayList<>();
        if ("TECHNIQUE".equals(provider)) {
            Iterable<FlexibleLine> serviceJourneyItr = flexibleLineRepository.findAll();
            serviceJourneyItr.forEach(lines::add);
        }else{
            lines = flexibleLineRepository.findAllByProviderCode(provider.toLowerCase());
        }

        return lines;
    }

    private void generateStopTimes(String provider, GtfsWriter writer, IdFormat formatId) {
        List<ServiceJourney> serviceJourneys = getServiceJourneysForProvider(provider);

        if (CollectionUtils.isEmpty(serviceJourneys)){
            return;
        }

        Set<String> netexIds = new HashSet<>();
        List<List<StopTime>> allStopTimes = new ArrayList<>();
        for (ServiceJourney serviceJourney : serviceJourneys) {
            List<StopTime> stopTimes = PassingTimeMapper.map(serviceJourney);
            allStopTimes.add(stopTimes);
            for (StopTime st : stopTimes) {
                if (st.getStop() != null && st.getStop().getId() != null && st.getStop().getId().getId() != null) {
                    netexIds.add(provider + QUAY_KEY + st.getStop().getId().getId());
                }
            }
        }

        if (netexIds.isEmpty()) {
            return;
        }

        Set<String> netexIdList = new HashSet<>(netexIds);
        List<QuayView> quayViews = stopPlaceregistry.getQuayListFromRegistry(provider, netexIdList);

        Map<String, QuayView> quayByNetexId = quayViews.stream()
                .filter(qv -> qv.getImportedId() != null)
                .collect(Collectors.toMap(QuayView::getImportedId, Function.identity(), (a, b) -> a));

        Set<String> alreadySeenStops = new HashSet<>();

        for (List<StopTime> stopTimes : allStopTimes) {
            for (StopTime stopTime : stopTimes) {
                writer.handleEntity(stopTime);

                if (stopTime.getStop() != null && stopTime.getStop().getId() != null) {
                    String stopId = stopTime.getStop().getId().getId();
                    if (!alreadySeenStops.contains(stopId)) {
                        Stop stop = new Stop();
                        stop.setId(stopTime.getStop().getId());
                        if(formatId.equals(IdFormat.TRIDENT)) {
                            applyTridentId(provider, stop.getId());
                        }

                        QuayView qv = quayByNetexId.get(provider + QUAY_KEY + stopId);
                        if (qv != null) {
                            if (qv.getName() != null) {
                                stop.setName(qv.getName());
                            }
                            if (qv.getLatitude() != null) {
                                stop.setLat(qv.getLatitude().doubleValue());
                            }
                            if (qv.getLongitude() != null) {
                                stop.setLon(qv.getLongitude().doubleValue());
                            }
                            if (formatId.equals(IdFormat.TRIDENT) && qv.getNetexStopPlaceId() != null) {
                                stop.setParentStation(qv.getNetexStopPlaceId());
                            } else {
                                stop.setParentStation(qv.getStopPlaceImportedId());
                            }
                        }

                        writer.handleEntity(stop);
                        alreadySeenStops.add(stopId);
                    }
                }
            }
        }
    }

    private void generateTrips(String provider, GtfsWriter writer) {
        List<ServiceJourney> serviceJourneys = getServiceJourneysForProvider(provider);
        
        if (CollectionUtils.isEmpty(serviceJourneys)){
            return;
        }
        for (ServiceJourney serviceJourney : serviceJourneys) {
            Trip trip = ServiceJourneyMapper.map(serviceJourney);
            writer.handleEntity(trip);
        }
        
    }

    private List<ServiceJourney> getServiceJourneysForProvider(String provider) {
        List<ServiceJourney> serviceJourneys = new ArrayList<>();
        if ("TECHNIQUE".equals(provider)) {
            Iterable<ServiceJourney> serviceJourneyItr = serviceJourneyRepository.findAll();
            serviceJourneyItr.forEach(serviceJourneys::add);
        }else{
            serviceJourneys = serviceJourneyRepository.findAllByProviderCode(provider.toLowerCase());
        }
        return serviceJourneys;
    }

    private void generateBookingArrangements(String provider, GtfsWriter writer) {
        List<BookingArrangement> bookingArrangements = new ArrayList<>();
        if ("TECHNIQUE".equals(provider)) {
            Iterable<BookingArrangement> resultsItr = bookingArrangementRepository.findAll();
            resultsItr.forEach(bookingArrangements::add);
        } else {
            bookingArrangements = bookingArrangementRepository.findAllByDatasetId(provider);
        }

        if (CollectionUtils.isEmpty(bookingArrangements)){
            return;
        }

        for (BookingArrangement bookingArrangement : bookingArrangements) {
            BookingRule bookingRule = BookingArrangementMapper.map(bookingArrangement);
            writer.handleEntity(bookingRule);
        }

    }

    private void generateLocationGroups(String provider, GtfsWriter writer, IdFormat formatId) {
        List<FlexibleStopPlace> flexibleStopPlaces = getFlexibleStopPlacesForProvider(provider);

        if (CollectionUtils.isEmpty(flexibleStopPlaces)) {
            log.info("No flexibleStopPlaces found for provider:{}", provider);
            return ;
        }

        for (FlexibleStopPlace flexibleStopPlace : flexibleStopPlaces) {
            if(CollectionUtils.isNotEmpty(flexibleStopPlace.getStops())){
                LocationGroup locationGroup = FlexibleStopPlaceToLocationGroupMapper.map(flexibleStopPlace);
                writer.handleEntity(locationGroup);
                List<LocationGroupElement> groupStops = FlexibleStopPlaceToLocationGroupMapper.getLocationGroupStops(flexibleStopPlace);

                for (LocationGroupElement stopLocation : groupStops) {
                    if (formatId.equals(IdFormat.TRIDENT)
                            && stopLocation.getStop() != null
                            && stopLocation.getStop().getId().getId() != null) {
                        AgencyAndId stopId = stopLocation.getStop().getId();
                        applyTridentId(provider, stopId);
                    }

                    writer.handleEntity(stopLocation);
                }
            }
        }
    }

    private void generateLocations(String provider, Long jobId) {

        File exportFile = exportPath.resolve(String.valueOf(jobId)).resolve("locations.geojson").toFile();
        exportFile.getParentFile().mkdirs();
        List<Location> locations = buildLocations(provider);
        if (CollectionUtils.isEmpty(locations)) {
            return;
        }

        try (PrintWriter printWriter = new PrintWriter(exportPath.resolve(String.valueOf(jobId)).resolve("locations.geojson").toFile())) {
            LocationsGeoJSONWriter writer = new LocationsGeoJSONWriter(printWriter);
            writer.write(new ArrayList<>(locations));
        } catch (IOException e) {
            log.error("Error while writing locations. provider : " + provider, e);
        }
    }

    private List<FlexibleStopPlace> getFlexibleStopPlacesForProvider(String provider){
        List<FlexibleStopPlace> flexibleStopPlaces = new ArrayList<>();
        if ("TECHNIQUE".equals(provider)) {
            Iterable<FlexibleStopPlace> resultsItr = flexibleStopPlaceRepository.findAll();
            resultsItr.forEach(flexibleStopPlaces::add);
        } else {
            flexibleStopPlaces = flexibleStopPlaceRepository.findAllByProviderCode(provider.toLowerCase());
        }
        return flexibleStopPlaces;
    }

    private List<Location> buildLocations(String provider) {
        List<Location> results = new ArrayList<>();
        List<FlexibleStopPlace> flexibleStopPlaces = getFlexibleStopPlacesForProvider(provider);

        if (CollectionUtils.isEmpty(flexibleStopPlaces)) {
            log.info("No flexibleStopPlaces found for provider:{}", provider);
            return new ArrayList<>();
        }

        int locationIndex = 1;
        for (FlexibleStopPlace flexibleStopPlaceToExport : flexibleStopPlaces) {
            if (CollectionUtils.isNotEmpty(flexibleStopPlaceToExport.getFlexibleAreas())) {
                Location newLocation = FlexibleStopPlaceToLocationMapper.map(flexibleStopPlaceToExport, locationIndex);
                results.add(newLocation);
            }
            locationIndex++;
        }
        return results;
    }
}
