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

import com.google.common.base.Joiner;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import no.entur.uttu.config.Context;
import no.entur.uttu.util.Preconditions;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.UUID;

/**
 * Abstract superclass for all entities belong to a provider.
 */
@MappedSuperclass
@Data
@EqualsAndHashCode(callSuper = true, of = {"netexId"})
@ToString(callSuper = true, of = {"netexId"})
public abstract class ProviderEntity extends IdentifiedEntity {

    @ManyToOne(optional = false)
    protected Provider provider;

    @NotNull
    @Column(unique = true)
    protected String netexId;

    public String getNetexVersion() {
        return Objects.toString(version);
    }

    @PrePersist
    public void setNetexIdIfMissing() {
        if (StringUtils.isBlank(netexId)) {
            this.setNetexId(Joiner.on(":").join(getProvider().getCodespace().getXmlns(), getNetexName(), UUID.randomUUID()));
        }
    }

    public String getNetexName() {
        return this.getClass().getSimpleName();
    }


    @PreUpdate
    protected void verifyProvider() {
        String providerCode = Context.getVerifiedProviderCode();
        Preconditions.checkArgument(Objects.equals(this.getProvider().getCode(), providerCode),
                "Provider mismatch, attempting to store entity[½s] in context of provider[%s] .", this, providerCode);
    }

    public Ref getRef() {
        return new Ref(getNetexId(), getNetexVersion());
    }

    public String identity() {
        return MessageFormat.format("{0}[{1}]", getClass().getSimpleName(), getNetexId());
    }
}
