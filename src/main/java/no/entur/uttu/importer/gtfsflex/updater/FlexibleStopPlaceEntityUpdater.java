package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.FlexibleStopPlace;
import no.entur.uttu.util.CollectionUtil;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FlexibleStopPlaceEntityUpdater implements EntityUpdater<FlexibleStopPlace> {

    private final GroupOfEntitiesVersionStructureEntityUpdater groupOfEntitiesVersionStructureEntityUpdater;

    public FlexibleStopPlaceEntityUpdater(GroupOfEntitiesVersionStructureEntityUpdater groupOfEntitiesVersionStructureEntityUpdater) {
        this.groupOfEntitiesVersionStructureEntityUpdater = groupOfEntitiesVersionStructureEntityUpdater;
    }

    @Override
    public boolean update(FlexibleStopPlace newEntity, FlexibleStopPlace dbEntity) {
        boolean updated = groupOfEntitiesVersionStructureEntityUpdater.update(newEntity, dbEntity);

        if (!Objects.equals(newEntity.getTransportMode(), dbEntity.getTransportMode())) {
            dbEntity.setTransportMode(newEntity.getTransportMode());
            updated = true;
        }
        
        if (!CollectionUtil.isEqualCollectionNullSafe(newEntity.getStops(), dbEntity.getStops())) {
            dbEntity.setStops(newEntity.getStops());
            updated = true;
        }

        return updated;
    }

}
