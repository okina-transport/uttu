package no.entur.uttu.repository.generic;

import no.entur.uttu.model.ProviderEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Collection;
import java.util.List;

@NoRepositoryBean
public interface ProviderEntityRepository<T extends ProviderEntity> extends IdentifiedRepository<T> {

    @Query("select e from #{#entityName} as e where e.netexId = :netexId")
    T findByNetexId(String netexId);

    @Query("select e from #{#entityName} as e where e.netexId in :netexIds")
    List<T> findByNetexIdIn(Collection<String> netexIds);

    @Query("select e from #{#entityName} as e where e.provider.code in :code")
    List<T> findAllByProviderCode(String code);

    @Query("delete from #{#entityName} as e where e.netexId = :netexId")
    void deleteByNetexId(String netexId);

    @Query("select count(*) from #{#entityName} as e where e.provider.code in :code")
    Long countByProviderCode(String code);

}
