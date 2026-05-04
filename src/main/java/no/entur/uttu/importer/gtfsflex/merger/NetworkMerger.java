package no.entur.uttu.importer.gtfsflex.merger;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.Network;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class NetworkMerger extends ProviderEntityBaseMerger<Network> {

    protected NetworkMerger(EntityUpdater<Network> updater, IdentifiedRepository<Network> repository, ProviderMerger providerMerger) {
        super(updater, repository, providerMerger);
    }

    @Override
    protected Map<String, Network> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getNetworksByOriginalId();
    }
}
