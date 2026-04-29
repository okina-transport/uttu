package no.entur.uttu.importer.gtfsflex.mapper;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.model.DayType;
import no.entur.uttu.model.DayTypeAssignment;
import org.onebusaway.gtfs.model.ServiceCalendarDate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

import static org.onebusaway.gtfs.model.ServiceCalendarDate.EXCEPTION_TYPE_ADD;

@Component
@Slf4j
public class CalendarDateMapper implements Mapper<ServiceCalendarDate> {

    @Override
    public void map(ServiceCalendarDate gtfsEntity, Referential gtfsImportReferential) {
        log.info("Mapping calendar date {}/{}", gtfsEntity.getServiceId().getId(), gtfsEntity.getDate());
        DayType dayType = gtfsImportReferential.getDayType(gtfsEntity.getServiceId().getId());

        dayType.setName(gtfsEntity.getServiceId().getId());
        dayType.setProvider(gtfsImportReferential.getProvider(gtfsEntity.getServiceId().getAgencyId()));

        DayTypeAssignment dayTypeAssignment = gtfsImportReferential.getDayTypeAssignmentByServiceIdAndServiceDate(gtfsEntity.getServiceId().getId(), gtfsEntity.getDate());
        dayTypeAssignment.setDate(LocalDate.of(gtfsEntity.getDate().getYear(), gtfsEntity.getDate().getMonth(), gtfsEntity.getDate().getDay()));
        dayTypeAssignment.setAvailable(gtfsEntity.getExceptionType() == EXCEPTION_TYPE_ADD);
        dayType.getDayTypeAssignments().add(dayTypeAssignment);

        log.debug("gtfsEntity {}", gtfsEntity);
        log.debug("dayType {}", dayType);
    }

}
