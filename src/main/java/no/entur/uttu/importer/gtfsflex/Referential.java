package no.entur.uttu.importer.gtfsflex;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import no.entur.uttu.model.*;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores all NETEX entities mapped from GTFS flex data.
 */
@Getter
public class Referential {
    private final String dataset;
    private final Map<String, Provider> providersByOriginalId = new HashMap<>();
    private final Map<String, Codespace> codespacesByOriginalId = new HashMap<>();
    private final Map<String, Network> networksByOriginalId = new HashMap<>();
    private final Map<String, BookingArrangement> bookingArrangementsByOriginalId = new HashMap<>();
    private final Map<String, Contact> contactsByOriginalId = new HashMap<>();
    private final Map<String, DayType> dayTypesByOriginalId = new HashMap<>();
    private final Map<String, DayTypeAssignment> dayTypeAssignmentsByOriginalId = new HashMap<>();
    private final Map<String, OperatingPeriod> operatingPeriodByOriginalId = new HashMap<>();
    private final Map<String, FlexibleStopPlace> flexibleStopPlacesByOriginalId = new HashMap<>();
    private final Map<String, Stop> stopsByOriginalId = new HashMap<>();
    private final Map<String, FlexibleArea> flexibleAreasByOriginalId = new HashMap<>();
    private final Map<String, FlexibleLine> flexibleLinesByOriginalId = new HashMap<>();
    private final Map<String, TimetabledPassingTime> timetabledPassingTimesByOriginalId = new HashMap<>();
    private final Map<String, StopPointInJourneyPattern> stopPointInJourneyPatternsByOriginalId = new HashMap<>();
    private final Map<String, DestinationDisplay> destinationDisplaysByOriginalId = new HashMap<>();
    private final Map<String, JourneyPattern> journeyPatternsByOriginalId = new HashMap<>();
    private final Map<String, ServiceJourney> serviceJourneysByOriginalId = new HashMap<>();

    public Referential(String dataset) {
        this.dataset = dataset;
    }

    public Provider getProvider(@NotNull String originalId) {
        return providersByOriginalId.computeIfAbsent(originalId, id -> {
            var entity = new Provider();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public Codespace getCodespace(@NotNull String agencyId) {
        return codespacesByOriginalId.computeIfAbsent(agencyId, id -> {
            var entity = new Codespace();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public Network getNetwork(@NotNull String agencyId) {
        return networksByOriginalId.computeIfAbsent(agencyId, id -> {
            var entity = new Network();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public BookingArrangement getBookingArrangement(@NotNull String originalId) {
        return bookingArrangementsByOriginalId.computeIfAbsent(originalId, id -> {
            var entity = new BookingArrangement();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public Contact getContact(@NotNull String originalId) {
        return contactsByOriginalId.computeIfAbsent(originalId, id -> {
            var entity = new Contact();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public DayType getDayType(@NotNull String serviceId) {
        return dayTypesByOriginalId.computeIfAbsent(serviceId, id -> {
            var entity = new DayType();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public DayTypeAssignment getDayTypeAssignmentByServiceId(@NotNull String originalId) {
        return dayTypeAssignmentsByOriginalId.computeIfAbsent(originalId, id -> {
            var entity = new DayTypeAssignment();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public DayTypeAssignment getDayTypeAssignmentByServiceIdAndServiceDate(@NotNull String serviceId, ServiceDate serviceDate) {
        return getDayTypeAssignmentByServiceId(serviceId + "_" + serviceDate.getAsString());
    }

    public OperatingPeriod getOperatingPeriod(@NotNull String originalId) {
        return operatingPeriodByOriginalId.computeIfAbsent(originalId, id -> {
            var entity = new OperatingPeriod();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public FlexibleStopPlace getFlexibleStopPlace(@NotNull String originalId) {
        return flexibleStopPlacesByOriginalId.computeIfAbsent(originalId, id -> {
            var entity = new FlexibleStopPlace();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public Stop getStop(@NotNull String originalId) {
        return stopsByOriginalId.computeIfAbsent(originalId, id -> {
            var entity = new Stop();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public FlexibleArea getFlexibleArea(@NotNull String originalId) {
        return flexibleAreasByOriginalId.computeIfAbsent(originalId, id -> {
            var entity = new FlexibleArea();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public FlexibleArea getFlexibleArea(@NotNull String originalId, int index) {
        return getFlexibleArea(originalId + "_" + index);
    }

    public FlexibleLine getFlexibleLine(@NotNull String routeId) {
        return flexibleLinesByOriginalId.computeIfAbsent(routeId, id -> {
            var entity = new FlexibleLine();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public JourneyPattern getJourneyPattern(@NotNull String originalId) {
        return journeyPatternsByOriginalId.computeIfAbsent(originalId, id -> {
            var entity = new JourneyPattern();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public ServiceJourney getServiceJourney(@NotNull String originalId) {
        return serviceJourneysByOriginalId.computeIfAbsent(originalId, id -> {
            var entity = new ServiceJourney();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public StopPointInJourneyPattern getStopPointInJourneyPattern(@NotNull String originalId, int stopSequence) {
        return stopPointInJourneyPatternsByOriginalId.computeIfAbsent(originalId + "_" + stopSequence, id -> {
            var entity = new StopPointInJourneyPattern();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public TimetabledPassingTime getTimetabledPassingTime(@NotNull String tripId, int stopSequence) {
        return timetabledPassingTimesByOriginalId.computeIfAbsent(tripId + "_" + stopSequence, id -> {
            var entity = new TimetabledPassingTime();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }

    public DestinationDisplay getDestinationDisplay(@NotNull String tripId, int stopSequence) {
        return destinationDisplaysByOriginalId.computeIfAbsent(tripId + "_" + stopSequence, id -> {
            var entity = new DestinationDisplay();
            entity.setDatasetId(this.dataset);
            entity.setOriginalId(id);
            return entity;
        });
    }
}