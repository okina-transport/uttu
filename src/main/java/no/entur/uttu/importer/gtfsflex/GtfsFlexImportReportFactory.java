package no.entur.uttu.importer.gtfsflex;

import no.entur.uttu.model.FlexibleLine;
import no.entur.uttu.model.job.ImportReport;
import no.entur.uttu.model.job.ImportedFile;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

@Component
public class GtfsFlexImportReportFactory {

    public ImportReport buildSuccessReport(File gtfsZip, Referential referential) {
        ImportReport report = new ImportReport();
        report.setStats(buildStats(referential));
        report.setImportedLineNames(buildLineNames(referential));
        report.setFiles(listFiles(gtfsZip).stream()
                .map(fileName -> new ImportedFile(fileName, true))
                .collect(Collectors.toCollection(ArrayList::new)));
        return report;
    }

    public ImportReport buildFailureReport(File gtfsZip, Exception error) {
        ImportReport report = new ImportReport();
        report.setErrorMessage(error.getMessage() != null ? error.getMessage() : error.getClass().getSimpleName());
        String invalidFile = error instanceof GtfsFileMappingException gtfsFileMappingException
                ? gtfsFileMappingException.getFileName()
                : null;
        report.setFiles(listFiles(gtfsZip).stream()
                .map(fileName -> new ImportedFile(fileName, !fileName.equals(invalidFile)))
                .collect(Collectors.toCollection(ArrayList::new)));
        return report;
    }

    private Map<String, Integer> buildStats(Referential referential) {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("Compagnies", referential.getProvidersByOriginalId().size());
        stats.put("Réseaux", referential.getNetworksByOriginalId().size());
        stats.put("Lignes", referential.getFlexibleLinesByOriginalId().size());
        // GTFS Flex import never produces interchanges/transfers.
        stats.put("Correspondances", 0);
        stats.put("Arrêts", referential.getStopsByOriginalId().size());
        stats.put("Zones flexibles", referential.getFlexibleStopPlacesByOriginalId().size());
        stats.put("Courses", referential.getServiceJourneysByOriginalId().size());
        stats.put("Parcours", referential.getJourneyPatternsByOriginalId().size());
        return stats;
    }

    private List<String> buildLineNames(Referential referential) {
        return referential.getFlexibleLinesByOriginalId().values().stream()
                .map(this::lineName)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String lineName(FlexibleLine line) {
        return StringUtils.isNotBlank(line.getName()) ? line.getName() : line.getPublicCode();
    }

    private List<String> listFiles(File gtfsZip) {
        try (ZipFile zipFile = new ZipFile(gtfsZip)) {
            return zipFile.stream()
                    .filter(entry -> !entry.isDirectory())
                    .map(ZipEntry::getName)
                    .sorted()
                    .toList();
        } catch (IOException e) {
            return List.of();
        }
    }
}
