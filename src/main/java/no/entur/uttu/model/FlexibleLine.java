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

package no.entur.uttu.model;

import no.entur.uttu.export.netex.NetexExportContext;
import no.entur.uttu.export.netex.producer.line.LineProducerVisitor;
import no.entur.uttu.export.netex.producer.line.RouteProducerVisitor;
import org.rutebanken.netex.model.LineRefStructure;
import org.rutebanken.netex.model.Line_VersionStructure;
import org.rutebanken.netex.model.NoticeAssignment;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
public class FlexibleLine extends Line {

    @Enumerated(EnumType.STRING)
    @NotNull
    private FlexibleLineTypeEnumeration flexibleLineType;

    @OneToOne(cascade = CascadeType.ALL)
    private BookingArrangement bookingArrangement;

    public FlexibleLineTypeEnumeration getFlexibleLineType() {
        return flexibleLineType;
    }

    public void setFlexibleLineType(FlexibleLineTypeEnumeration flexibleLineType) {
        this.flexibleLineType = flexibleLineType;
    }

    public BookingArrangement getBookingArrangement() {
        return bookingArrangement;
    }

    public void setBookingArrangement(BookingArrangement bookingArrangement) {
        this.bookingArrangement = bookingArrangement;
    }

    @Override
    public Line_VersionStructure accept(LineProducerVisitor visitor, List<NoticeAssignment> noticeAssignments, NetexExportContext context) {
        return visitor.visitFlexibleLine(this, noticeAssignments, context);
    }

    @Override
    public LineRefStructure accept(RouteProducerVisitor visitor) {
        return visitor.visitFlexibleLine(this);
    }
}
