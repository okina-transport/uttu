package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.TimetabledPassingTime;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class TimetabledPassingTimeMerger extends ProviderEntityBaseMerger<TimetabledPassingTime> {

    private final ServiceJourneyMerger serviceJourneyMerger;

    protected TimetabledPassingTimeMerger(EntityUpdater<TimetabledPassingTime> updater, IdentifiedRepository<TimetabledPassingTime> repository, ProviderMerger providerMerger, ServiceJourneyMerger serviceJourneyMerger) {
        super(updater, repository, providerMerger);
        this.serviceJourneyMerger = serviceJourneyMerger;
    }

    @Override
    protected Map<String, TimetabledPassingTime> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getTimetabledPassingTimesByOriginalId();
    }

    @Override
    protected void mergeForeignEntities(TimetabledPassingTime entity, Referential dbReferential) {
        super.mergeForeignEntities(entity, dbReferential);
        if (entity.getServiceJourney() != null) {
            entity.setServiceJourney(serviceJourneyMerger.merge(entity.getServiceJourney(), dbReferential, true));
        }
    }
}
