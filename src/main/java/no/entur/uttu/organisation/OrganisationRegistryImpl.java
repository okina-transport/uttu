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

package no.entur.uttu.organisation;

import no.entur.uttu.error.codederror.CodedError;
import no.entur.uttu.error.codes.ErrorCodeEnumeration;
import no.entur.uttu.security.TokenService;
import no.entur.uttu.util.Preconditions;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.rutebanken.netex.model.GeneralOrganisation;
import org.rutebanken.netex.model.MultilingualString;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

import static no.entur.uttu.organisation.Organisation.NETEX_OPERATOR_ID_REFEFRENCE_KEY;
import static no.entur.uttu.organisation.Organisation.OPERATOR_TYPE;

@Component
public class OrganisationRegistryImpl implements OrganisationRegistry {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String organisationRegistryUrl;

    private TokenService tokenService;

    @Autowired
    public OrganisationRegistryImpl(@Value("${organisation.registry.url:https://tiamat-rmr.nouvelle-aquitaine.pro/api/organisations/1.0/}") String organisationRegistryUrl, TokenService tokenService) {
        this.organisationRegistryUrl = organisationRegistryUrl;
        this.tokenService = tokenService;
    }

    private RestTemplate restTemplate = createRestTemplate();

    private RestTemplate createRestTemplate() {
        CloseableHttpClient clientBuilder = HttpClientBuilder.create().build();
        return new RestTemplate(new HttpComponentsClientHttpRequestFactory(clientBuilder));
    }

    @Override
    public List<GeneralOrganisation> getOrganisations() {
        return null;
    }

    public Optional<GeneralOrganisation> getOrganisation(String organisationId) {
        try {
            ResponseEntity<Organisation> rateResponse =
                    restTemplate.exchange(
                            organisationRegistryUrl + organisationId,
                            HttpMethod.GET,
                            getEntityWithAuthenticationToken(),
                            Organisation.class);
            Organisation organisation = rateResponse.getBody();
            if (organisation.types == null) {
                Set<String> types = new HashSet<>();
                types.add(OPERATOR_TYPE);
                organisation.types = types;

                if (organisation.references == null) {
                    Map<String, String> references = new HashMap<>();
                    references.put(NETEX_OPERATOR_ID_REFEFRENCE_KEY, organisation.id);
                    organisation.references = references;
                }
            }
            return Optional.of(convertToGeneralOrganisation(organisation));
        } catch (HttpClientErrorException ex) {
            logger.warn("Exception while trying to fetch operator: " + organisationId + " : " + ex.getMessage(), ex);
            return Optional.empty();
        }
    }

    private GeneralOrganisation convertToGeneralOrganisation(Organisation org){
        GeneralOrganisation generalOrg = new GeneralOrganisation();
        generalOrg.setId(org.id);
        MultilingualString name = new MultilingualString();
        name.setValue(org.name);
        generalOrg.setName(name);

        return generalOrg;

    }

    /**
     * Return provided operatorRef if valid, else throw exception.
     */
    public String getVerifiedOperatorRef(String operatorRef) {
        if (StringUtils.isEmpty(operatorRef)) {
            return null;
        }
        Optional<GeneralOrganisation> organisation = getOrganisation(operatorRef);
        if (organisation.isEmpty()){
            throw new IllegalArgumentException("Operator not verified : " + operatorRef);
        }
        return operatorRef;
    }

    /**
     * Return provided authorityRef if valid, else throw exception.
     */
    public String getVerifiedAuthorityRef(String authorityRef) {
        if (StringUtils.isEmpty(authorityRef)) {
            return null;
        }
        Optional<GeneralOrganisation> organisation = getOrganisation(authorityRef);
        if (organisation.isEmpty()){
            throw new IllegalArgumentException("Authority not verified : " + authorityRef);
        }
        return authorityRef;
    }

    private HttpEntity<String> getEntityWithAuthenticationToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tokenService.getToken());
        return new HttpEntity<>(headers);
    }


}
