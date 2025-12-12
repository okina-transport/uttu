/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.entur.uttu.model.job;

import jakarta.persistence.*;
import no.entur.uttu.util.Preconditions;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.text.MessageFormat;

@Entity
@SequenceGenerator(
        name = "export_message_seq_gen",
        sequenceName = "export_message_seq",
        allocationSize = 10
)
public class ExportMessage implements Comparable<ExportMessage> {

    @Id
    protected Long pk;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SeverityEnumeration severity;

    @NotNull
    @Column(length = 4000)
    @Size(max = 4000)
    private String message;

    private ExportMessage() {
    }

    public ExportMessage(SeverityEnumeration severity, String message, Object... params) {
        Preconditions.checkArgument(severity != null, "Severity must be assigned");
        Preconditions.checkArgument(message != null, "Severity must be assigned");
        this.severity = severity;
        this.message = MessageFormat.format(message, params);
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "ExportMessage{" +
                       "message='" + message + '\'' +
                       '}';
    }

    public Long getPk() {
        return pk;
    }

    public SeverityEnumeration getSeverity() {
        return severity;
    }

    @Override
    public int compareTo(ExportMessage o) {
        int severityCmp = o.severity.compareTo(this.severity);
        if (severityCmp != 0) {
            return severityCmp;
        }
        return this.message.compareTo(o.getMessage());
    }
}
