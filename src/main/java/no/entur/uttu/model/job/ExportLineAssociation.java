package no.entur.uttu.model.job;

import jakarta.persistence.*;
import no.entur.uttu.model.Line;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

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

    @ManyToOne(optional = false)
    private Export export;

    @ManyToOne(optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
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

