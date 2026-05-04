package no.entur.uttu.repository;

import no.entur.uttu.model.Network;
import no.entur.uttu.repository.generic.ProviderEntityRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NetworkRepository extends ProviderEntityRepository<Network> {

    Optional<Network> findByName(String name);

}
