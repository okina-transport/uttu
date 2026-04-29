package no.entur.uttu.model;

import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static no.entur.uttu.model.Constraints.PROVIDER_UNIQUE_CODE;

@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name = PROVIDER_UNIQUE_CODE, columnNames = "code")})
@SequenceGenerator(
        name = "identified_entity_gen",
        sequenceName = "stop_seq",
        allocationSize = 10
)
@EqualsAndHashCode(callSuper = true)
@Data
public class Stop extends ProviderEntity {
}
