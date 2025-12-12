package no.entur.uttu.export.netex;

import no.entur.uttu.error.codedexception.CodedIllegalArgumentException;
import no.entur.uttu.model.FixedLine;
import no.entur.uttu.model.Line;
import no.entur.uttu.model.job.ExportLineAssociation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class NetexExporterTest {
    @Test
    public void findLinesToExportReturnsAll() {
        NetexExporter exporter = new NetexExporter();

        Line line = new FixedLine();

        Assertions.assertEquals(
                1,
                exporter.findLinesToExport(null, List.of(line)).size()
        );
    }

    @Test
    public void findLinesToExportFiltersWithLineAssociation() {
        NetexExporter exporter = new NetexExporter();

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
    public void findLinesToExportErrorIfEmpty() {

        assertThrows(CodedIllegalArgumentException.class, () -> {
            NetexExporter exporter = new NetexExporter();
            exporter.findLinesToExport(List.of(), List.of());
        });



    }

    @Test()
    public void findLinesToExportErrorIfEmptyAfterFiltering() {

        assertThrows(CodedIllegalArgumentException.class, () -> {
            NetexExporter exporter = new NetexExporter();

            Line line1 = new FixedLine();
            Line line2 = new FixedLine();

            ExportLineAssociation la = new ExportLineAssociation();
            la.setLine(line1);

            exporter.findLinesToExport(List.of(la), List.of(line2));
        });


    }
}
