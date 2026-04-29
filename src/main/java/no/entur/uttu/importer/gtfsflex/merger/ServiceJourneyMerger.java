package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.ServiceJourney;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ServiceJourneyMerger extends ProviderEntityBaseMerger<ServiceJourney> {

    private final DayTypeMerger dayTypeMerger;
    private final JourneyPatternMerger journeyPatternMerger;

    protected ServiceJourneyMerger(EntityUpdater<ServiceJourney> updater, IdentifiedRepository<ServiceJourney> repository, ProviderMerger providerMerger, DayTypeMerger dayTypeMerger, JourneyPatternMerger journeyPatternMerger) {
        super(updater, repository, providerMerger);
        this.dayTypeMerger = dayTypeMerger;
        this.journeyPatternMerger = journeyPatternMerger;
    }

    @Override
    protected Map<String, ServiceJourney> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getServiceJourneysByOriginalId();
    }

    @Override
    protected void mergeForeignEntities(ServiceJourney entity, Referential dbReferential) {
        super.mergeForeignEntities(entity, dbReferential);
        if (entity.getJourneyPattern() != null) {
            entity.setJourneyPattern(journeyPatternMerger.merge(entity.getJourneyPattern(), dbReferential, true));
        }
        if (CollectionUtils.isNotEmpty(entity.getDayTypes())) {
            entity.updateDayTypes(entity.getDayTypes().stream().map(e -> dayTypeMerger.merge(e, dbReferential, true)).collect(Collectors.toCollection(ArrayList::new)));
        }
    }
}
