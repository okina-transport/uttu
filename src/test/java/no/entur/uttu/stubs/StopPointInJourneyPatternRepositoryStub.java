package no.entur.uttu.stubs;

import lombok.Setter;
import no.entur.uttu.model.FlexibleStopPlace;
import no.entur.uttu.model.StopPointInJourneyPattern;
import no.entur.uttu.repository.StopPointInJourneyPatternRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Setter
@Profile("stub-spijp-repository")
@Component
@Primary
public class StopPointInJourneyPatternRepositoryStub implements StopPointInJourneyPatternRepository {
    int nextCountByFlexibleStopPlace = 0;

    @Override
    public int countByFlexibleStopPlace(FlexibleStopPlace flexibleStopPlace) {
        return nextCountByFlexibleStopPlace;
    }

    @NotNull
    @Override
    public StopPointInJourneyPattern save(@NotNull StopPointInJourneyPattern entity) {
        return entity;
    }

    @NotNull
    @Override
    public <S extends StopPointInJourneyPattern> Iterable<S> saveAll(@NotNull Iterable<S> entities) {
        return List.of();
    }

    @NotNull
    @Override
    public Optional<StopPointInJourneyPattern> findById(@NotNull Long aLong) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(@NotNull Long aLong) {
        return false;
    }

    @NotNull
    @Override
    public Iterable<StopPointInJourneyPattern> findAll() {
        return List.of();
    }

    @NotNull
    @Override
    public Iterable<StopPointInJourneyPattern> findAllById(@NotNull Iterable<Long> longs) {
        return List.of();
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(@NotNull Long aLong) {
        // unused
    }

    @Override
    public void delete(@NotNull StopPointInJourneyPattern entity) {
        // unused
    }

    @Override
    public void deleteAllById(@NotNull Iterable<? extends Long> longs) {
        // unused
    }

    @Override
    public void deleteAll(@NotNull Iterable<? extends StopPointInJourneyPattern> entities) {
        // unused
    }

    @Override
    public StopPointInJourneyPattern findByNetexId(String netexId) {
        return null;
    }

    @Override
    public List<StopPointInJourneyPattern> findByNetexIdIn(Collection<String> ids) {
        return List.of();
    }

    @Override
    public List<StopPointInJourneyPattern> findAllByProviderCode(String code) {
        return List.of();
    }

    @Override
    public void deleteByNetexId(String netexId) {
        // unused
    }

    @Override
    public Long countByProviderCode(String code) {
        return 0L;
    }

    @Override
    public void deleteAll() {
        // unused
    }

    @Override
    public Optional<StopPointInJourneyPattern> findByDatasetIdAndOriginalId(String referentialId, String originalId) {
        return Optional.empty();
    }
}
