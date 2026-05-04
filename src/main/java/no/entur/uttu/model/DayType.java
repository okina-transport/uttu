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

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.entur.uttu.util.Preconditions;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(
        name = "identified_entity_gen",
        sequenceName = "day_type_seq",
        allocationSize = 10
)
@EqualsAndHashCode(callSuper = true, of = {"daysOfWeek", "name"})
@ToString(callSuper = true, of = {"daysOfWeek", "name"})
@Data
public class DayType extends ProviderEntity {

    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<DayOfWeek> daysOfWeek = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DayTypeAssignment> dayTypeAssignments = new ArrayList<>();

    private String name;

    @Override
    public void checkPersistable() {
        super.checkPersistable();
        getDayTypeAssignments().stream().forEach(IdentifiedEntity::checkPersistable);

        if (CollectionUtils.isEmpty(daysOfWeek)) {
            boolean includedPeriod = getDayTypeAssignments().stream().anyMatch(dta -> dta.getOperatingPeriod() != null);
            Preconditions.checkArgument(!includedPeriod, "%s has OperatingPeriod without setting daysOfWeek", identity());
        }
    }

    public void setDayTypeAssignments(List<DayTypeAssignment> dayTypeAssignments) {
        this.dayTypeAssignments.clear();
        if (dayTypeAssignments != null) {
            this.dayTypeAssignments.addAll(dayTypeAssignments);
        }
    }

    @Override
    public boolean isValid(LocalDate from, LocalDate to) {
        return super.isValid(from, to) && getDayTypeAssignments().stream().anyMatch(e -> e.isValid(from, to));
    }
}
