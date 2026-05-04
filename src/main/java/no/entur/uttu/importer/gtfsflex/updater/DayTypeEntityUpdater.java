package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.DayType;
import no.entur.uttu.util.CollectionUtil;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DayTypeEntityUpdater implements EntityUpdater<DayType> {

    @Override
    public boolean update(DayType newEntity, DayType dbEntity) {
        boolean updated = false;

        if (!Strings.CS.equals(newEntity.getName(), dbEntity.getName())) {
            dbEntity.setName(newEntity.getName());
            updated = true;
        }

        if (!Objects.equals(newEntity.getProvider(), dbEntity.getProvider())) {
            dbEntity.setProvider(newEntity.getProvider());
            updated = true;
        }

        if (!CollectionUtil.isEqualCollectionNullSafe(newEntity.getDaysOfWeek(), dbEntity.getDaysOfWeek())) {
            dbEntity.setDaysOfWeek(newEntity.getDaysOfWeek());
            updated = true;
        }

        if (!CollectionUtil.isEqualCollectionNullSafe(newEntity.getDayTypeAssignments(), dbEntity.getDayTypeAssignments())) {
            dbEntity.setDayTypeAssignments(newEntity.getDayTypeAssignments());
            updated = true;
        }

        return updated;
    }

}
