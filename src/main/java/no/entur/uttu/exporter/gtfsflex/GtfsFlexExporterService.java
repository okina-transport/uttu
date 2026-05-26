package no.entur.uttu.exporter.gtfsflex;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.model.*;
import no.entur.uttu.repository.BookingArrangementRepository;
import no.entur.uttu.repository.FlexibleLineRepository;
import no.entur.uttu.repository.FlexibleStopPlaceRepository;
import no.entur.uttu.repository.ServiceJourneyRepository;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
public class GtfsFlexExporterService {


    private final FlexibleStopPlaceRepository flexibleStopPlaceRepository;
    private final BookingArrangementRepository bookingArrangementRepository;
    private final ServiceJourneyRepository serviceJourneyRepository;
    private final FlexibleLineRepository flexibleLineRepository;
    public static final String EXPORT_DIR = "gtfsExports";

    private final Path exportPath;

    public GtfsFlexExporterService(FlexibleStopPlaceRepository flexibleStopPlaceRepository, BookingArrangementRepository bookingArrangementRepository, ServiceJourneyRepository serviceJourneyRepository, FlexibleLineRepository flexibleLineRepository, @Value("${uttu.storage.path:/tmp/uttu}") Path uttuStoragePath) {
        this.flexibleStopPlaceRepository = flexibleStopPlaceRepository;
        this.bookingArrangementRepository = bookingArrangementRepository;
        this.serviceJourneyRepository = serviceJourneyRepository;
        this.flexibleLineRepository = flexibleLineRepository;
        this.exportPath = uttuStoragePath.resolve(EXPORT_DIR);
    }

    @Transactional
    public void exportGtfsFlex(String provider, Long jobId) throws IOException {
        Path jobPath = exportPath.resolve(String.valueOf(jobId));
        File outputDir = new File(jobPath.toUri());
        outputDir.getParentFile().mkdirs();

        GtfsWriter writer = new GtfsWriter();
        writer.setOutputLocation(outputDir);
        generateAgencies(provider, writer);
        generateLocations(provider, jobId);
        generateLocationGroups(provider,  writer);
        generateBookingArrangements(provider, writer);
        generateTrips(provider, writer);
        generateStopTimes(provider, writer);
        generateRoutes(provider, writer);
        generateCalendars(provider, writer);
        writer.close();

        FileUtils.zipFilesInDirectory(jobPath.toAbsolutePath().toString(), jobId + ".zip");
        FileUtils.deleteFilesByExtension(jobPath.toAbsolutePath().toString(), ".txt");
        FileUtils.deleteFilesByExtension(jobPath.toAbsolutePath().toString(), ".geojson");

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

    private void generateStopTimes(String provider, GtfsWriter writer) {
        List<ServiceJourney> serviceJourneys = getServiceJourneysForProvider(provider);

        if (CollectionUtils.isEmpty(serviceJourneys)){
            return;
        }
        Set<String> alreadySeenStops = new HashSet<>();

        for (ServiceJourney serviceJourney : serviceJourneys) {
            List<StopTime> stopTimes = PassingTimeMapper.map(serviceJourney);
            for (StopTime stopTime : stopTimes) {
                writer.handleEntity(stopTime);

                if (stopTime.getStop() != null && !alreadySeenStops.contains(stopTime.getStop().getId().getId())){
                    Stop stop = new Stop();
                    stop.setId(stopTime.getStop().getId());
                    writer.handleEntity(stop);
                    alreadySeenStops.add(stopTime.getStop().getId().getId());
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

    private void generateLocationGroups(String provider, GtfsWriter writer) {
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
                if (CollectionUtils.isNotEmpty(groupStops)){
                    groupStops.forEach(writer::handleEntity);
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
