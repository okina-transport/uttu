package no.entur.uttu.repository;

import no.entur.uttu.model.Stop;
import no.entur.uttu.repository.generic.ProviderEntityRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StopRepository extends ProviderEntityRepository<Stop> {
}
