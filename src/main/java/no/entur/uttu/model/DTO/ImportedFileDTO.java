package no.entur.uttu.model.DTO;

import lombok.Getter;
import no.entur.uttu.model.job.ImportedFile;

@Getter
public class ImportedFileDTO {

    private final String fileName;
    private final boolean valid;

    public ImportedFileDTO(ImportedFile file) {
        this.fileName = file.getFileName();
        this.valid = file.isValid();
    }
}
