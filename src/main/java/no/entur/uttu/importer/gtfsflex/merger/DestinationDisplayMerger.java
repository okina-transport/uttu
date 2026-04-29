package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.DestinationDisplay;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DestinationDisplayMerger extends ProviderEntityBaseMerger<DestinationDisplay> {
    protected DestinationDisplayMerger(EntityUpdater<DestinationDisplay> updater, IdentifiedRepository<DestinationDisplay> repository, ProviderMerger providerMerger) {
        super(updater, repository, providerMerger);
    }

    @Override
    protected Map<String, DestinationDisplay> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getDestinationDisplaysByOriginalId();
    }
}
