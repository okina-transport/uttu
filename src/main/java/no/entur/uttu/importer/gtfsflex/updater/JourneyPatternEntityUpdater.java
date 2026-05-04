package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.GroupOfEntitiesVersionStructure;
import no.entur.uttu.model.JourneyPattern;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class JourneyPatternEntityUpdater implements EntityUpdater<JourneyPattern> {

    private final EntityUpdater<GroupOfEntitiesVersionStructure> groupOfEntitiesVersionStructureEntityUpdater;

    public JourneyPatternEntityUpdater(EntityUpdater<GroupOfEntitiesVersionStructure> groupOfEntitiesVersionStructureUpdater) {
        groupOfEntitiesVersionStructureEntityUpdater = groupOfEntitiesVersionStructureUpdater;
    }

    @Override
    public boolean update(JourneyPattern newEntity, JourneyPattern dbEntity) {
        boolean updated = groupOfEntitiesVersionStructureEntityUpdater.update(newEntity, dbEntity);

        if (!Objects.equals(newEntity.getLine(), dbEntity.getLine())) {
            dbEntity.setLine(newEntity.getLine());
            updated = true;
        }

        if (newEntity.getDirectionType() != dbEntity.getDirectionType()) {
            dbEntity.setDirectionType(newEntity.getDirectionType());
            updated = true;
        }

        return updated;
    }

}
