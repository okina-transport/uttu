package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.FlexibleLine;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FlexibleLineMerger extends ProviderEntityBaseMerger<FlexibleLine> {

    private final BookingArrangementMerger bookingArrangementMerger;
    private final NetworkMerger networkMerger;

    protected FlexibleLineMerger(EntityUpdater<FlexibleLine> updater, IdentifiedRepository<FlexibleLine> repository, ProviderMerger providerMerger, BookingArrangementMerger bookingArrangementMerger, NetworkMerger networkMerger) {
        super(updater, repository, providerMerger);
        this.bookingArrangementMerger = bookingArrangementMerger;
        this.networkMerger = networkMerger;
    }

    @Override
    protected Map<String, FlexibleLine> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getFlexibleLinesByOriginalId();
    }

    @Override
    protected void mergeForeignEntities(FlexibleLine entity, Referential dbReferential) {
        super.mergeForeignEntities(entity, dbReferential);
        if (entity.getNetwork() != null) {
            entity.setNetwork(networkMerger.merge(entity.getNetwork(), dbReferential, true));
        }
        if (entity.getBookingArrangement() != null) {
            entity.setBookingArrangement(bookingArrangementMerger.merge(entity.getBookingArrangement(), dbReferential, true));
        }
    }
}
