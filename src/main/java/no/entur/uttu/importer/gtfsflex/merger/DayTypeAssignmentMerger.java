package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.DayTypeAssignment;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DayTypeAssignmentMerger extends IdentifiedEntityBaseMerger<DayTypeAssignment> {

    private final OperatingPeriodMerger operatingPeriodMerger;

    protected DayTypeAssignmentMerger(EntityUpdater<DayTypeAssignment> updater, IdentifiedRepository<DayTypeAssignment> repository, OperatingPeriodMerger operatingPeriodMerger) {
        super(updater, repository);
        this.operatingPeriodMerger = operatingPeriodMerger;
    }

    @Override
    protected Map<String, DayTypeAssignment> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getDayTypeAssignmentsByOriginalId();
    }

    @Override
    protected void mergeForeignEntities(DayTypeAssignment entity, Referential dbReferential) {
        super.mergeForeignEntities(entity, dbReferential);
        if (entity.getOperatingPeriod() != null) {
            entity.setOperatingPeriod(operatingPeriodMerger.merge(entity.getOperatingPeriod(), dbReferential, true));
        }
    }
}
