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

import no.entur.uttu.model.job.ExportLineAssociation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExportLineAssociationRepository extends CrudRepository<ExportLineAssociation, Long> {

    // weird request due to https://hibernate.atlassian.net/browse/HHH-7314
    @Modifying
    @Query("DELETE FROM ExportLineAssociation ela WHERE ela.line IN " +
            "(SELECT line FROM Line line WHERE line.netexId=:lineNetexId)")
    int deleteByLineNetexId(@Param("lineNetexId") String lineId);
}
