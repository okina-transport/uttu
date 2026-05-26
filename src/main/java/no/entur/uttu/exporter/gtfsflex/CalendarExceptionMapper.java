package no.entur.uttu.exporter.gtfsflex;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.model.DayType;
import no.entur.uttu.model.DayTypeAssignment;
import org.apache.commons.collections4.CollectionUtils;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.onebusaway.gtfs.model.calendar.ServiceDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CalendarExceptionMapper {


    public static List<ServiceCalendarDate> map(DayType dayType){
        List<ServiceCalendarDate> results = new ArrayList<>();


        if (CollectionUtils.isNotEmpty(dayType.getDayTypeAssignments())){
            for (DayTypeAssignment dayTypeAssignment : dayType.getDayTypeAssignments()) {
                if (dayTypeAssignment.getOperatingPeriod() != null){
                    continue;
                }

                LocalDate date = dayTypeAssignment.getDate();
                ServiceCalendarDate serviceCalendarDate = new ServiceCalendarDate();
                ServiceDate serviceDate = new ServiceDate(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
                serviceCalendarDate.setDate(serviceDate);
                AgencyAndId serviceCalendarId = new AgencyAndId();
                serviceCalendarId.setId(dayType.getOriginalId());
                serviceCalendarDate.setServiceId(serviceCalendarId);
                serviceCalendarDate.setExceptionType(Boolean.TRUE.equals(dayTypeAssignment.getAvailable()) ? 1 : 2);
                results.add(serviceCalendarDate);
            }
        }

        return results;
    }
}
