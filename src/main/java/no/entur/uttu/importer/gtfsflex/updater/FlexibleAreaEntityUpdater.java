package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.FlexibleArea;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class FlexibleAreaEntityUpdater implements EntityUpdater<FlexibleArea> {

    @Override
    public boolean update(FlexibleArea newEntity, FlexibleArea dbEntity) {
        boolean updated = false;

        if (!Objects.equals(newEntity.getFlexibleStopPlace(), dbEntity.getFlexibleStopPlace())) {
            dbEntity.setFlexibleStopPlace(newEntity.getFlexibleStopPlace());
            updated = true;
        }

        if (!Objects.equals(newEntity.getPolygon(), dbEntity.getPolygon())) {
            dbEntity.setPolygon(newEntity.getPolygon());
            updated = true;
        }

        return updated;
    }

}
