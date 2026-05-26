package no.entur.uttu.exporter.gtfsflex;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.model.DayType;
import no.entur.uttu.model.DayTypeAssignment;
import org.apache.commons.collections4.CollectionUtils;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
public class DayTypeMapper {

    private static final DateTimeFormatter GTFS_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");


    public static ServiceCalendar map(DayType dayType){
        ServiceCalendar calendar = new ServiceCalendar();

        AgencyAndId calendarId = new AgencyAndId();
        calendarId.setId(dayType.getOriginalId());
        calendar.setServiceId(calendarId);

        if (CollectionUtils.isNotEmpty(dayType.getDaysOfWeek())){
            for (DayOfWeek dayOfWeek : dayType.getDaysOfWeek()) {
                if (DayOfWeek.MONDAY.equals(dayOfWeek)){
                    calendar.setMonday(1);
                }

                if (DayOfWeek.TUESDAY.equals(dayOfWeek)){
                    calendar.setTuesday(1);
                }

                if (DayOfWeek.WEDNESDAY.equals(dayOfWeek)){
                    calendar.setWednesday(1);
                }

                if (DayOfWeek.THURSDAY.equals(dayOfWeek)){
                    calendar.setThursday(1);
                }

                if (DayOfWeek.FRIDAY.equals(dayOfWeek)){
                    calendar.setFriday(1);
                }

                if (DayOfWeek.SATURDAY.equals(dayOfWeek)){
                    calendar.setSaturday(1);
                }

                if (DayOfWeek.SUNDAY.equals(dayOfWeek)){
                    calendar.setSunday(1);
                }
            }
        }

        if (CollectionUtils.isNotEmpty(dayType.getDayTypeAssignments())){

            List<DayTypeAssignment> operatingPeriods = dayType.getDayTypeAssignments().stream()
                    .filter(dta -> dta.getOperatingPeriod() != null)
                    .toList();

            if (operatingPeriods.size() > 1){
                log.warn("More than one operating period found in dayType:{}", dayType.getNetexId());
            }


            DayTypeAssignment assignment = operatingPeriods.getFirst();
            LocalDate fromDate = assignment.getOperatingPeriod().getFromDate();
            ServiceDate startDate = new ServiceDate(fromDate.getYear(), fromDate.getMonthValue(), fromDate.getDayOfMonth());
            calendar.setStartDate(startDate);

            LocalDate toDate = assignment.getOperatingPeriod().getToDate();
            ServiceDate endDate = new ServiceDate(toDate.getYear(), toDate.getMonthValue(), toDate.getDayOfMonth());
            calendar.setEndDate(endDate);
        }

        return calendar;
    }
}
