package no.entur.uttu.importer.gtfsflex.validation;

import org.onebusaway.gtfs.model.BookingRule;
import org.springframework.stereotype.Component;

import static org.onebusaway.gtfs.model.BookingRule.NO_VALUE;

@Component
public class BookingRuleValidator {

    private static final int REAL_TIME_BOOKING = 0;
    private static final int SAME_DAY_BOOKING = 1;
    private static final int PRIOR_DAY_BOOKING = 2;

    public void validate(BookingRule entity) {
        int type = entity.getType();
        if (type != REAL_TIME_BOOKING && type != SAME_DAY_BOOKING && type != PRIOR_DAY_BOOKING) {
            throw error(entity, "booking_type=" + type + " is not a valid value (must be 0, 1 or 2)");
        }

        boolean hasDurationMin = entity.getPriorNoticeDurationMin() != NO_VALUE;
        boolean hasDurationMax = entity.getPriorNoticeDurationMax() != NO_VALUE;
        boolean hasLastDay = entity.getPriorNoticeLastDay() != NO_VALUE;
        boolean hasLastTime = entity.getPriorNoticeLastTime() != NO_VALUE;
        boolean hasStartDay = entity.getPriorNoticeStartDay() != NO_VALUE;
        boolean hasStartTime = entity.getPriorNoticeStartTime() != NO_VALUE;
        boolean hasServiceId = entity.getPriorNoticeServiceId() != null;

        switch (type) {
            case REAL_TIME_BOOKING -> {
                requireAbsent(entity, hasDurationMin, "prior_notice_duration_min");
                requireAbsent(entity, hasDurationMax, "prior_notice_duration_max");
                requireAbsent(entity, hasLastDay, "prior_notice_last_day");
                requireAbsent(entity, hasStartDay, "prior_notice_start_day");
                requireAbsent(entity, hasServiceId, "prior_notice_service_id");
            }
            case SAME_DAY_BOOKING -> {
                requirePresent(entity, hasDurationMin, "prior_notice_duration_min");
                requireAbsent(entity, hasLastDay, "prior_notice_last_day");
                requireAbsent(entity, hasServiceId, "prior_notice_service_id");
                if (hasStartDay && hasDurationMax) {
                    throw error(entity, "prior_notice_start_day is forbidden when prior_notice_duration_max is "
                            + "defined and booking_type=1");
                }
            }
            case PRIOR_DAY_BOOKING -> {
                requireAbsent(entity, hasDurationMin, "prior_notice_duration_min");
                requireAbsent(entity, hasDurationMax, "prior_notice_duration_max");
                requirePresent(entity, hasLastDay, "prior_notice_last_day");
            }
        }

        if (hasLastDay != hasLastTime) {
            throw error(entity, "prior_notice_last_day and prior_notice_last_time must both be set, or neither");
        }
        if (hasStartDay != hasStartTime) {
            throw error(entity, "prior_notice_start_day and prior_notice_start_time must both be set, or neither");
        }
    }

    private void requirePresent(BookingRule entity, boolean present, String field) {
        if (!present) {
            throw error(entity, field + " is required for booking_type=" + entity.getType());
        }
    }

    private void requireAbsent(BookingRule entity, boolean present, String field) {
        if (present) {
            throw error(entity, field + " is forbidden for booking_type=" + entity.getType());
        }
    }

    private static IllegalArgumentException error(BookingRule entity, String message) {
        return new IllegalArgumentException(String.format(
                "Invalid booking_rules.txt row for booking_rule_id=%s: %s",
                entity.getId().getId(), message));
    }
}
