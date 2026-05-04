package no.entur.uttu.importer.gtfsflex.merger;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.IdentifiedEntity;
import no.entur.uttu.repository.generic.IdentifiedRepository;

import java.util.Map;
import java.util.Optional;

@Slf4j
public abstract class IdentifiedEntityBaseMerger<T extends IdentifiedEntity> implements Merger<T> {

    protected final EntityUpdater<T> updater;
    protected final IdentifiedRepository<T> repository;

    protected IdentifiedEntityBaseMerger(EntityUpdater<T> updater, IdentifiedRepository<T> repository) {
        this.updater = updater;
        this.repository = repository;
    }

    protected abstract Map<String, T> getDbEntitiesByOriginalId(Referential dbReferential);

    protected void mergeForeignEntities(T entity, Referential dbReferential) {
    }

    @Override
    public T merge(T entity, Referential dbReferential, boolean persist) {
        Map<String, T> originalIdToDbEntities = getDbEntitiesByOriginalId(dbReferential);
        Optional<T> dbEntityOpt = Optional.ofNullable(originalIdToDbEntities.getOrDefault(entity.getOriginalId(),
                null));
        if (entity.isMerged() && dbEntityOpt.isEmpty()) {
            throw new IllegalStateException(String.format("Entity entity %s (datasetId: %s, originalId: %s) not found in database", entity.getClass().getSimpleName(), entity.getDatasetId(), entity.getOriginalId()));
        }
        if (entity.isMerged()) {
            return dbEntityOpt.get();
        }
        log.info("Merging entity {} (datasetId: {}, originalId: {})", entity.getClass().getSimpleName(),
                entity.getDatasetId(), entity.getOriginalId());
        mergeForeignEntities(entity, dbReferential);
        entity.setMerged(true);
        if (dbEntityOpt.isEmpty()) {
            log.info("Entity does not exist in db");
            entity.checkPersistable();
            if (persist) {
                log.info("Persisting entity");
                entity = repository.save(entity);
            }
            originalIdToDbEntities.put(entity.getOriginalId(), entity);
            return entity;
        }
        T dbEntity = dbEntityOpt.get();
        entity.setVersion(dbEntity.getVersion());
        log.info("Entity already exists in db check if it has been updated");
        if (updater.update(entity, dbEntity)) {
            log.info("Entity has been updated and merged");
            dbEntity.checkPersistable();
            if (persist) {
                log.info("Persisting entity");
                dbEntity = repository.save(dbEntity);
            }
        } else {
            log.info("Entity has not been updated");
        }
        return dbEntity;
    }
}
