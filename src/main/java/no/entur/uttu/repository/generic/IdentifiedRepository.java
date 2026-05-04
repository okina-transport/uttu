package no.entur.uttu.repository.generic;

import no.entur.uttu.model.IdentifiedEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface IdentifiedRepository<T extends IdentifiedEntity> extends CrudRepository<T, Long> {

    Optional<T> findByDatasetIdAndOriginalId(String referentialId, String originalId);

}
