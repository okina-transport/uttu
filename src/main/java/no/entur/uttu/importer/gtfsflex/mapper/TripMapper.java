package no.entur.uttu.importer.gtfsflex.mapper;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.model.*;
import no.entur.uttu.repository.CompanyRegistry;
import org.onebusaway.gtfs.model.Trip;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TripMapper implements Mapper<Trip> {

    private final CompanyRegistry companyRegistry;

    public TripMapper(CompanyRegistry companyRegistry) {
        this.companyRegistry = companyRegistry;
    }

    @Override
    public void map(Trip gtfsEntity, Referential gtfsImportReferential) {
        log.info("Mapping trip {}", gtfsEntity.getId().getId());
        Provider provider = gtfsImportReferential.getProvider(gtfsEntity.getId().getAgencyId());

        ServiceJourney serviceJourney = gtfsImportReferential.getServiceJourney(gtfsEntity.getId().getId());
        serviceJourney.setOperatorRef(companyRegistry.getVerifiedOperatorRef(gtfsImportReferential.getDataset()));
        serviceJourney.updateDayTypes(List.of(gtfsImportReferential.getDayType(gtfsEntity.getServiceId().getId())));
        serviceJourney.setProvider(provider);

        JourneyPattern journeyPattern = gtfsImportReferential.getJourneyPattern(gtfsEntity.getId().getId());
        journeyPattern.setShortName(gtfsEntity.getTripShortName());
        journeyPattern.setDirectionType(DirectionTypeEnumeration.fromDirectionId(gtfsEntity.getDirectionId()));
        journeyPattern.setProvider(provider);
        journeyPattern.addServiceJourney(serviceJourney);

        FlexibleLine flexibleLine = gtfsImportReferential.getFlexibleLine(gtfsEntity.getRoute().getId().getId());
        flexibleLine.addJourneyPattern(journeyPattern);

        log.debug("gtfsEntity {}", gtfsEntity);
        log.debug("serviceJourney {}", serviceJourney);
    }

}
