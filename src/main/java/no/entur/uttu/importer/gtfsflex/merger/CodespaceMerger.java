package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.Codespace;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CodespaceMerger extends IdentifiedEntityBaseMerger<Codespace> {
    protected CodespaceMerger(EntityUpdater<Codespace> updater, IdentifiedRepository<Codespace> repository) {
        super(updater, repository);
    }

    @Override
    protected Map<String, Codespace> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getCodespacesByOriginalId();
    }
}
