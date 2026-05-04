package no.entur.uttu.importer.gtfsflex.mapper;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.Referential;
import no.entur.uttu.model.FlexibleArea;
import no.entur.uttu.model.FlexibleStopPlace;
import no.entur.uttu.model.VehicleModeEnumeration;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Polygon;
import org.onebusaway.gtfs.model.Location;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class LocationMapper implements Mapper<Location> {

    private final GeometryFactory geometryFactory;

    public LocationMapper(GeometryFactory geometryFactory) {
        this.geometryFactory = geometryFactory;
    }

    @Override
    public void map(Location gtfsEntity, Referential gtfsImportReferential) {
        log.info("Mapping location {}", gtfsEntity.getId().getId());
        FlexibleStopPlace flexibleStopPlace = gtfsImportReferential.getFlexibleStopPlace(gtfsEntity.getId().getId());

        flexibleStopPlace.setName(gtfsEntity.getName());
        flexibleStopPlace.setDescription(gtfsEntity.getDesc());
        flexibleStopPlace.setTransportMode(VehicleModeEnumeration.BUS);
        flexibleStopPlace.setProvider(gtfsImportReferential.getProvider(gtfsEntity.getId().getAgencyId()));

        List<FlexibleArea> flexibleAreas = gtfsLocationToFlexibleAreaAreas(gtfsEntity, gtfsImportReferential, flexibleStopPlace);
        flexibleStopPlace.setFlexibleAreas(flexibleAreas);

        log.debug("gtfsEntity {}", gtfsEntity);
        log.debug("flexibleStopPlace {}", flexibleStopPlace);
    }

    private Polygon polygonCoordinatesToFlexibleArea(java.util.List<org.geojson.LngLatAlt> exteriorRing) {
        Coordinate[] coordinates =
                exteriorRing.stream().map(
                        lngLatAlt -> new Coordinate(lngLatAlt.getLongitude(),
                                lngLatAlt.getLatitude())).toArray(Coordinate[]::new);
        return geometryFactory.createPolygon(coordinates);
    }

    private List<FlexibleArea> gtfsLocationToFlexibleAreaAreas(Location gtfsEntity, Referential gtfsImportReferential, FlexibleStopPlace flexibleStopPlace) {
        List<FlexibleArea> flexibleAreas = new ArrayList<>();
        if (gtfsEntity.getGeometry() instanceof org.geojson.Polygon geoJsonPolygon) {
            FlexibleArea flexibleArea = gtfsImportReferential.getFlexibleArea(gtfsEntity.getId().getId());
            flexibleArea.setPolygon(polygonCoordinatesToFlexibleArea(geoJsonPolygon.getExteriorRing()));
            flexibleArea.setFlexibleStopPlace(flexibleStopPlace);
            flexibleAreas.add(flexibleArea);
        } else if (gtfsEntity.getGeometry() instanceof org.geojson.MultiPolygon multiPolygon) {
            for (int i = 0; i < multiPolygon.getCoordinates().getFirst().size(); i++) {
                FlexibleArea flexibleArea = gtfsImportReferential.getFlexibleArea(gtfsEntity.getId().getId(), i);
                flexibleArea.setPolygon(polygonCoordinatesToFlexibleArea(multiPolygon.getCoordinates().getFirst().get(i)));
                flexibleArea.setFlexibleStopPlace(flexibleStopPlace);
                flexibleAreas.add(flexibleArea);
            }
        }
        return flexibleAreas;
    }

}
