package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.ProviderEntity;
import no.entur.uttu.repository.generic.IdentifiedRepository;


public abstract class ProviderEntityBaseMerger<T extends ProviderEntity> extends IdentifiedEntityBaseMerger<T> {

    protected final ProviderMerger providerMerger;

    protected ProviderEntityBaseMerger(EntityUpdater<T> updater, IdentifiedRepository<T> repository, ProviderMerger providerMerger) {
        super(updater, repository);
        this.providerMerger = providerMerger;
    }

    @Override
    protected void mergeForeignEntities(T entity, Referential dbReferential) {
        super.mergeForeignEntities(entity, dbReferential);
        if (entity.getProvider() != null) {
            entity.setProvider(providerMerger.merge(entity.getProvider(), dbReferential, true));
        }
    }
}
