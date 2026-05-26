package no.entur.uttu.importer.gtfsflex.mapper;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.model.BookingArrangement;
import no.entur.uttu.model.Contact;
import no.entur.uttu.model.PurchaseWhenEnumeration;
import org.apache.commons.lang3.StringUtils;
import org.onebusaway.gtfs.model.BookingRule;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalTime;

import static org.onebusaway.gtfs.model.BookingRule.NO_VALUE;

@Component
@Slf4j
public class BookingRuleMapper implements Mapper<BookingRule> {

    @Override
    public void map(BookingRule gtfsEntity, Referential gtfsImportReferential) {
        log.info("Mapping booking rule {}", gtfsEntity.getId().getId());

        BookingArrangement bookingArrangement = gtfsImportReferential.getBookingArrangement(gtfsEntity.getId().getId());
        switch (gtfsEntity.getType()) {
            case 0 -> bookingArrangement.setBookWhen(PurchaseWhenEnumeration.TIME_OF_TRAVEL_ONLY);
            case 1 -> bookingArrangement.setBookWhen(PurchaseWhenEnumeration.ADVANCE_AND_DAY_OF_TRAVEL);
            case 2 -> bookingArrangement.setBookWhen(PurchaseWhenEnumeration.UNTIL_PREVIOUS_DAY);
            default -> log.warn("Unhandled booking_type: {}", gtfsEntity.getType());
        }

        if (gtfsEntity.getPriorNoticeDurationMin() != NO_VALUE) {
            bookingArrangement.setMinimumBookingPeriod(Duration.ofMinutes(gtfsEntity.getPriorNoticeDurationMin()));
        }

        if (gtfsEntity.getPriorNoticeLastTime() != NO_VALUE) {
            bookingArrangement.setLatestBookingTime(LocalTime.ofSecondOfDay(gtfsEntity.getPriorNoticeLastTime()));
        }

        if (gtfsEntity.getPriorNoticeDurationMax() != NO_VALUE){
            bookingArrangement.setPriorNoticeDurationMax(gtfsEntity.getPriorNoticeDurationMax());
        }

        if (gtfsEntity.getPriorNoticeLastDay() != NO_VALUE){
            bookingArrangement.setPriorNoticeLastDay(gtfsEntity.getPriorNoticeLastDay());
        }

        if (gtfsEntity.getPriorNoticeStartDay() != NO_VALUE){
            bookingArrangement.setPriorNoticeStartDay(gtfsEntity.getPriorNoticeStartDay());
        }

        if (gtfsEntity.getPriorNoticeStartTime() != NO_VALUE){
            bookingArrangement.setPriorNoticeStartTime(LocalTime.ofSecondOfDay(gtfsEntity.getPriorNoticeStartTime()));
        }

        if (gtfsEntity.getPriorNoticeServiceId() != null){
            bookingArrangement.setServiceId(gtfsEntity.getPriorNoticeServiceId().getId());
        }

        if (StringUtils.isBlank(gtfsEntity.getPhoneNumber()) && StringUtils.isBlank(gtfsEntity.getUrl()) && StringUtils.isBlank(gtfsEntity.getInfoUrl()) && StringUtils.isBlank(gtfsEntity.getMessage()) && StringUtils.isBlank(gtfsEntity.getPickupMessage()) && StringUtils.isBlank(gtfsEntity.getDropOffMessage())) {
            log.info("No contact information for booking rule {}", gtfsEntity.getId().getId());
            return;
        }

        Contact contact = gtfsImportReferential.getContact(gtfsEntity.getId().getId());
        contact.setPhone(gtfsEntity.getPhoneNumber());
        if (StringUtils.isNotBlank(gtfsEntity.getInfoUrl())) {
            contact.setUrl(gtfsEntity.getInfoUrl());
        }
        if (StringUtils.isNotBlank(gtfsEntity.getUrl())) {
            // if info_url & url are set, keep url
            contact.setUrl(gtfsEntity.getUrl());
        }
        if (StringUtils.isNotBlank(gtfsEntity.getPickupMessage())) {
            contact.setFurtherDetails(gtfsEntity.getPickupMessage());
        }
        if (StringUtils.isNotBlank(gtfsEntity.getDropOffMessage())) {
            contact.setFurtherDetails(gtfsEntity.getDropOffMessage());
        }
        if (StringUtils.isNotBlank(gtfsEntity.getMessage())) {
            // if message & pickup_message/drop_off_message are set, keep message
            contact.setFurtherDetails(gtfsEntity.getMessage());
        }
        bookingArrangement.setBookingContact(contact);


        log.debug("gtfsEntity {}", gtfsEntity);
        log.debug("bookingArrangement {}", bookingArrangement);
    }

}
