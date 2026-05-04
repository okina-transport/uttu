package no.entur.uttu.importer.gtfsflex;

import no.entur.uttu.importer.gtfsflex.merger.FlexibleLineMerger;
import no.entur.uttu.importer.gtfsflex.merger.StopPointInJourneyPatternMerger;
import no.entur.uttu.importer.gtfsflex.merger.TimetabledPassingTimeMerger;
import no.entur.uttu.model.FlexibleLine;
import no.entur.uttu.model.StopPointInJourneyPattern;
import no.entur.uttu.model.TimetabledPassingTime;
import no.entur.uttu.repository.*;
import org.springframework.stereotype.Service;

@Service
public class GtfsFlexMergerService {

    private final ProviderRepository providerRepository;
    private final CodespaceRepository codespaceRepository;
    private final NetworkRepository networkRepository;
    private final BookingArrangementRepository bookingArrangementRepository;
    private final ContactRepository contactRepository;
    private final DayTypeRepository dayTypeRepository;
    private final DayTypeAssignmentRepository dayTypeAssignmentRepository;
    private final OperatingPeriodRepository operatingPeriodRepository;
    private final FlexibleStopPlaceRepository flexibleStopPlaceRepository;
    private final StopRepository stopRepository;
    private final FlexibleAreaRepository flexibleAreaRepository;
    private final FlexibleLineRepository flexibleLineRepository;
    private final TimetabledPassingTimeRepository timetabledPassingTimeRepository;
    private final StopPointInJourneyPatternRepository stopPointInJourneyPatternRepository;
    private final DestinationDisplayRepository destinationDisplayRepository;
    private final JourneyPatternRepository journeyPatternRepository;
    private final ServiceJourneyRepository serviceJourneyRepository;
    private final FlexibleLineMerger flexibleLineMerger;
    private final TimetabledPassingTimeMerger timetabledPassingTimeMerger;
    private final StopPointInJourneyPatternMerger stopPointInJourneyPatternMerger;

    public GtfsFlexMergerService(ProviderRepository providerRepository, CodespaceRepository codespaceRepository, NetworkRepository networkRepository, BookingArrangementRepository bookingArrangementRepository, ContactRepository contactRepository, DayTypeRepository dayTypeRepository, DayTypeAssignmentRepository dayTypeAssignmentRepository, OperatingPeriodRepository operatingPeriodRepository, FlexibleStopPlaceRepository flexibleStopPlaceRepository, StopRepository stopRepository, FlexibleAreaRepository flexibleAreaRepository, FlexibleLineRepository flexibleLineRepository, TimetabledPassingTimeRepository timetabledPassingTimeRepository, StopPointInJourneyPatternRepository stopPointInJourneyPatternRepository, DestinationDisplayRepository destinationDisplayRepository, JourneyPatternRepository journeyPatternRepository, ServiceJourneyRepository serviceJourneyRepository, FlexibleLineMerger flexibleLineMerger, TimetabledPassingTimeMerger timetabledPassingTimeMerger, StopPointInJourneyPatternMerger stopPointInJourneyPatternMerger) {
        this.providerRepository = providerRepository;
        this.codespaceRepository = codespaceRepository;
        this.networkRepository = networkRepository;
        this.bookingArrangementRepository = bookingArrangementRepository;
        this.contactRepository = contactRepository;
        this.dayTypeRepository = dayTypeRepository;
        this.dayTypeAssignmentRepository = dayTypeAssignmentRepository;
        this.operatingPeriodRepository = operatingPeriodRepository;
        this.flexibleStopPlaceRepository = flexibleStopPlaceRepository;
        this.stopRepository = stopRepository;
        this.flexibleAreaRepository = flexibleAreaRepository;
        this.flexibleLineRepository = flexibleLineRepository;
        this.timetabledPassingTimeRepository = timetabledPassingTimeRepository;
        this.stopPointInJourneyPatternRepository = stopPointInJourneyPatternRepository;
        this.destinationDisplayRepository = destinationDisplayRepository;
        this.journeyPatternRepository = journeyPatternRepository;
        this.serviceJourneyRepository = serviceJourneyRepository;
        this.flexibleLineMerger = flexibleLineMerger;
        this.timetabledPassingTimeMerger = timetabledPassingTimeMerger;
        this.stopPointInJourneyPatternMerger = stopPointInJourneyPatternMerger;
    }

