package no.entur.uttu.export.netex;

import no.entur.uttu.UttuIntegrationTest;
import no.entur.uttu.error.codedexception.CodedIllegalArgumentException;
import no.entur.uttu.model.FixedLine;
import no.entur.uttu.model.Line;
import no.entur.uttu.model.job.ExportLineAssociation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class NetexExporterTest extends UttuIntegrationTest {

    @Autowired
    private NetexExporter exporter;

    @Test
    void findLinesToExportReturnsAll() {
        Line line = new FixedLine();

        Assertions.assertEquals(
                1,
                exporter.findLinesToExport(null, List.of(line)).size()
        );
    }

    @Test
    void findLinesToExportFiltersWithLineAssociation() {
        Line line1 = new FixedLine();
        Line line2 = new FixedLine();

        ExportLineAssociation la = new ExportLineAssociation();
        la.setLine(line1);

        Assertions.assertEquals(
                1,
                exporter.findLinesToExport(Collections.singletonList(la), List.of(line1, line2)).size()
        );
        Assertions.assertEquals(
                line1,
                exporter.findLinesToExport(Collections.singletonList(la), List.of(line1, line2)).get(0)
        );
    }

    @Test()
    void findLinesToExportErrorIfEmpty() {
        assertThrows(CodedIllegalArgumentException.class, () -> {

            exporter.findLinesToExport(List.of(), List.of());
        });


    }

    @Test()
    void findLinesToExportErrorIfEmptyAfterFiltering() {
        assertThrows(CodedIllegalArgumentException.class, () -> {


            Line line1 = new FixedLine();
            Line line2 = new FixedLine();

            ExportLineAssociation la = new ExportLineAssociation();
            la.setLine(line1);

            exporter.findLinesToExport(List.of(la), List.of(line2));
        });


    }
}
