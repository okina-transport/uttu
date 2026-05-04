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

package no.entur.uttu.model;

import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import no.entur.uttu.util.Preconditions;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@SequenceGenerator(
        name = "identified_entity_gen",
        sequenceName = "operating_period_seq",
        allocationSize = 10
)
@Data
@EqualsAndHashCode(callSuper = true)
public class OperatingPeriod extends IdentifiedEntity {
    @NotNull
    private LocalDate fromDate;
    @NotNull
    private LocalDate toDate;

    @Override
    public void checkPersistable() {
        super.checkPersistable();

        Preconditions.checkArgument(fromDate != null && toDate != null && !toDate.isBefore(fromDate), "fromDate (%s) cannot be later than toDate(%s)", fromDate, toDate);
    }

    @Override
    public boolean isValid(LocalDate from, LocalDate to) {
        return !(fromDate.isAfter(to) || toDate.isBefore(from));
    }

}
