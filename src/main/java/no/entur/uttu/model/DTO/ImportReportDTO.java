package no.entur.uttu.model.DTO;

import lombok.Getter;
import no.entur.uttu.model.job.ImportReport;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
public class ImportReportDTO {

    private final String errorMessage;
    private final Map<String, Integer> stats;
    private final List<String> importedLineNames;
    private final List<ImportedFileDTO> files;

    public ImportReportDTO(ImportReport report) {
        this.errorMessage = report.getErrorMessage();
        this.stats = new LinkedHashMap<>(report.getStats());
        this.importedLineNames = new ArrayList<>(report.getImportedLineNames());
        this.files = report.getFiles().stream().map(ImportedFileDTO::new).collect(Collectors.toList());
    }
}