    public void merge(Referential gtfsReferential) {
        Referential dbReferential = getDbReferential(gtfsReferential);
        for (TimetabledPassingTime timetabledPassingTime : gtfsReferential.getTimetabledPassingTimesByOriginalId().values()) {
            timetabledPassingTimeMerger.merge(timetabledPassingTime, dbReferential, true);
        }

        for (StopPointInJourneyPattern stopPointInJourneyPattern : gtfsReferential.getStopPointInJourneyPatternsByOriginalId().values()) {
            stopPointInJourneyPatternMerger.merge(stopPointInJourneyPattern, dbReferential, true);
        }

        for (FlexibleLine line : gtfsReferential.getFlexibleLinesByOriginalId().values()) {
            flexibleLineMerger.merge(line, dbReferential, true);
        }
    }

    private Referential getDbReferential(Referential gtfsImportReferential) {
        Referential dbReferential = new Referential(gtfsImportReferential.getDataset());

        for (var provider : gtfsImportReferential.getProvidersByOriginalId().values()) {
            providerRepository.findByCode(provider.getCode()).ifPresent((entity -> dbReferential.getProvidersByOriginalId().put(provider.getOriginalId(), entity)));
        }

        for (var codespace : gtfsImportReferential.getCodespacesByOriginalId().values()) {
            codespaceRepository.findByXmlns(codespace.getXmlns()).ifPresent((entity -> dbReferential.getCodespacesByOriginalId().put(codespace.getOriginalId(), entity)));
        }

        for (var network : gtfsImportReferential.getNetworksByOriginalId().values()) {
            networkRepository.findByName(network.getName()).ifPresent((entity -> dbReferential.getNetworksByOriginalId().put(network.getOriginalId(), entity)));
        }

        for (var originalId : gtfsImportReferential.getBookingArrangementsByOriginalId().keySet()) {
            bookingArrangementRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getBookingArrangementsByOriginalId().put(originalId, entity)));
        }

        for (var originalId : gtfsImportReferential.getContactsByOriginalId().keySet()) {
            contactRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getContactsByOriginalId().put(originalId, entity)));
        }

        for (var originalId : gtfsImportReferential.getDayTypesByOriginalId().keySet()) {
            dayTypeRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getDayTypesByOriginalId().put(originalId, entity)));
        }

        for (var originalId : gtfsImportReferential.getDayTypeAssignmentsByOriginalId().keySet()) {
            dayTypeAssignmentRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getDayTypeAssignmentsByOriginalId().put(originalId, entity)));
        }

        for (var originalId : gtfsImportReferential.getOperatingPeriodByOriginalId().keySet()) {
            operatingPeriodRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getOperatingPeriodByOriginalId().put(originalId, entity)));
        }

        for (var originalId : gtfsImportReferential.getFlexibleStopPlacesByOriginalId().keySet()) {
            flexibleStopPlaceRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getFlexibleStopPlacesByOriginalId().put(originalId, entity)));
        }

        for (var originalId : gtfsImportReferential.getStopsByOriginalId().keySet()) {
            stopRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getStopsByOriginalId().put(originalId, entity)));
        }

        for (var originalId : gtfsImportReferential.getFlexibleAreasByOriginalId().keySet()) {
            flexibleAreaRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getFlexibleAreasByOriginalId().put(originalId, entity)));
        }

        for (var originalId : gtfsImportReferential.getFlexibleLinesByOriginalId().keySet()) {
            flexibleLineRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getFlexibleLinesByOriginalId().put(originalId, entity)));
        }

        for (var originalId : gtfsImportReferential.getTimetabledPassingTimesByOriginalId().keySet()) {
            timetabledPassingTimeRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getTimetabledPassingTimesByOriginalId().put(originalId, entity)));
        }

        for (var originalId : gtfsImportReferential.getStopPointInJourneyPatternsByOriginalId().keySet()) {
            stopPointInJourneyPatternRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getStopPointInJourneyPatternsByOriginalId().put(originalId, entity)));
        }

        for (var originalId : gtfsImportReferential.getDestinationDisplaysByOriginalId().keySet()) {
            destinationDisplayRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getDestinationDisplaysByOriginalId().put(originalId, entity)));
        }

        for (var originalId : gtfsImportReferential.getJourneyPatternsByOriginalId().keySet()) {
            journeyPatternRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getJourneyPatternsByOriginalId().put(originalId, entity)));
        }

        for (var originalId : gtfsImportReferential.getServiceJourneysByOriginalId().keySet()) {
            serviceJourneyRepository.findByDatasetIdAndOriginalId(gtfsImportReferential.getDataset(), originalId).ifPresent((entity -> dbReferential.getServiceJourneysByOriginalId().put(originalId, entity)));
        }

        return dbReferential;
    }

}
