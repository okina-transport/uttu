package no.entur.uttu.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.model.DayType;
import no.entur.uttu.repository.generic.ProviderEntityRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static no.entur.uttu.graphql.GraphQLNames.FIELD_IDS;

@Slf4j
@Service("dayTypesBulkUpdater")
@Transactional
public class DayTypesBulkUpdater implements DataFetcher<List<DayType>> {

    private final DayTypeUpdater dayTypeUpdater;
    private final ProviderEntityRepository<DayType> repository;

    @Autowired
    public DayTypesBulkUpdater(DayTypeUpdater dayTypeUpdater,
                               ProviderEntityRepository<DayType> repository) {

        this.dayTypeUpdater = dayTypeUpdater;
        this.repository = repository;
    }

    @Override
    public List<DayType> get(DataFetchingEnvironment environment) throws Exception {
        if (environment.getField().getName().equals("deleteDayTypes")) {
            return deleteEntities(environment);
        } else {
            return repository.findByNetexIdIn(environment.getArgument(FIELD_IDS));
        }
    }

    protected List<DayType> deleteEntities(DataFetchingEnvironment env) {
        List<String> ids = env.getArgument(FIELD_IDS);
        if (CollectionUtils.isEmpty(ids)) {
            log.error("No DayType IDs to delete");
            return List.of();
        }
        ids.forEach(dayTypeUpdater::verifyDeleteAllowed);
        List<DayType> entities = repository.findByNetexIdIn(ids);
        repository.deleteAll(entities);
        return entities;
    }
}
