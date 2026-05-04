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
import no.entur.uttu.model.job.Export;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@SequenceGenerator(
        name = "identified_entity_gen",
        sequenceName = "exported_line_statistics_seq",
        allocationSize = 10
)
@EqualsAndHashCode(of = {"lineName", "operatingPeriodFrom", "operatingPeriodTo", "publicCode"})
@ToString(of = {"lineName", "operatingPeriodFrom", "operatingPeriodTo", "publicCode"})
@Data
public class ExportedLineStatistics {

    @OneToMany(mappedBy = "exportedLineStatistics", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @NotNull
    private final List<ExportedDayTypeStatistics> exportedDayTypesStatistics = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;
    @NotNull
    protected String lineName;
    @NotNull
    private LocalDate operatingPeriodFrom;
    @NotNull
    private LocalDate operatingPeriodTo;
    private String publicCode;
    @ManyToOne(optional = false)
    private Export export;

    public boolean isValid(LocalDate from, LocalDate to) {
        return !(operatingPeriodFrom.isAfter(to) || operatingPeriodTo.isBefore(from));
    }

    public void addExportedDayTypesStatistics(ExportedDayTypeStatistics exportedDayTypesStatisticsToAdd) {
        exportedDayTypesStatisticsToAdd.setExportedLineStatistics(this);
        exportedDayTypesStatistics.add(exportedDayTypesStatisticsToAdd);
    }
}
