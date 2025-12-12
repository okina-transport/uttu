package no.entur.uttu.model.job;

import jakarta.persistence.*;
import no.entur.uttu.model.Line;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.validation.constraints.NotNull;

@Entity
@SequenceGenerator(
        name = "export_line_association_seq_gen",
        sequenceName = "export_line_association_seq",
        allocationSize = 10
)
public class ExportLineAssociation {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    protected Long id;

    @ManyToOne
    @NotNull
    private Export export;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @NotNull
    private Line line;

    public Long getId() {
        return id;
    }

    public Export getExport() {
        return export;
    }

    public void setExport(Export export) {
        this.export = export;
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }
}

