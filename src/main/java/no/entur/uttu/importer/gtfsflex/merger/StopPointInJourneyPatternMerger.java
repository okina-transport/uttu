package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.StopPointInJourneyPattern;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StopPointInJourneyPatternMerger extends ProviderEntityBaseMerger<StopPointInJourneyPattern> {

    private final BookingArrangementMerger bookingArrangementMerger;
    private final DestinationDisplayMerger destinationDisplayMerger;
    private final StopMerger stopMerger;
    private final FlexibleStopPlaceMerger flexibleStopPlaceMerger;
    private final JourneyPatternMerger journeyPatternMerger;

    protected StopPointInJourneyPatternMerger(EntityUpdater<StopPointInJourneyPattern> updater, IdentifiedRepository<StopPointInJourneyPattern> repository, ProviderMerger providerMerger, BookingArrangementMerger bookingArrangementMerger, DestinationDisplayMerger destinationDisplayMerger, StopMerger stopMerger, FlexibleStopPlaceMerger flexibleStopPlaceMerger, JourneyPatternMerger journeyPatternMerger) {
        super(updater, repository, providerMerger);
        this.bookingArrangementMerger = bookingArrangementMerger;
        this.destinationDisplayMerger = destinationDisplayMerger;
        this.stopMerger = stopMerger;
        this.flexibleStopPlaceMerger = flexibleStopPlaceMerger;
        this.journeyPatternMerger = journeyPatternMerger;
    }

    @Override
    protected Map<String, StopPointInJourneyPattern> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getStopPointInJourneyPatternsByOriginalId();
    }

    @Override
    protected void mergeForeignEntities(StopPointInJourneyPattern entity, Referential dbReferential) {
        super.mergeForeignEntities(entity, dbReferential);
        if (entity.getBookingArrangement() != null) {
            entity.setBookingArrangement(bookingArrangementMerger.merge(entity.getBookingArrangement(), dbReferential, true));
        }
        if (entity.getDestinationDisplay() != null) {
            entity.setDestinationDisplay(destinationDisplayMerger.merge(entity.getDestinationDisplay(), dbReferential, true));
        }
        if (entity.getStop() != null) {
            entity.setStop(stopMerger.merge(entity.getStop(), dbReferential, true));
        }
        if (entity.getFlexibleStopPlace() != null) {
            entity.setFlexibleStopPlace(flexibleStopPlaceMerger.merge(entity.getFlexibleStopPlace(), dbReferential, true));
        }
        if (entity.getJourneyPattern() != null) {
            entity.setJourneyPattern(journeyPatternMerger.merge(entity.getJourneyPattern(), dbReferential, true));
        }
    }
}
