package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.DayType;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DayTypeMerger extends ProviderEntityBaseMerger<DayType> {

    private final DayTypeAssignmentMerger dayTypeAssignmentMerger;

    protected DayTypeMerger(EntityUpdater<DayType> updater, IdentifiedRepository<DayType> repository, ProviderMerger providerMerger, DayTypeAssignmentMerger dayTypeAssignmentMerger) {
        super(updater, repository, providerMerger);
        this.dayTypeAssignmentMerger = dayTypeAssignmentMerger;
    }

    @Override
    protected Map<String, DayType> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getDayTypesByOriginalId();
    }

    @Override
    protected void mergeForeignEntities(DayType entity, Referential dbReferential) {
        super.mergeForeignEntities(entity, dbReferential);
        if (CollectionUtils.isNotEmpty(entity.getDayTypeAssignments())) {
            entity.setDayTypeAssignments(entity.getDayTypeAssignments().stream().map(e -> dayTypeAssignmentMerger.merge(e, dbReferential, true)).collect(Collectors.toCollection(ArrayList::new)));
        }
    }
}
