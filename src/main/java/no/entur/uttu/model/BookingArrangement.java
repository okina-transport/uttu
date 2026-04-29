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

import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

@Entity
@SequenceGenerator(
        name = "identified_entity_gen",
        sequenceName = "booking_arrangement_seq",
        allocationSize = 10
)
@EqualsAndHashCode(callSuper = true, of = {"latestBookingTime", "minimumBookingPeriod", "bookingNote", "bookingMethods", "bookingAccess", "bookWhen", "buyWhen"})
@ToString(callSuper = true, of = {"latestBookingTime", "minimumBookingPeriod", "bookingNote", "bookingMethods", "bookingAccess", "bookWhen", "buyWhen"})
@Data
public class BookingArrangement extends IdentifiedEntity {

    private LocalTime latestBookingTime;

    private Duration minimumBookingPeriod;

    private String bookingNote;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<BookingMethodEnumeration> bookingMethods;
    @Enumerated(EnumType.STRING)
    private BookingAccessEnumeration bookingAccess;
    @Enumerated(EnumType.STRING)
    private PurchaseWhenEnumeration bookWhen;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private List<PurchaseMomentEnumeration> buyWhen;

    @OneToOne(cascade = CascadeType.ALL)
    private Contact bookingContact;
}
