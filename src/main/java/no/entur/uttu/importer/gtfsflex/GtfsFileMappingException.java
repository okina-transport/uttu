package no.entur.uttu.importer.gtfsflex;

public class GtfsFileMappingException extends RuntimeException {

    private final String fileName;

    public GtfsFileMappingException(String fileName, RuntimeException cause) {
        super(cause.getMessage(), cause);
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
