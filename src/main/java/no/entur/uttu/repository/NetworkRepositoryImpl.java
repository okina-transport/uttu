package no.entur.uttu.repository;


import no.entur.uttu.config.Context;
import no.entur.uttu.config.NetexHttpMessageConverter;
import no.entur.uttu.model.Codespace;
import no.entur.uttu.model.Network;
import no.entur.uttu.model.Provider;
import no.entur.uttu.security.TokenService;


import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.rutebanken.netex.model.GeneralOrganisation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

@Repository
public class NetworkRepositoryImpl extends SimpleJpaRepository<Network, Long> implements NetworkRepository {

    private static final Logger log = LoggerFactory.getLogger(NetworkRepositoryImpl.class);

    private final EntityManager entityManager;
    private RestTemplate restTemplate = createRestTemplate();

    @Value("${network.registry.url}")
    private String networkRegistryUrl;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private CompanyRegistry companyRegistry;


    public NetworkRepositoryImpl(EntityManager entityManager) {
        super(Network.class, entityManager);
        this.entityManager = entityManager;
    }


    private RestTemplate createRestTemplate() {
        CloseableHttpClient clientBuilder = HttpClientBuilder.create().build();
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(clientBuilder));
    }


    @Override
    public List<Network> syncAndFindAll() {
        String providerCode = Context.getVerifiedProviderCode();
        ResponseEntity<List> rateResponse = restTemplate.exchange(
                networkRegistryUrl,
                HttpMethod.GET,
                getEntityWithAuthenticationToken(providerCode),
                List.class);

        List<Network> networks = buildNetworkListFromResponse(rateResponse);
        updateNetworksInDB(networks);

        return entityManager.createQuery("from Network e where e.provider.code=:providerCode", Network.class).setParameter("providerCode", providerCode).getResultList();

    }


    public void updateNetworksInDB(List<Network> networksFromChouette) {
        String providerCode = Context.getVerifiedProviderCode();


        Provider provider = entityManager.createQuery("from Provider p where p.name = :providerName", Provider.class)
                .setParameter("providerName", providerCode)
                .getSingleResult();


        for (Network newNetwork : networksFromChouette) {
            Network existingNetwork = null;
            try {
                existingNetwork = entityManager.createQuery("from Network e where e.provider.code=:providerCode and e.name = :name", Network.class)
                        .setParameter("providerCode", providerCode)
                        .setParameter("name", newNetwork.getName())
                        .getSingleResult();
            } catch (NoResultException e) {
                log.info("No network found for provider code: " + providerCode + " and name: " + newNetwork.getName() + ".Creating new one");
            }


            if (existingNetwork == null) {
                newNetwork.setProvider(provider);
                entityManager.persist(newNetwork);
            } else {
                existingNetwork.setNetexId(newNetwork.getNetexId());
                existingNetwork.setAuthorityRef(newNetwork.getAuthorityRef());
                entityManager.merge(existingNetwork);
            }
        }
        entityManager.flush();

    }


    private List<Network> buildNetworkListFromResponse(ResponseEntity<List> response) {
        List<Network> networkList = new ArrayList<>();

        if (!response.hasBody()) {
            return networkList;
        }

        List<LinkedHashMap<String, String>> responseList = response.getBody();

        for (LinkedHashMap<String, String> stringStringLinkedHashMap : responseList) {
            Network network = new Network();
            network.setAuthorityRef(stringStringLinkedHashMap.get("authorityRef"));
            network.setName(stringStringLinkedHashMap.get("name"));
            network.setNetexId(stringStringLinkedHashMap.get("objectId"));
            networkList.add(network);
        }
        return networkList;
    }

    @Override
    public void deleteAll() {
        entityManager.createQuery("delete from Network").executeUpdate();
    }

    @Override
    public Network getById(String id) {
        return entityManager.createQuery("from Network where id=:id", Network.class).setParameter("id", id).getResultList().stream().findFirst().orElse(null);
    }

    @Override
    public Network findByName(String name) {
        return entityManager.createQuery("from Network where name=:name", Network.class).setParameter("name", name).getResultList().stream().findFirst().orElse(null);
    }

    private HttpEntity<String> getEntityWithAuthenticationToken(String referential) {
        HttpHeaders headers = new HttpHeaders();
        String token = tokenService.getToken();
        headers.set("Authorization", "Bearer " + token);
        headers.set("X-Okina-Referential", referential);
        return new HttpEntity<>(headers);
    }
}
