package no.entur.uttu.exporter.gtfsflex;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.model.BookingArrangement;
import no.entur.uttu.model.PurchaseWhenEnumeration;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.BookingRule;

@Slf4j
public class BookingArrangementMapper {

    public static BookingRule map(BookingArrangement bookingArrangement){
        BookingRule bookingRule = new BookingRule();
        AgencyAndId bookingId = new AgencyAndId();
        bookingId.setId(String.valueOf(bookingArrangement.getPk()));
        bookingRule.setId(bookingId);

        switch (bookingArrangement.getBookWhen()) {
            case PurchaseWhenEnumeration.TIME_OF_TRAVEL_ONLY -> bookingRule.setType(0);
            case PurchaseWhenEnumeration.ADVANCE_AND_DAY_OF_TRAVEL -> bookingRule.setType(1);
            case PurchaseWhenEnumeration.UNTIL_PREVIOUS_DAY -> bookingRule.setType(2);
            default -> log.warn("Unhandled bookWhen: {}", bookingArrangement.getBookWhen());
        }

        if (bookingArrangement.getMinimumBookingPeriod() != null){
            long priorNoticeDuration = bookingArrangement.getMinimumBookingPeriod().getSeconds() / 60;
            bookingRule.setPriorNoticeDurationMin((int) priorNoticeDuration);
        }

        if (bookingArrangement.getPriorNoticeDurationMax() != null){
            bookingRule.setPriorNoticeDurationMax(bookingArrangement.getPriorNoticeDurationMax());
        }

        if (bookingArrangement.getPriorNoticeLastDay() != null){
            bookingRule.setPriorNoticeLastDay(bookingArrangement.getPriorNoticeLastDay());
        }

        if (bookingArrangement.getLatestBookingTime() != null){
            bookingRule.setPriorNoticeLastTime(bookingArrangement.getLatestBookingTime().toSecondOfDay());
        }

        if (bookingArrangement.getPriorNoticeStartDay() != null){
            bookingRule.setPriorNoticeStartDay(bookingArrangement.getPriorNoticeStartDay());
        }

        if (StringUtils.isNotBlank(bookingArrangement.getServiceId())){
            AgencyAndId serviceId = new AgencyAndId();
            serviceId.setId(bookingArrangement.getServiceId());
            bookingRule.setPriorNoticeServiceId(serviceId);
        }

        if (bookingArrangement.getBookingContact() != null){
            bookingRule.setInfoUrl(bookingArrangement.getBookingContact().getUrl());
            bookingRule.setPhoneNumber(bookingArrangement.getBookingContact().getPhone());
            bookingRule.setMessage(bookingArrangement.getBookingContact().getFurtherDetails());
        }



        return bookingRule;
    }

}
