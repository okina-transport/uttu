package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.DayTypeAssignment;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DayTypeAssignmentEntityUpdater implements EntityUpdater<DayTypeAssignment> {

    @Override
    public boolean update(DayTypeAssignment newEntity, DayTypeAssignment dbEntity) {
        boolean updated = false;

        if (!Objects.equals(newEntity.getOperatingPeriod(), dbEntity.getOperatingPeriod())) {
            dbEntity.setOperatingPeriod(newEntity.getOperatingPeriod());
            updated = true;
        }

        if (!Objects.equals(newEntity.getAvailable(), dbEntity.getAvailable())) {
            dbEntity.setAvailable(newEntity.getAvailable());
            updated = true;
        }

        if (!Objects.equals(newEntity.getDate(), dbEntity.getDate())) {
            dbEntity.setDate(newEntity.getDate());
            updated = true;
        }

        return updated;
    }

}
