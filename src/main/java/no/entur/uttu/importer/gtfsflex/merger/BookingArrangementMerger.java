package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.BookingArrangement;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class BookingArrangementMerger extends IdentifiedEntityBaseMerger<BookingArrangement> {

    private final ContactMerger contactMerger;

    protected BookingArrangementMerger(EntityUpdater<BookingArrangement> updater, IdentifiedRepository<BookingArrangement> repository, ContactMerger contactMerger) {
        super(updater, repository);
        this.contactMerger = contactMerger;
    }

    @Override
    protected Map<String, BookingArrangement> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getBookingArrangementsByOriginalId();
    }

    @Override
    protected void mergeForeignEntities(BookingArrangement entity, Referential dbReferential) {
        super.mergeForeignEntities(entity, dbReferential);
        if (entity.getBookingContact() != null) {
            entity.setBookingContact(contactMerger.merge(entity.getBookingContact(), dbReferential, true));
        }
    }
}
