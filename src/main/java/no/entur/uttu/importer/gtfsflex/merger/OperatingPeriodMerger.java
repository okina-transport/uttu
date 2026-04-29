package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.OperatingPeriod;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OperatingPeriodMerger extends IdentifiedEntityBaseMerger<OperatingPeriod> {
    protected OperatingPeriodMerger(EntityUpdater<OperatingPeriod> updater, IdentifiedRepository<OperatingPeriod> repository) {
        super(updater, repository);
    }

    @Override
    protected Map<String, OperatingPeriod> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getOperatingPeriodByOriginalId();
    }
}
