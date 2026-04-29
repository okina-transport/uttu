package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.DestinationDisplay;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DestinationDisplayEntityUpdater implements EntityUpdater<DestinationDisplay> {
    @Override
    public boolean update(DestinationDisplay newEntity, DestinationDisplay dbEntity) {
        boolean updated = false;

        if (!Objects.equals(newEntity.getProvider(), dbEntity.getProvider())) {
            dbEntity.setProvider(newEntity.getProvider());
            updated = true;
        }

        if (!Strings.CS.equals(newEntity.getFrontText(), dbEntity.getFrontText())) {
            dbEntity.setFrontText(newEntity.getFrontText());
            updated = true;
        }

        return updated;
    }
}
