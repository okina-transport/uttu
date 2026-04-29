package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.GroupOfEntitiesVersionStructure;
import no.entur.uttu.model.Network;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Component;

@Component
public class NetworkUpdater implements EntityUpdater<Network> {

    private final EntityUpdater<GroupOfEntitiesVersionStructure> groupOfEntitiesVersionStructureEntityUpdater;

    public NetworkUpdater(EntityUpdater<GroupOfEntitiesVersionStructure> groupOfEntitiesVersionStructureEntityUpdater) {
        this.groupOfEntitiesVersionStructureEntityUpdater = groupOfEntitiesVersionStructureEntityUpdater;
    }

    @Override
    public boolean update(Network newEntity, Network dbEntity) {
        boolean updated = groupOfEntitiesVersionStructureEntityUpdater.update(newEntity, dbEntity);

        if (!Strings.CS.equals(newEntity.getAuthorityRef(), dbEntity.getAuthorityRef())) {
            dbEntity.setAuthorityRef(newEntity.getAuthorityRef());
            updated = true;
        }

        return updated;
    }
}
