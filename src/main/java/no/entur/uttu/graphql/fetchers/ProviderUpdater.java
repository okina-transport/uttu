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

package no.entur.uttu.graphql.fetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import no.entur.uttu.graphql.ArgumentWrapper;
import no.entur.uttu.model.Codespace;
import no.entur.uttu.model.Provider;
import no.entur.uttu.repository.CodespaceRepository;
import no.entur.uttu.repository.ProviderRepository;
import no.entur.uttu.util.Preconditions;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static no.entur.uttu.graphql.GraphQLNames.*;
import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_ROUTE_DATA_ADMIN;

@Component
@Transactional
public class ProviderUpdater implements DataFetcher<Provider> {

    private final ProviderRepository repository;
    private final CodespaceRepository codespaceRepository;

    public ProviderUpdater(ProviderRepository repository, CodespaceRepository codespaceRepository) {
        this.repository = repository;
        this.codespaceRepository = codespaceRepository;
    }

    @Override
    @PreAuthorize("hasRole('" + ROLE_ROUTE_DATA_ADMIN + "')")
    public Provider get(DataFetchingEnvironment env) {
        ArgumentWrapper input = new ArgumentWrapper(env.getArgument(FIELD_INPUT));
        String code = input.get(FIELD_CODE);
        Provider entity;

        if (code == null) {
            entity = new Provider();
        } else {
            entity = repository.findByCode(code).orElse(new Provider());
        }

        populateEntityFromInput(entity, input);

        return repository.save(entity);
    }

    private void populateEntityFromInput(Provider entity, ArgumentWrapper input) {
        input.apply(FIELD_CODE, entity::setCode);
        input.apply(FIELD_NAME, entity::setName);
        input.apply(FIELD_CODE_SPACE_XMLNS, this::getVerifiedCodespace, entity::setCodespace);
    }

    private Codespace getVerifiedCodespace(String xmlns) {
        Optional<Codespace> codespace = codespaceRepository.findByXmlns(xmlns);
        Preconditions.checkArgument(codespace.isPresent(),
                "Codespace not found [xmlns=%s]", xmlns);
        return codespace.get();
    }
}
