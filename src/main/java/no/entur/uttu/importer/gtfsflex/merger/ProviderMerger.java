package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.Provider;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProviderMerger extends IdentifiedEntityBaseMerger<Provider> {

    private final CodespaceMerger codespaceMerger;

    protected ProviderMerger(EntityUpdater<Provider> updater, IdentifiedRepository<Provider> repository, CodespaceMerger codespaceMerger) {
        super(updater, repository);
        this.codespaceMerger = codespaceMerger;
    }

    @Override
    protected void mergeForeignEntities(Provider entity, Referential dbReferential) {
        super.mergeForeignEntities(entity, dbReferential);
        if (entity.getCodespace() != null) {
            entity.setCodespace(codespaceMerger.merge(entity.getCodespace(), dbReferential, true));
        }
    }

    @Override
    protected Map<String, Provider> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getProvidersByOriginalId();
    }
}
