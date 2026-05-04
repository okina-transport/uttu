package no.entur.uttu.importer.gtfsflex.mapper;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.model.FlexibleLine;
import no.entur.uttu.model.FlexibleLineTypeEnumeration;
import no.entur.uttu.model.VehicleModeEnumeration;
import no.entur.uttu.model.VehicleSubmodeEnumeration;
import org.apache.commons.lang3.StringUtils;
import org.onebusaway.gtfs.model.Route;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RouteMapper implements Mapper<Route> {

    @Override
    public void map(Route gtfsEntity, Referential gtfsImportReferential) {
        log.info("Mapping route {}", gtfsEntity.getId().getId());
        FlexibleLine flexibleLine = gtfsImportReferential.getFlexibleLine(gtfsEntity.getId().getId());

        flexibleLine.setPublicCode(gtfsEntity.getShortName());
        flexibleLine.setName(StringUtils.truncate(gtfsEntity.getLongName(), 255));
        flexibleLine.setShortName(StringUtils.truncate(gtfsEntity.getShortName(), 255));
        flexibleLine.setDescription(StringUtils.truncate(gtfsEntity.getDesc(), 255));
        flexibleLine.setTransportMode(VehicleModeEnumeration.BUS);
        flexibleLine.setTransportSubmode(VehicleSubmodeEnumeration.DEMAND_AND_RESPONSE_BUS);
        flexibleLine.setFlexibleLineType(FlexibleLineTypeEnumeration.MIXED_FLEXIBLE);
        flexibleLine.setProvider(gtfsImportReferential.getProvider(gtfsEntity.getAgency().getId()));
        flexibleLine.setNetwork(gtfsImportReferential.getNetwork(gtfsEntity.getAgency().getId()));

        log.debug("gtfsEntity {}", gtfsEntity);
        log.debug("flexibleLine {}", flexibleLine);
    }

}
