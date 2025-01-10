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

import no.entur.uttu.config.Context;
import no.entur.uttu.organisation.Organisation;
import no.entur.uttu.security.TokenService;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.rutebanken.netex.model.GeneralOrganisation;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.OrganisationTypeEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


@Component
public class CompanyRegistryImpl implements CompanyRegistry {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String companyRegistryUrl;

    private TokenService tokenService;

    private Map<String, List<GeneralOrganisation>> operatorCache = new HashMap<>();

    private Map<String, List<GeneralOrganisation>> authorityCache = new HashMap<>();



    @Autowired
    public CompanyRegistryImpl(@Value("${companies.registry.url}") String organisationRegistryUrl, TokenService tokenService) {
        this.companyRegistryUrl = organisationRegistryUrl;
        this.tokenService = tokenService;
    }

    private RestTemplate restTemplate = createRestTemplate();

    private RestTemplate createRestTemplate() {
        CloseableHttpClient clientBuilder = HttpClientBuilder.create().build();
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(clientBuilder));
    }

    public void updateCaches(String providerCode){
        operatorCache.remove(providerCode);
        authorityCache.remove(providerCode);

        List<GeneralOrganisation> companiesFromChouette = getCompaniesFromChouette(providerCode);

        List<GeneralOrganisation> operators = companiesFromChouette.stream()
                                .filter(comp -> OrganisationTypeEnumeration.OPERATOR.equals(comp.getOrganisationType().get(0)))
                                .collect(Collectors.toList());

        List<GeneralOrganisation> authorities = companiesFromChouette.stream()
                .filter(comp -> OrganisationTypeEnumeration.AUTHORITY.equals(comp.getOrganisationType().get(0)))
                .collect(Collectors.toList());

        operatorCache.put(providerCode, operators);
        authorityCache.put(providerCode, authorities);

    }

    private List<GeneralOrganisation> getCompaniesFromChouette(String providerCode){
        ResponseEntity<List> rateResponse = restTemplate.exchange(
                companyRegistryUrl,
                HttpMethod.GET,
                getEntityWithAuthenticationToken(providerCode),
                List.class);

        return buildOrganisationListFromResponse(rateResponse);
    }

    @Override
    public List<GeneralOrganisation> getCompanies() {
        String providerCode = Context.getVerifiedProviderCode();
        updateCaches(providerCode);
        List<GeneralOrganisation> companies  = new ArrayList<>();
        companies.addAll(operatorCache.get(providerCode));
        companies.addAll(authorityCache.get(providerCode));
        return companies;
    }

    public Optional<GeneralOrganisation> getOperator(String operatorRef) {

        if (org.apache.commons.lang3.StringUtils.isEmpty(operatorRef)){
            return Optional.empty();
        }
        String providerCode = Context.getVerifiedProviderCode();
        if (!operatorCache.containsKey(providerCode)){
             updateCaches(providerCode);
        }

        if (!operatorCache.containsKey(providerCode)){
            return Optional.empty();
        }
        List<GeneralOrganisation> operators = operatorCache.get(providerCode);

        return operators.stream()
                .filter(comp -> operatorRef.equals(comp.getId()))
                .findFirst();

    }

    @Override
    public Optional<GeneralOrganisation> getAuthority(String authorityRef) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(authorityRef)){
            return Optional.empty();
        }
        String providerCode = Context.getVerifiedProviderCode();
        if (!authorityCache.containsKey(providerCode)){
            updateCaches(providerCode);
        }

        if (!authorityCache.containsKey(providerCode)){
            return Optional.empty();
        }
        List<GeneralOrganisation> authorities = authorityCache.get(providerCode);

        return authorities.stream()
                .filter(comp -> authorityRef.equals(comp.getId()))
                .findFirst();
    }

    private List<GeneralOrganisation> buildOrganisationListFromResponse(ResponseEntity<List> response){
        List<GeneralOrganisation> organisationList = new ArrayList<>();

        if (!response.hasBody()){
            return organisationList;
        }

        List<LinkedHashMap<String,String>> responseList = response.getBody();

        for (LinkedHashMap linkedHashMap : responseList) {

            GeneralOrganisation newOrg = new GeneralOrganisation();


            if (linkedHashMap.containsKey("netexId")){
                newOrg.setId((String) linkedHashMap.get("netexId"));
            }

            if (linkedHashMap.containsKey("nom")){
                MultilingualString name = new MultilingualString();
                name.setValue((String) linkedHashMap.get("nom"));
                newOrg.setName(name);
            }


            if (linkedHashMap.containsKey("type")){
                String loweredOrgType = ((String) linkedHashMap.get("type")).toLowerCase();
                newOrg.withOrganisationType(OrganisationTypeEnumeration.fromValue(loweredOrgType));
            }
            organisationList.add(newOrg);
        }
        return organisationList;
    }


    private GeneralOrganisation convertToGeneralOrganisation(Organisation org){
        GeneralOrganisation generalOrg = new GeneralOrganisation();
        generalOrg.setId(org.id);
        MultilingualString name = new MultilingualString();
        name.setValue(org.name);
        generalOrg.setName(name);

        for (String type : org.types) {
            generalOrg.getOrganisationType().add(OrganisationTypeEnumeration.fromValue(type.toLowerCase()));
        }
        return generalOrg;

    }

    /**
     * Return provided operatorRef if valid, else throw exception.
     */
    public String getVerifiedOperatorRef(String operatorRef) {
        if (StringUtils.isEmpty(operatorRef)) {
            return null;
        }
        Optional<GeneralOrganisation> organisation = getOperator(operatorRef);
        if (organisation.isEmpty()){
            return getDefaultOperator();
        }
        return operatorRef;
    }

    private String getDefaultOperator() {
        String providerCode = Context.getVerifiedProviderCode();

        if (operatorCache.containsKey(providerCode)){
            return operatorCache.get(providerCode).get(0).getId();
        }
        throw new IllegalArgumentException("No operator available for provider:" + providerCode);
    }

    /**
     * Return provided authorityRef if valid, else throw exception.
     */
    public String getVerifiedAuthorityRef(String authorityRef) {
        if (StringUtils.isEmpty(authorityRef)) {
            return null;
        }
        Optional<GeneralOrganisation> authority = getAuthority(authorityRef);
        if (authority.isEmpty()){
            return getDefaultAuthority();
        }
        return authorityRef;
    }

    private String getDefaultAuthority() {
        String providerCode = Context.getVerifiedProviderCode();

        if (authorityCache.containsKey(providerCode)){
            return authorityCache.get(providerCode).get(0).getId();
        }
        throw new IllegalArgumentException("No authority available for provider:" + providerCode);
    }

    private HttpEntity<String> getEntityWithAuthenticationToken(String referential) {
        HttpHeaders headers = new HttpHeaders();
        String token = tokenService.getToken();
        headers.set("Authorization", "Bearer " +token );
        headers.set("X-Okina-Referential", referential);
        return new HttpEntity<>(headers);
    }


}
