package no.entur.uttu.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@SequenceGenerator(
        name = "identified_entity_gen",
        sequenceName = "exported_day_type_statistics_seq",
        allocationSize = 10
)
@EqualsAndHashCode(of = {"serviceJourneyName", "dayTypeNetexId", "operatingPeriodFrom", "operatingPeriodTo"})
@ToString(of = {"serviceJourneyName", "dayTypeNetexId", "operatingPeriodFrom", "operatingPeriodTo"})
@Data
public class ExportedDayTypeStatistics {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    @NotNull
    protected String serviceJourneyName;
    @NotNull
    protected String dayTypeNetexId;
    @ManyToOne(optional = false)
    private ExportedLineStatistics exportedLineStatistics;
    @NotNull
    private LocalDate operatingPeriodFrom;

    @NotNull
    private LocalDate operatingPeriodTo;
}
