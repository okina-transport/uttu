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

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Entity
@Data
@EqualsAndHashCode(callSuper = true, of = "flexibleLineType")
@ToString(callSuper = true, of = "flexibleLineType")
public class FlexibleLine extends Line {

    @Enumerated(EnumType.STRING)
    @NotNull
    private FlexibleLineTypeEnumeration flexibleLineType;

    @OneToOne(cascade = CascadeType.ALL)
    private BookingArrangement bookingArrangement;

    @Override
    public void accept(LineVisitor lineVisitor) {
        lineVisitor.visitFlexibleLine(this);
    }

    @Override
    public void checkPersistable() {
        super.checkPersistable();

        validateBookingInformations();
    }

    private void validateBookingInformations() {
        validateBookingInformation(this.bookingArrangement);
        this.getJourneyPatterns().stream().forEach(jp -> {
            jp.getPointsInSequence().stream().forEach(stopPoint -> validateBookingInformation(stopPoint.getBookingArrangement()));
            jp.getServiceJourneys().stream().forEach(sj -> validateBookingInformation(sj.getBookingArrangement()));
        });

    }

    private void validateBookingInformation(BookingArrangement bookingArrangement) {
        if (bookingArrangement == null) return;
        bookingArrangement.checkPersistable();
    }
}
