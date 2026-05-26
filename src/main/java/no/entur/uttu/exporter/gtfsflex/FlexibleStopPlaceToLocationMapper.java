package no.entur.uttu.exporter.gtfsflex;

import no.entur.uttu.model.FlexibleArea;
import no.entur.uttu.model.FlexibleStopPlace;
import org.apache.commons.collections4.CollectionUtils;
import org.geojson.GeoJsonObject;
import org.geojson.LngLatAlt;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Polygon;
import org.onebusaway.gtfs.model.AgencyAndId;
import org.onebusaway.gtfs.model.Location;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FlexibleStopPlaceToLocationMapper {

    public static Location map(FlexibleStopPlace flexibleStopPlace, int id) {
        Location newLocation = new Location();
        newLocation.setDesc(flexibleStopPlace.getDescription());
        newLocation.setName(flexibleStopPlace.getName());
        AgencyAndId newAgencyAndId = new AgencyAndId();
        newAgencyAndId.setId(flexibleStopPlace.getOriginalId());
        newAgencyAndId.setAgencyId(String.valueOf(id));
        newLocation.setId(newAgencyAndId);

        if (CollectionUtils.isNotEmpty(flexibleStopPlace.getFlexibleAreas())) {
            FlexibleArea area = flexibleStopPlace.getFlexibleAreas().getFirst();
            newLocation.setGeometry(convertPolygonToGeoJson(area.getPolygon()));
        }
        return newLocation;

    }

    public static GeoJsonObject convertPolygonToGeoJson(Polygon polygon) {
        org.geojson.Polygon geoJsonPolygon = new org.geojson.Polygon();

        List<LngLatAlt> exteriorRing = coordinatesToLngLatAlt(polygon.getExteriorRing().getCoordinates());
        geoJsonPolygon.setExteriorRing(exteriorRing);


        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            List<LngLatAlt> interiorRing = coordinatesToLngLatAlt(polygon.getInteriorRingN(i).getCoordinates());
            geoJsonPolygon.addInteriorRing(interiorRing);
        }

        return geoJsonPolygon;
    }

    private static List<LngLatAlt> coordinatesToLngLatAlt(Coordinate[] coordinates) {
        return Arrays.stream(coordinates)
                .map(c -> new LngLatAlt(c.x, c.y))
                .collect(Collectors.toList());
    }
}
