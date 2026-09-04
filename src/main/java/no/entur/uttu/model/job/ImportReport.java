package no.entur.uttu.model.job;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "job_import_report")
public class ImportReport {

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "job_id")
    private Job job;

    @Column(name = "error_message", length = 2000)
    private String errorMessage;

    @ElementCollection
    @CollectionTable(name = "job_import_report_stat", joinColumns = @JoinColumn(name = "report_id"))
    @MapKeyColumn(name = "stat_key")
    @Column(name = "stat_count")
    private Map<String, Integer> stats = new LinkedHashMap<>();

    @ElementCollection
    @CollectionTable(name = "job_import_report_line", joinColumns = @JoinColumn(name = "report_id"))
    @OrderColumn(name = "list_order")
    @Column(name = "line_name")
    private List<String> importedLineNames = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "job_import_report_file", joinColumns = @JoinColumn(name = "report_id"))
    @OrderColumn(name = "list_order")
    private List<ImportedFile> files = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, Integer> getStats() {
        return stats;
    }

    public void setStats(Map<String, Integer> stats) {
        this.stats = stats;
    }

    public List<String> getImportedLineNames() {
        return importedLineNames;
    }

    public void setImportedLineNames(List<String> importedLineNames) {
        this.importedLineNames = importedLineNames;
    }

    public List<ImportedFile> getFiles() {
        return files;
    }

    public void setFiles(List<ImportedFile> files) {
        this.files = files;
    }
}
