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

        if (!Objects.equals(newEntity.getPriorNoticeDurationMax(), dbEntity.getPriorNoticeDurationMax())) {
            dbEntity.setPriorNoticeDurationMax(newEntity.getPriorNoticeDurationMax());
            updated = true;
        }

        if (!Objects.equals(newEntity.getPriorNoticeLastDay(), dbEntity.getPriorNoticeLastDay())) {
            dbEntity.setPriorNoticeLastDay(newEntity.getPriorNoticeLastDay());
            updated = true;
        }

        if (!Objects.equals(newEntity.getPriorNoticeStartDay(), dbEntity.getPriorNoticeStartDay())) {
            dbEntity.setPriorNoticeStartDay(newEntity.getPriorNoticeStartDay());
            updated = true;
        }

        if (!Objects.equals(newEntity.getPriorNoticeStartTime(), dbEntity.getPriorNoticeStartTime())) {
            dbEntity.setPriorNoticeStartTime(newEntity.getPriorNoticeStartTime());
            updated = true;
        }

        if (!Objects.equals(newEntity.getServiceId(), dbEntity.getServiceId())) {
            dbEntity.setServiceId(newEntity.getServiceId());
            updated = true;
        }



        return updated;
    }

}
