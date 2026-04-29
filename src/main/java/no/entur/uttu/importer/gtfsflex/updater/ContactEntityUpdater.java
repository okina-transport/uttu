package no.entur.uttu.importer.gtfsflex.updater;

import no.entur.uttu.model.Contact;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Component;

@Component
public class ContactEntityUpdater implements EntityUpdater<Contact> {

    @Override
    public boolean update(Contact newEntity, Contact dbEntity) {
        boolean updated = false;
        
        if (!Strings.CS.equals(newEntity.getEmail(), dbEntity.getEmail())) {
            dbEntity.setEmail(newEntity.getEmail());
            updated = true;
        }

        if (!Strings.CS.equals(newEntity.getPhone(), dbEntity.getPhone())) {
            dbEntity.setPhone(newEntity.getPhone());
            updated = true;
        }

        if (!Strings.CS.equals(newEntity.getUrl(), dbEntity.getUrl())) {
            dbEntity.setUrl(newEntity.getUrl());
            updated = true;
        }

        if (!Strings.CS.equals(newEntity.getContactPerson(), dbEntity.getContactPerson())) {
            dbEntity.setContactPerson(newEntity.getContactPerson());
            updated = true;
        }

        if (!Strings.CS.equals(newEntity.getFurtherDetails(), dbEntity.getFurtherDetails())) {
            dbEntity.setFurtherDetails(newEntity.getFurtherDetails());
            updated = true;
        }

        return updated;
    }

}
