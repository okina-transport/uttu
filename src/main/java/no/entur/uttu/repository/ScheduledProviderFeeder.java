package no.entur.uttu.repository;


import no.entur.uttu.model.Codespace;
import no.entur.uttu.model.Provider;
import no.entur.uttu.security.TokenService;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Scheduled task to read provider repository (BABA) and feed uttu provider base with recovered data
 */
@Component
@Configuration
public class ScheduledProviderFeeder {

    private final Logger logger = LoggerFactory.getLogger(ScheduledProviderFeeder.class);

    @Autowired
    ProviderRepository providerRepository;

    @Autowired
    CodespaceRepository codespaceRepository;

    @Autowired
    private TokenService tokenService;

    @Value("${uttu.provider.update.frequency.min:60}")
    private int updateFrequency = 60;

    private RestTemplate restTemplate = createRestTemplate();

    @Value("${providers.registry.url:https://tiamat-rmr.nouvelle-aquitaine.pro/api/organisations/1.0/}")
    private String providersRegistryUrl;

    private  List<Provider> existingProviders;



    @PostConstruct
    private void initialize() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(this::updateProviders, 0, updateFrequency, TimeUnit.MINUTES);
        logger.info("Initialized provider update with updateFrequency:{} min", updateFrequency);
    }


    private void updateProviders() {

        existingProviders = providerRepository.findAll();

        try{
            ResponseEntity<List> rateResponse = restTemplate.exchange(
                    providersRegistryUrl,
                    HttpMethod.GET,
                    getEntityWithAuthenticationToken(),
                    List.class);

             initProviders(rateResponse);

        }catch (Exception e){
            logger.error("Error on provider sync", e);
        }


    }

    /**
     * Check if the provider is already existing in uttu DB or not
     * @param providerName
     *  the provider to search
     * @return
     *      true : the provider is existing
     *      false : the provider is not existing
     */
    private boolean isProviderExisting(String providerName){
            return existingProviders.stream()
                                    .anyMatch(currPov -> currPov.getName().equals(providerName));
    }


    /**
     * Initialize providers in uttu DB
     * @param response
     */
    private void initProviders(ResponseEntity<List> response){
        

        if (!response.hasBody()){
            return ;
        }

        List<LinkedHashMap<String,String>> responseList = response.getBody();

        for (LinkedHashMap<String,String> linkedHashMap : responseList) {

            if (!isProviderUsable(linkedHashMap)){
                continue;
            }
            String provName = linkedHashMap.get("name");

            Codespace newCodeSpace = new Codespace();
            newCodeSpace.setChanged(Instant.now());
            newCodeSpace.setCreated(Instant.now());
            newCodeSpace.setCreatedBy("ScheduledProviderFeeder");
            newCodeSpace.setVersion(1l);
            newCodeSpace.setChangedBy("ScheduledProviderFeeder");
            newCodeSpace.setXmlns(provName);
            newCodeSpace.setXmlnsUrl("http://" + provName);
            newCodeSpace = codespaceRepository.save(newCodeSpace);


            Provider newProv = new Provider();
            newProv.setName(provName);
            newProv.setCode(provName);
            newProv.setCreated(Instant.now());
            newProv.setChanged(Instant.now());
            newProv.setChangedBy("ScheduledProviderFeeder");
            newProv.setCreatedBy("ScheduledProviderFeeder");
            newProv.setVersion(1l);
            newProv.setCodespace(newCodeSpace);
            providerRepository.save(newProv);
        }
        logger.info("Providers updated successfully");
        
    }

    /**
     * Check if a provider is usable or not
     * usable =
     *          - with a name
     *          - not currently existing
     *          - not beginning with mobiiti_
     *          - not technique
     * @param providerParameters
     *      the providers's parameters
     * @return
     *      true : provider is usable
     *      false : provider can not be integrated in uttu
     */
    private boolean isProviderUsable(LinkedHashMap<String,String> providerParameters){
        if (!providerParameters.containsKey("name") || isProviderExisting(providerParameters.get("name"))){
            return false;
        }

        String providerName = providerParameters.get("name");
        if ("technique".equals(providerName) || providerName.startsWith("mobiiti_")){
            return false;
        }
        return true;
    }
    private RestTemplate createRestTemplate() {
        CloseableHttpClient clientBuilder = HttpClientBuilder.create().build();
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(clientBuilder));
    }

    private HttpEntity<String> getEntityWithAuthenticationToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenService.getToken());
        return new HttpEntity<>(headers);
    }


}
