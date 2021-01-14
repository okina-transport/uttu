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

import graphql.schema.DataFetchingEnvironment;
import no.entur.uttu.graphql.mappers.AbstractProviderEntityMapper;
import no.entur.uttu.model.FlexibleLine;
import no.entur.uttu.repository.ExportLineAssociationRepository;
import no.entur.uttu.repository.generic.ProviderEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static no.entur.uttu.graphql.GraphQLNames.FIELD_ID;

public abstract class FlexibleLineProviderEntityUpdater extends AbstractProviderEntityUpdater<FlexibleLine> {

    @Autowired
    private ExportLineAssociationRepository exportLineAssociationRepository;

    protected FlexibleLineProviderEntityUpdater(AbstractProviderEntityMapper<FlexibleLine> mapper, ProviderEntityRepository<FlexibleLine> repository) {
        super(mapper, repository);
    }

    @Override
    protected FlexibleLine deleteEntity(DataFetchingEnvironment env) {
        String id = env.getArgument(FIELD_ID);
        verifyDeleteAllowed(id);
        exportLineAssociationRepository.deleteByLineNetexId(id);
        return repository.delete(id);
    }


}
