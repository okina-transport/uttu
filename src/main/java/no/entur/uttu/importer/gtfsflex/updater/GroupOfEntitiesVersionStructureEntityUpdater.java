package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.GroupOfEntitiesVersionStructure;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class GroupOfEntitiesVersionStructureEntityUpdater implements EntityUpdater<GroupOfEntitiesVersionStructure> {

    @Override
    public boolean update(GroupOfEntitiesVersionStructure newEntity, GroupOfEntitiesVersionStructure dbEntity) {
        boolean updated = false;

        if (!Objects.equals(newEntity.getProvider(), dbEntity.getProvider())) {
            dbEntity.setProvider(newEntity.getProvider());
            updated = true;
        }

        if (!Strings.CS.equals(newEntity.getName(), dbEntity.getName())) {
            dbEntity.setName(newEntity.getName());
            updated = true;
        }

        if (!Strings.CS.equals(newEntity.getShortName(), dbEntity.getShortName())) {
            dbEntity.setShortName(newEntity.getShortName());
            updated = true;
        }

        if (!Strings.CS.equals(newEntity.getDescription(), dbEntity.getDescription())) {
            dbEntity.setDescription(newEntity.getDescription());
            updated = true;
        }

        if (!Strings.CS.equals(newEntity.getPrivateCode(), dbEntity.getPrivateCode())) {
            dbEntity.setPrivateCode(newEntity.getPrivateCode());
            updated = true;
        }

        return updated;
    }
}
