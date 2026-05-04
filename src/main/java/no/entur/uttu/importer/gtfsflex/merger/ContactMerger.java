package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.Contact;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ContactMerger extends IdentifiedEntityBaseMerger<Contact> {
    protected ContactMerger(EntityUpdater<Contact> updater, IdentifiedRepository<Contact> repository) {
        super(updater, repository);
    }

    @Override
    protected Map<String, Contact> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getContactsByOriginalId();
    }
}
