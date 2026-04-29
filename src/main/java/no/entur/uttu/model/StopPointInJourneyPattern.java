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

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(
        name = "identified_entity_gen",
        sequenceName = "stop_point_in_journey_pattern_seq",
        allocationSize = 10
)
@EqualsAndHashCode(callSuper = true, of = {"order", "forAlighting", "forBoarding"})
@ToString(callSuper = true, of = {"order", "forAlighting", "forBoarding"})
@Data
public class StopPointInJourneyPattern extends ProviderEntity {

    @ManyToOne
    private FlexibleStopPlace flexibleStopPlace;

    // Reference to quay in external stop place registry (NSR), either this or flexibleStopPlace must be set
    @ManyToOne
    private Stop stop;

    @ManyToOne(optional = false)
    private JourneyPattern journeyPattern;

    @OneToOne(cascade = CascadeType.ALL)
    private BookingArrangement bookingArrangement;

    // Order is reserved word in db
    @Column(name = "order_val")
    @Min(value = 1L, message = "The value must be positive")
    private int order;

    @OneToOne(cascade = CascadeType.ALL)
    private DestinationDisplay destinationDisplay;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Notice> notices = new ArrayList<>();

    private Boolean forAlighting;
    private Boolean forBoarding;

    @Override
    public void checkPersistable() {
        super.checkPersistable();

        Preconditions.checkArgument(!Boolean.FALSE.equals(forBoarding) || !Boolean.FALSE.equals(forAlighting),
                "%s allows neither boarding or alighting", identity());

        Preconditions.checkArgument(flexibleStopPlace != null ^ stop != null,
                "%s exactly one of flexibleStopPlace and quayRef should be set", identity());

    }
}
