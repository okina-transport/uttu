package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.FlexibleLine;
import no.entur.uttu.model.JourneyPattern;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class JourneyPatternMerger extends ProviderEntityBaseMerger<JourneyPattern> {

    private final FlexibleLineMerger flexibleLineMerger;

    protected JourneyPatternMerger(EntityUpdater<JourneyPattern> updater, IdentifiedRepository<JourneyPattern> repository, ProviderMerger providerMerger, FlexibleLineMerger flexibleLineMerger) {
        super(updater, repository, providerMerger);
        this.flexibleLineMerger = flexibleLineMerger;
    }


    @Override
    protected Map<String, JourneyPattern> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getJourneyPatternsByOriginalId();
    }

    @Override
    protected void mergeForeignEntities(JourneyPattern entity, Referential dbReferential) {
        super.mergeForeignEntities(entity, dbReferential);
        if (entity.getLine() != null && entity.getLine() instanceof FlexibleLine flexibleLine) {
            entity.setLine(flexibleLineMerger.merge(flexibleLine, dbReferential, true));
        }
    }
}
