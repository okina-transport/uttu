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

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static no.entur.uttu.model.Constraints.LINE_UNIQUE_NAME;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(uniqueConstraints = {@UniqueConstraint(name = LINE_UNIQUE_NAME, columnNames = {"provider_pk", "name"})})
@SequenceGenerator(
        name = "identified_entity_gen",
        sequenceName = "line_seq",
        allocationSize = 10
)
@Data
@EqualsAndHashCode(callSuper = true, of = {"publicCode", "transportMode", "transportSubmode", "operatorRef"})
@ToString(callSuper = true, of = {"publicCode", "transportMode", "transportSubmode", "operatorRef"})
public abstract class Line extends GroupOfEntitiesVersionStructure {

    @OneToMany(mappedBy = "line", orphanRemoval = true)
    private final List<JourneyPattern> journeyPatterns = new ArrayList<>();
    private String publicCode;
    @Enumerated(EnumType.STRING)
    @NotNull
    private VehicleModeEnumeration transportMode;
    @Enumerated(EnumType.STRING)
    @NotNull
    private VehicleSubmodeEnumeration transportSubmode;
    @ManyToOne(optional = false)
    private Network network;
    private String operatorRef;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Notice> notices = new ArrayList<>();

    public void addJourneyPattern(JourneyPattern journeyPattern) {
        this.journeyPatterns.add(journeyPattern);
        journeyPattern.setLine(this);
    }

    public void setJourneyPatterns(List<JourneyPattern> journeyPatterns) {
        this.journeyPatterns.clear();
        if (journeyPatterns != null) {
            journeyPatterns.stream().forEach(jp -> jp.setLine(this));
            this.journeyPatterns.addAll(journeyPatterns);
        }
    }

    @Override
    public boolean isValid(LocalDate from, LocalDate to) {
        return super.isValid(from, to) && getJourneyPatterns().stream().anyMatch(e -> e.isValid(from, to));
    }

    @Override
    public void checkPersistable() {
        super.checkPersistable();

        Preconditions.checkArgument(transportMode != null, "% transportMode not set", identity());
        Preconditions.checkArgument(transportSubmode != null, "% transportSubmode not set", identity());
        Preconditions.checkArgument(Objects.equals(transportMode, transportSubmode.getVehicleMode()), "%s transportSubmode %s is valid for transportMode %s", identity(), transportSubmode.value(), transportMode.value());

        getJourneyPatterns().forEach(ProviderEntity::checkPersistable);
        if (getNotices() != null) {
            getNotices().forEach(IdentifiedEntity::checkPersistable);
        }
    }

    public abstract void accept(LineVisitor lineVisitor);
}
