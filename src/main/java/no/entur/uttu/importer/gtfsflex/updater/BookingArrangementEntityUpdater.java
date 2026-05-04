package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.BookingArrangement;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BookingArrangementEntityUpdater implements EntityUpdater<BookingArrangement> {

    @Override
    public boolean update(BookingArrangement newEntity, BookingArrangement dbEntity) {
        boolean updated = false;
        
        if (newEntity.getBookWhen() != dbEntity.getBookWhen()) {
            dbEntity.setBookWhen(newEntity.getBookWhen());
            updated = true;
        }

        if (!Objects.equals(newEntity.getMinimumBookingPeriod(), dbEntity.getMinimumBookingPeriod())) {
            dbEntity.setMinimumBookingPeriod(newEntity.getMinimumBookingPeriod());
            updated = true;
        }

        if (!Objects.equals(newEntity.getLatestBookingTime(), dbEntity.getLatestBookingTime())) {
            dbEntity.setLatestBookingTime(newEntity.getLatestBookingTime());
            updated = true;
        }

        if (!Objects.equals(newEntity.getBookingContact(), dbEntity.getBookingContact())) {
            dbEntity.setBookingContact(newEntity.getBookingContact());
            updated = true;
        }

        return updated;
    }

}
