package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.FlexibleLine;
import no.entur.uttu.model.GroupOfEntitiesVersionStructure;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FlexibleLineEntityUpdater implements EntityUpdater<FlexibleLine> {

    private final EntityUpdater<GroupOfEntitiesVersionStructure> groupOfEntitiesVersionStructureEntityUpdater;

    public FlexibleLineEntityUpdater(EntityUpdater<GroupOfEntitiesVersionStructure> groupOfEntitiesVersionStructureEntityUpdater) {
        this.groupOfEntitiesVersionStructureEntityUpdater = groupOfEntitiesVersionStructureEntityUpdater;
    }

    @Override
    public boolean update(FlexibleLine newEntity, FlexibleLine dbEntity) {
        boolean updated = groupOfEntitiesVersionStructureEntityUpdater.update(newEntity, dbEntity);

        if (!Strings.CS.equals(newEntity.getPublicCode(), dbEntity.getPublicCode())) {
            dbEntity.setPublicCode(newEntity.getPublicCode());
            updated = true;
        }

        if (newEntity.getTransportMode() != dbEntity.getTransportMode()) {
            dbEntity.setTransportMode(newEntity.getTransportMode());
            updated = true;
        }

        if (newEntity.getTransportSubmode() != dbEntity.getTransportSubmode()) {
            dbEntity.setTransportSubmode(newEntity.getTransportSubmode());
            updated = true;
        }

        if (!Objects.equals(newEntity.getFlexibleLineType(), dbEntity.getFlexibleLineType())) {
            dbEntity.setFlexibleLineType(newEntity.getFlexibleLineType());
            updated = true;
        }

        if (!Objects.equals(newEntity.getNetwork(), dbEntity.getNetwork())) {
            dbEntity.setNetwork(newEntity.getNetwork());
            updated = true;
        }

        return updated;
    }
}
