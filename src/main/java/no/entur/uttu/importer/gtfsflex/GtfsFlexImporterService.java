package no.entur.uttu.importer.gtfsflex;

import lombok.extern.slf4j.Slf4j;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.model.*;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;

@Service
@Slf4j
public class GtfsFlexImporterService {

    private final GtfsFlexMapperService gtfsFlexMapperService;
    private final GtfsFlexIdMapperService gtfsFlexIdMapperService;
    private final GtfsFlexMergerService gtfsFlexMergerService;

    public GtfsFlexImporterService(GtfsFlexMapperService gtfsFlexMapperService, GtfsFlexIdMapperService gtfsFlexIdMapperService, GtfsFlexMergerService gtfsFlexMergerService) {
        this.gtfsFlexMapperService = gtfsFlexMapperService;
        this.gtfsFlexIdMapperService = gtfsFlexIdMapperService;
        this.gtfsFlexMergerService = gtfsFlexMergerService;
    }

    @Transactional
    public Referential importGtfsFlex(File gtfsZip, String datasetId) throws IOException {
        GtfsReader gtfsReader = readGtfsFlexEntitiesFromGtfsZip(gtfsZip);
        Referential gtfsImportReferential = gtfsFlexMapperService.mapGtfsFlexToNetex(gtfsReader, datasetId);
        gtfsFlexIdMapperService.mapIds(gtfsImportReferential, datasetId);
        gtfsFlexMergerService.merge(gtfsImportReferential);
        return gtfsImportReferential;
    }

    /**
     * Parse GTFS flex entities from a GTFS ZIP file
     *
     * @param gtfsZip Gtfs archive
     * @return gtfsReader with entities read
     * @throws IOException when reading entities fail
     */
    public GtfsReader readGtfsFlexEntitiesFromGtfsZip(File gtfsZip) throws IOException {
        GtfsReader gtfsReader = new GtfsReader();
        gtfsReader.setInputLocation(gtfsZip);
        GtfsDaoImpl store = new GtfsDaoImpl();
        gtfsReader.setEntityStore(store);
        return readGtfsFlexEntities(gtfsReader);
    }

    /**
     * Read GTFS flex entities from a GTFS reader
     * InputLocation and EntityStore must be set before calling this method.
     *
     * @param gtfsReader gtfsReader to read entities from
     * @return gtfsReader with entities read
     * @throws IOException when reading entities fail
     */
    public GtfsReader readGtfsFlexEntities(GtfsReader gtfsReader) throws IOException {
        log.info("Reading GTFS flex entities");
        gtfsReader.readEntities(Agency.class);
        gtfsReader.readEntities(ServiceCalendar.class);
        gtfsReader.readEntities(ServiceCalendarDate.class);
        gtfsReader.readEntities(Stop.class);
        gtfsReader.readEntities(LocationGroup.class);
        gtfsReader.readEntities(LocationGroupElement.class);
        gtfsReader.readEntities(Location.class);
        gtfsReader.readEntities(Route.class);
        gtfsReader.readEntities(Trip.class);
        gtfsReader.readEntities(BookingRule.class);
        gtfsReader.readEntities(StopTime.class);
        log.info("Finished reading GTFS flex entities");
        return gtfsReader;
    }

}
