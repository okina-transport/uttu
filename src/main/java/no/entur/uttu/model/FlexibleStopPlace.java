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
import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = Constraints.FLEXIBLE_STOP_PLACE_UNIQUE_NAME, columnNames = {"provider_pk", "name"})})
@SequenceGenerator(
        name = "identified_entity_gen",
        sequenceName = "flexible_stop_place_seq",
        allocationSize = 10
)
@EqualsAndHashCode(callSuper = true, of = "transportMode")
@ToString(callSuper = true, of = "transportMode")
@Data
public class FlexibleStopPlace extends GroupOfEntitiesVersionStructure {

    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    protected Map<String, Value> keyValues = new HashMap<>();

    @Enumerated(EnumType.STRING)
    @NotNull
    private VehicleModeEnumeration transportMode;

    @OneToMany(mappedBy = "flexibleStopPlace", orphanRemoval = true)
    private List<FlexibleArea> flexibleAreas = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    private HailAndRideArea hailAndRideArea;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Stop> stops = new ArrayList<>();

    @OneToMany(mappedBy = "flexibleStopPlace", orphanRemoval = true)
    private List<StopPointInJourneyPattern> stopPointInJourneyPatterns = new ArrayList<>();

    public void replaceKeyValues(Map<String, Value> keyValues) {
        this.keyValues.clear();
        this.keyValues.putAll(keyValues);
    }

    public void setFlexibleAreas(List<FlexibleArea> flexibleAreas) {
        this.flexibleAreas.clear();
        if (flexibleAreas != null) {
            flexibleAreas.forEach(e -> e.setFlexibleStopPlace(this));
            this.flexibleAreas.addAll(flexibleAreas);
        }
    }

    public void addStopPointInJourneyPattern(StopPointInJourneyPattern stopPointInJourneyPattern) {
        stopPointInJourneyPatterns.add(stopPointInJourneyPattern);
        stopPointInJourneyPattern.setFlexibleStopPlace(this);
    }

    public void setStopPointInJourneyPatterns(List<StopPointInJourneyPattern> stopPointInJourneyPatterns) {
        this.stopPointInJourneyPatterns.clear();
        if (stopPointInJourneyPatterns != null) {
            stopPointInJourneyPatterns.forEach(e -> e.setFlexibleStopPlace(this));
            this.stopPointInJourneyPatterns.addAll(stopPointInJourneyPatterns);
        }
    }

    @Override
    public void checkPersistable() {
        super.checkPersistable();
        Preconditions.checkArgument(
                CollectionUtils.isNotEmpty(flexibleAreas) ^
                        hailAndRideArea != null ^
                        CollectionUtils.isNotEmpty(stops)
                , "%s exactly one of flexibleArea or hailAndRideArea or stops must be set", identity());
        for (FlexibleArea flexibleArea : flexibleAreas) {
            flexibleArea.checkPersistable();
        }

        if (hailAndRideArea != null) {
            hailAndRideArea.checkPersistable();
        }

        for (Stop stop : stops) {
            stop.checkPersistable();
        }
    }
}