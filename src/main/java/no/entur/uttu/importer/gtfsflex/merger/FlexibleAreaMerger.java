package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.FlexibleArea;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FlexibleAreaMerger extends IdentifiedEntityBaseMerger<FlexibleArea> {
    protected FlexibleAreaMerger(EntityUpdater<FlexibleArea> updater, IdentifiedRepository<FlexibleArea> repository) {
        super(updater, repository);
    }

    @Override
    protected Map<String, FlexibleArea> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getFlexibleAreasByOriginalId();
    }
}
