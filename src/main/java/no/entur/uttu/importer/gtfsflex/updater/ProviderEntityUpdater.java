package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.Provider;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ProviderEntityUpdater implements EntityUpdater<Provider> {

    @Override
    public boolean update(Provider newEntity, Provider dbEntity) {
        boolean updated = false;

        if (!Strings.CS.equals(newEntity.getName(), dbEntity.getName())) {
            dbEntity.setName(newEntity.getName());
            updated = true;
        }

        if (!Strings.CS.equals(newEntity.getCode(), dbEntity.getCode())) {
            dbEntity.setCode(newEntity.getCode());
            updated = true;
        }

        if (!Objects.equals(newEntity.getCodespace(), dbEntity.getCodespace())) {
            dbEntity.setCodespace(newEntity.getCodespace());
            updated = true;
        }

        return updated;
    }

}
