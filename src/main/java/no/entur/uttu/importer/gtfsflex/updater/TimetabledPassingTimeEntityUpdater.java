package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.TimetabledPassingTime;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TimetabledPassingTimeEntityUpdater implements EntityUpdater<TimetabledPassingTime> {

    @Override
    public boolean update(TimetabledPassingTime newEntity, TimetabledPassingTime dbEntity) {
        boolean updated = false;

        if (!Objects.equals(newEntity.getServiceJourney(), dbEntity.getServiceJourney())) {
            dbEntity.setServiceJourney(newEntity.getServiceJourney());
            updated = true;
        }

        if (newEntity.getOrder() != dbEntity.getOrder()) {
            dbEntity.setOrder(newEntity.getOrder());
            updated = true;
        }

        if (!Objects.equals(newEntity.getArrivalTime(), dbEntity.getArrivalTime())) {
            dbEntity.setArrivalTime(newEntity.getArrivalTime());
            updated = true;
        }

        if (newEntity.getArrivalDayOffset() != dbEntity.getArrivalDayOffset()) {
            dbEntity.setArrivalDayOffset(newEntity.getArrivalDayOffset());
            updated = true;
        }

        if (!Objects.equals(newEntity.getDepartureTime(), dbEntity.getDepartureTime())) {
            dbEntity.setDepartureTime(newEntity.getDepartureTime());
            updated = true;
        }

        if (newEntity.getDepartureDayOffset() != dbEntity.getDepartureDayOffset()) {
            dbEntity.setDepartureDayOffset(newEntity.getDepartureDayOffset());
            updated = true;
        }

        if (!Objects.equals(newEntity.getEarliestDepartureTime(), dbEntity.getEarliestDepartureTime())) {
            dbEntity.setEarliestDepartureTime(newEntity.getEarliestDepartureTime());
            updated = true;
        }

        if (newEntity.getEarliestDepartureDayOffset() != dbEntity.getEarliestDepartureDayOffset()) {
            dbEntity.setEarliestDepartureDayOffset(newEntity.getEarliestDepartureDayOffset());
            updated = true;
        }

        if (!Objects.equals(newEntity.getLatestArrivalTime(), dbEntity.getLatestArrivalTime())) {
            dbEntity.setLatestArrivalTime(newEntity.getLatestArrivalTime());
            updated = true;
        }

        if (newEntity.getLatestArrivalDayOffset() != dbEntity.getLatestArrivalDayOffset()) {
            dbEntity.setLatestArrivalDayOffset(newEntity.getLatestArrivalDayOffset());
            updated = true;
        }

        if (!Objects.equals(newEntity.getProvider(), dbEntity.getProvider())) {
            dbEntity.setProvider(newEntity.getProvider());
            updated = true;
        }

        return updated;
    }

}
