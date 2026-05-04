package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.StopPointInJourneyPattern;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class StopPointInJourneyPatternEntityUpdater implements EntityUpdater<StopPointInJourneyPattern> {

    @Override
    public boolean update(StopPointInJourneyPattern newEntity, StopPointInJourneyPattern dbEntity) {
        boolean updated = false;

        if (newEntity.getOrder() != dbEntity.getOrder()) {
            dbEntity.setOrder(newEntity.getOrder());
            updated = true;
        }

        if (!Objects.equals(newEntity.getDestinationDisplay(), dbEntity.getDestinationDisplay())) {
            dbEntity.setDestinationDisplay(newEntity.getDestinationDisplay());
            updated = true;
        }

        if (!Objects.equals(newEntity.getProvider(), dbEntity.getProvider())) {
            dbEntity.setProvider(newEntity.getProvider());
            updated = true;
        }

        if (!Objects.equals(newEntity.getBookingArrangement(), dbEntity.getBookingArrangement())) {
            dbEntity.setBookingArrangement(newEntity.getBookingArrangement());
            updated = true;
        }

        if (!Objects.equals(newEntity.getStop(), dbEntity.getStop())) {
            dbEntity.setStop(newEntity.getStop());
            updated = true;
        }

        if (!Objects.equals(newEntity.getFlexibleStopPlace(), dbEntity.getFlexibleStopPlace())) {
            dbEntity.setFlexibleStopPlace(newEntity.getFlexibleStopPlace());
            updated = true;
        }

        if (!Objects.equals(newEntity.getJourneyPattern(), dbEntity.getJourneyPattern())) {
            dbEntity.setJourneyPattern(newEntity.getJourneyPattern());
            updated = true;
        }

        if (!Objects.equals(newEntity.getForBoarding(), dbEntity.getForBoarding())) {
            dbEntity.setForBoarding(newEntity.getForBoarding());
            updated = true;
        }

        if (!Objects.equals(newEntity.getForAlighting(), dbEntity.getForAlighting())) {
            dbEntity.setForAlighting(newEntity.getForAlighting());
            updated = true;
        }

        return updated;
    }
}
