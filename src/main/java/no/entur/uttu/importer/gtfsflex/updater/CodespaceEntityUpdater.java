package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.Codespace;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Component;

@Component
public class CodespaceEntityUpdater implements EntityUpdater<Codespace> {

    @Override
    public boolean update(Codespace newEntity, Codespace dbEntity) {
        boolean updated = false;
        
        if (!Strings.CS.equals(newEntity.getXmlns(), dbEntity.getXmlns())) {
            dbEntity.setXmlns(newEntity.getXmlns());
            updated = true;
        }

        if (!Strings.CS.equals(newEntity.getXmlnsUrl(), dbEntity.getXmlnsUrl())) {
            dbEntity.setXmlnsUrl(newEntity.getXmlnsUrl());
            updated = true;
        }

        return updated;
    }

}
