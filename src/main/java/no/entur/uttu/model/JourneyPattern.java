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
import no.entur.uttu.error.codederror.CodedError;
import no.entur.uttu.error.codes.ErrorCodeEnumeration;
import no.entur.uttu.util.Preconditions;
import org.apache.commons.lang3.BooleanUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static no.entur.uttu.model.Constraints.JOURNEY_PATTERN_UNIQUE_NAME;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = JOURNEY_PATTERN_UNIQUE_NAME, columnNames = {"provider_pk", "name"})})
@SequenceGenerator(
        name = "identified_entity_gen",
        sequenceName = "journey_pattern_seq",
        allocationSize = 10
)
@EqualsAndHashCode(callSuper = true, of = "directionType")
@ToString(callSuper = true, of = "directionType")
@Data
public class JourneyPattern extends GroupOfEntitiesVersionStructure {

    @OneToMany(mappedBy = "journeyPattern", orphanRemoval = true)
    @NotNull
    private final List<ServiceJourney> serviceJourneys = new ArrayList<>();
    @OneToMany(mappedBy = "journeyPattern", orphanRemoval = true)
    @NotNull
    @OrderBy("order")
    private final List<StopPointInJourneyPattern> pointsInSequence = new ArrayList<>();
    @ManyToOne(optional = false)
    private Line line;
    @Enumerated(EnumType.STRING)
    private DirectionTypeEnumeration directionType;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Notice> notices = new ArrayList<>();

    public void addServiceJourney(ServiceJourney serviceJourney) {
        this.serviceJourneys.add(serviceJourney);
        serviceJourney.setJourneyPattern(this);
    }

    public void setServiceJourneys(List<ServiceJourney> serviceJourneys) {
        this.serviceJourneys.clear();
        if (serviceJourneys != null) {
            serviceJourneys.stream().forEach(sj -> sj.setJourneyPattern(this));
            this.serviceJourneys.addAll(serviceJourneys);
        }
    }

    public void addPointInSequence(StopPointInJourneyPattern pointInSequence) {
        this.pointsInSequence.add(pointInSequence);
        pointInSequence.setJourneyPattern(this);
    }

    public void setPointsInSequence(List<StopPointInJourneyPattern> pointsInSequence) {
        this.pointsInSequence.clear();
        if (pointsInSequence != null) {
            int i = 1;
            for (StopPointInJourneyPattern sp : pointsInSequence) {
                sp.setOrder(i++);
                sp.setJourneyPattern(this);
            }
            this.pointsInSequence.addAll(pointsInSequence);
        }
    }

    @Override
    public boolean isValid(LocalDate from, LocalDate to) {
        return super.isValid(from, to) && getServiceJourneys().stream().anyMatch(e -> e.isValid(from, to));
    }

    @Override
    public void checkPersistable() {
        super.checkPersistable();

        Preconditions.checkArgument(getPointsInSequence().size() >= 2,
                CodedError.fromErrorCode(ErrorCodeEnumeration.MINIMUM_POINTS_IN_SEQUENCE),
                "%s does not have minimum of 2 pointsInSequence", identity());

        getPointsInSequence().stream().forEach(ProviderEntity::checkPersistable);

        if (line instanceof FixedLine) {
            Preconditions.checkArgument(!Boolean.FALSE.equals(getPointsInSequence().getFirst().getForBoarding()),
                    "%s does not permit boarding on first pointsInSequence", identity());

            Preconditions.checkArgument(!Boolean.FALSE.equals(getPointsInSequence().getLast().getForAlighting()),
                    "%s does not permit alighting on last pointsInSequence", identity());
        } else if (line instanceof FlexibleLine) {
            Preconditions.checkArgument(getPointsInSequence().stream().anyMatch(stopPointInJourneyPattern -> BooleanUtils.isTrue(stopPointInJourneyPattern.getForBoarding())), "%s does not permit boarding", identity());
            Preconditions.checkArgument(getPointsInSequence().stream().anyMatch(stopPointInJourneyPattern -> BooleanUtils.isTrue(stopPointInJourneyPattern.getForAlighting())), "%s does not permit alighting", identity());
        }

        Preconditions.checkArgument(getPointsInSequence().getLast().getDestinationDisplay() == null,
                "%s has destinationDisplay for last pointsInSequence", identity());

        getServiceJourneys().stream().forEach(ProviderEntity::checkPersistable);
        if (getNotices() != null) {
            getNotices().stream().forEach(IdentifiedEntity::checkPersistable);
        }
    }
}
