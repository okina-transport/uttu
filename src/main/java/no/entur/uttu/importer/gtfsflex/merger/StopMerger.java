package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.Stop;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StopMerger extends ProviderEntityBaseMerger<Stop> {
    protected StopMerger(EntityUpdater<Stop> updater, IdentifiedRepository<Stop> repository, ProviderMerger providerMerger) {
        super(updater, repository, providerMerger);
    }

    @Override
    protected Map<String, Stop> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getStopsByOriginalId();
    }
}
