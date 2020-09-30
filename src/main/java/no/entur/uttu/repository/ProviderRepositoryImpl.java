/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.entur.uttu.repository;

import no.entur.uttu.model.Codespace;
import no.entur.uttu.model.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Repository
public class ProviderRepositoryImpl extends SimpleJpaRepository<Provider, Long> implements ProviderRepository {

    private final EntityManager entityManager;

    @Autowired
    CodespaceRepository codespaceRepository;

    @Autowired
    RestProviderDAO restProviderService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(fixedRateString = "${tiamat.provider.cache.refresh.interval:60000}")
    @Transactional
    public void populate() {
        restProviderService.getProviders().stream()
                .filter(provider -> !provider.getChouetteInfo().getReferential().startsWith("naq"))
                .forEach(provider -> {
                    String xmlns = provider.getChouetteInfo().getXmlns();
                    provider.setCode(xmlns.toLowerCase());
                    if (getOne(provider.getCode()) == null) {
                        Codespace codeSpace = codespaceRepository.getOneByXmlns(xmlns);
                        if (codeSpace == null) {
                            provider.setCodespace(new Codespace(xmlns, provider.getChouetteInfo().getXmlnsurl()));
                            codespaceRepository.save(provider.getCodespace());
                        } else {
                            provider.setCodespace(codeSpace);
                        }
                        save(provider);

                    }
                });
    }

    public ProviderRepositoryImpl(EntityManager entityManager) {
        super(Provider.class, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Provider getOne(String code) {
        return entityManager.createQuery("from Provider where code=:code", Provider.class).setParameter("code", code).getResultList().stream().findFirst().orElse(null);
    }

}
