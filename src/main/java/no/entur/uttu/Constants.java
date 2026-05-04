package no.entur.uttu;

public class Constants {

    public static final String OKINA_REFERENTIAL = "x-okina-referential";
    public static final String USER_HEADER = "RutebankenUser";
    public static final String JOB_ID = "RutebankenJobId";
    public static final String UTTU_IMPORT_STATUS = "uttuImportStatus";
    public static final String GTFS_FLEX_FILE = "gtfsFlexFile";

    private Constants() {
        throw new IllegalStateException("Utility class");
    }
}
