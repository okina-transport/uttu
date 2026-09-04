package no.entur.uttu.model.job;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ImportedFile {

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "valid_file")
    private boolean valid;

    public ImportedFile() {
    }

    public ImportedFile(String fileName, boolean valid) {
        this.fileName = fileName;
        this.valid = valid;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
