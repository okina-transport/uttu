package no.entur.uttu.importer.gtfsflex.merger;

import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.importer.gtfsflex.updater.EntityUpdater;
import no.entur.uttu.model.FlexibleArea;
import no.entur.uttu.model.FlexibleStopPlace;
import no.entur.uttu.repository.generic.IdentifiedRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FlexibleStopPlaceMerger extends ProviderEntityBaseMerger<FlexibleStopPlace> {

    private final StopMerger stopMerger;
    private final FlexibleAreaMerger flexibleAreaMerger;

    protected FlexibleStopPlaceMerger(EntityUpdater<FlexibleStopPlace> updater, IdentifiedRepository<FlexibleStopPlace> repository, ProviderMerger providerMerger, StopMerger stopMerger, FlexibleAreaMerger flexibleAreaMerger) {
        super(updater, repository, providerMerger);
        this.stopMerger = stopMerger;
        this.flexibleAreaMerger = flexibleAreaMerger;
    }

    @Override
    protected Map<String, FlexibleStopPlace> getDbEntitiesByOriginalId(Referential dbReferential) {
        return dbReferential.getFlexibleStopPlacesByOriginalId();
    }

    @Override
    protected void mergeForeignEntities(FlexibleStopPlace entity, Referential dbReferential) {
        super.mergeForeignEntities(entity, dbReferential);
        if (CollectionUtils.isNotEmpty(entity.getStops())) {
            entity.setStops(entity.getStops().stream().map(e -> stopMerger.merge(e, dbReferential, true)).collect(Collectors.toCollection(ArrayList::new)));
        }
    }

    @Override
    public FlexibleStopPlace merge(FlexibleStopPlace entity, Referential dbReferential, boolean persist) {
        if (CollectionUtils.isNotEmpty(entity.getFlexibleAreas())) {
            FlexibleStopPlace merged = super.merge(entity, dbReferential, persist);
            for (FlexibleArea flexibleArea : entity.getFlexibleAreas()) {
                flexibleArea.setFlexibleStopPlace(merged);
                flexibleAreaMerger.merge(flexibleArea, dbReferential, persist);
            }
            return merged;
        } else {
            return super.merge(entity, dbReferential, persist);
        }
    }
}
