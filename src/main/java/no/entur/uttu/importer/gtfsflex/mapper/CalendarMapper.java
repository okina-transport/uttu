package no.entur.uttu.importer.gtfsflex.mapper;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.model.DayType;
import no.entur.uttu.model.DayTypeAssignment;
import no.entur.uttu.model.OperatingPeriod;
import org.onebusaway.gtfs.model.ServiceCalendar;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class CalendarMapper implements Mapper<ServiceCalendar> {

    @Override
    public void map(ServiceCalendar gtfsEntity, Referential gtfsImportReferential) {
        log.info("Mapping calendar {}", gtfsEntity.getServiceId().getId());
        DayType dayType = gtfsImportReferential.getDayType(gtfsEntity.getServiceId().getId());

        dayType.setName(gtfsEntity.getServiceId().getId());
        dayType.setProvider(gtfsImportReferential.getProvider(gtfsEntity.getServiceId().getAgencyId()));

        List<DayOfWeek> daysOfWeek = new ArrayList<>();
        if (gtfsEntity.getMonday() == 1) {
            daysOfWeek.add(DayOfWeek.MONDAY);
        }
        if (gtfsEntity.getTuesday() == 1) {
            daysOfWeek.add(DayOfWeek.TUESDAY);
        }
        if (gtfsEntity.getWednesday() == 1) {
            daysOfWeek.add(DayOfWeek.WEDNESDAY);
        }
        if (gtfsEntity.getThursday() == 1) {
            daysOfWeek.add(DayOfWeek.THURSDAY);
        }
        if (gtfsEntity.getFriday() == 1) {
            daysOfWeek.add(DayOfWeek.FRIDAY);
        }
        if (gtfsEntity.getSaturday() == 1) {
            daysOfWeek.add(DayOfWeek.SATURDAY);
        }
        if (gtfsEntity.getSunday() == 1) {
            daysOfWeek.add(DayOfWeek.SUNDAY);
        }
        dayType.setDaysOfWeek(daysOfWeek);

        OperatingPeriod operatingPeriod = gtfsImportReferential.getOperatingPeriod(gtfsEntity.getServiceId().getId());
        operatingPeriod.setFromDate(LocalDate.of(gtfsEntity.getStartDate().getYear(), gtfsEntity.getStartDate().getMonth(), gtfsEntity.getStartDate().getDay()));
        operatingPeriod.setToDate(LocalDate.of(gtfsEntity.getEndDate().getYear(), gtfsEntity.getEndDate().getMonth(), gtfsEntity.getEndDate().getDay()));

        DayTypeAssignment dayTypeAssignment = gtfsImportReferential.getDayTypeAssignmentByServiceId(gtfsEntity.getServiceId().getId());
        dayTypeAssignment.setOperatingPeriod(operatingPeriod);

        dayType.getDayTypeAssignments().add(dayTypeAssignment);

        log.debug("gtfsEntity {}", gtfsEntity);
        log.debug("dayType {}", dayType);
    }

}
