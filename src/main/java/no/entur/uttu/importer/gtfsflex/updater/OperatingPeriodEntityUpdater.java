package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.OperatingPeriod;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class OperatingPeriodEntityUpdater implements EntityUpdater<OperatingPeriod> {

    @Override
    public boolean update(OperatingPeriod newEntity, OperatingPeriod dbEntity) {
        boolean updated = false;

        if (!Objects.equals(newEntity.getFromDate(), dbEntity.getFromDate())) {
            dbEntity.setFromDate(newEntity.getFromDate());
            updated = true;
        }

        if (!Objects.equals(newEntity.getToDate(), dbEntity.getToDate())) {
            dbEntity.setToDate(newEntity.getToDate());
            updated = true;
        }

        return updated;
    }

}
