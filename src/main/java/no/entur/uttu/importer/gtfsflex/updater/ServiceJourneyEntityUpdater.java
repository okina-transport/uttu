package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.GroupOfEntitiesVersionStructure;
import no.entur.uttu.model.ServiceJourney;
import no.entur.uttu.util.CollectionUtil;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ServiceJourneyEntityUpdater implements EntityUpdater<ServiceJourney> {

    private final EntityUpdater<GroupOfEntitiesVersionStructure> groupOfEntitiesVersionStructureEntityUpdater;

    public ServiceJourneyEntityUpdater(EntityUpdater<GroupOfEntitiesVersionStructure> groupOfEntitiesVersionStructureUpdater) {
        groupOfEntitiesVersionStructureEntityUpdater = groupOfEntitiesVersionStructureUpdater;
    }

    @Override
    public boolean update(ServiceJourney newEntity, ServiceJourney dbEntity) {
        boolean updated = groupOfEntitiesVersionStructureEntityUpdater.update(newEntity, dbEntity);

        if (!CollectionUtil.isEqualCollectionNullSafe(newEntity.getDayTypes(), dbEntity.getDayTypes())) {
            dbEntity.updateDayTypes(newEntity.getDayTypes());
            updated = true;
        }

        if (!Objects.equals(newEntity.getJourneyPattern(), dbEntity.getJourneyPattern())) {
            dbEntity.setJourneyPattern(newEntity.getJourneyPattern());
            updated = true;
        }

        if (!Strings.CS.equals(newEntity.getOperatorRef(), dbEntity.getOperatorRef())) {
            dbEntity.setOperatorRef(newEntity.getOperatorRef());
            updated = true;
        }

        return updated;
    }

}
