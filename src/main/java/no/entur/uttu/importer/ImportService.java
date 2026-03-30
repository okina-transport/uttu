package no.entur.uttu.importer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ImportService {

    private static final Logger logger = LoggerFactory.getLogger(ImportService.class);


    public ImportService() {
    }

    @Transactional(readOnly = true)
    public String importGtfsFlex() {
        // todo: à relier au parsing développer dans un autre ticket
        return "import is OK";
    }
}
