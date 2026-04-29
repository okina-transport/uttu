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

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.entur.uttu.config.Context;

import javax.validation.constraints.NotNull;
import java.time.Instant;
import java.time.LocalDate;

@MappedSuperclass
@EqualsAndHashCode(of = {"version", "datasetId", "originalId"})
@ToString(of = {"version", "datasetId", "originalId"})
@Data
public abstract class IdentifiedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "identified_entity_gen")
    protected Long pk;

    @Version
    @NotNull
    protected Long version;

    @NotNull
    protected Instant created;

    @NotNull
    protected Instant changed;

    @NotNull
    protected String createdBy;

    @NotNull
    protected String changedBy;

    @Nullable
    protected String datasetId;

    @Nullable
    protected String originalId;

    @Transient
    protected boolean merged;

    @PrePersist
    @PreUpdate
    protected void setMetaData() {
        String user = Context.getUsername();
        Instant now = Instant.now();
        this.setChanged(now);
        this.setChangedBy(user);

        if (this.getCreated() == null) {
            this.setCreated(now);
            this.setCreatedBy(user);
        }
    }

    /**
     * Check whether entity is complete and consistent.
     * <p>
     * throws exception if not in a persistable state.
     */
    public void checkPersistable() {
    }

    /**
     * Check whether entity is valid within a period.
     *
     * @return if entity is valid for at least a part of the period.
     */
    public boolean isValid(LocalDate from, LocalDate to) {
        return true;
    }

}
