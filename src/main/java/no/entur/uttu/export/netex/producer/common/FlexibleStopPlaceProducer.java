/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.entur.uttu.export.netex.producer.common;

import jakarta.xml.bind.JAXBElement;
import no.entur.uttu.export.netex.NetexExportContext;
import no.entur.uttu.export.netex.producer.NetexObjectFactory;
import no.entur.uttu.model.FlexibleArea;
import no.entur.uttu.model.FlexibleStopPlace;
import no.entur.uttu.model.HailAndRideArea;
import no.entur.uttu.model.StopPlaceView;
import no.entur.uttu.stopplace.StopPlaceRegistry;
import org.apache.commons.collections4.CollectionUtils;
import org.locationtech.jts.geom.Polygon;
import org.rutebanken.netex.model.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FlexibleStopPlaceProducer {

    private final ObjectFactory objectFactory = new ObjectFactory();
    private final NetexObjectFactory netexObjectFactory;
    private final StopPlaceRegistry stopPlaceregistry;

    public FlexibleStopPlaceProducer(NetexObjectFactory netexObjectFactory, StopPlaceRegistry stopPlaceregistry) {
        this.netexObjectFactory = netexObjectFactory;
        this.stopPlaceregistry = stopPlaceregistry;
    }

    public List<org.rutebanken.netex.model.FlexibleStopPlace> produce(NetexExportContext context) {
        return context.flexibleStopPlaces.stream().map(localStopPlace -> mapFlexibleStopPlace(localStopPlace, context)).collect(Collectors.toList());
    }

    private org.rutebanken.netex.model.FlexibleStopPlace mapFlexibleStopPlace(FlexibleStopPlace localStopPlace, NetexExportContext context) {
        FlexibleStopPlace_VersionStructure.Areas areas = new FlexibleStopPlace_VersionStructure.Areas();
        if (CollectionUtils.isNotEmpty(localStopPlace.getFlexibleAreas())) {
            for (FlexibleArea flexibleArea : localStopPlace.getFlexibleAreas()) {
                areas.withFlexibleAreaOrFlexibleAreaRefOrHailAndRideArea(mapFlexibleArea(flexibleArea, localStopPlace, context));
            }
        } else {
            areas.withFlexibleAreaOrFlexibleAreaRefOrHailAndRideArea(mapHailAndRideArea(localStopPlace, context));
        }

        org.rutebanken.netex.model.FlexibleStopPlace netexFlexibleStopPlace = new org.rutebanken.netex.model.FlexibleStopPlace()
                .withId(localStopPlace.getNetexId())
                .withVersion(localStopPlace.getNetexVersion())
                .withName(netexObjectFactory.createMultilingualString(localStopPlace.getName()))
                .withDescription(netexObjectFactory.createMultilingualString(localStopPlace.getDescription()))
                .withTransportMode(netexObjectFactory.mapEnum(localStopPlace.getTransportMode(), AllVehicleModesOfTransportEnumeration.class))
                .withPrivateCode(netexObjectFactory.createPrivateCodeStructure(localStopPlace.getPrivateCode()))
                .withAreas(areas)
                .withKeyList(netexObjectFactory.mapKeyValues(localStopPlace.getKeyValues()));

        //For fixedStopAreas, we need to write members in the area
        feedMembers(netexFlexibleStopPlace, localStopPlace);

        return netexFlexibleStopPlace;
    }

    private void feedMembers(org.rutebanken.netex.model.FlexibleStopPlace netexFlexibleStopPlace, FlexibleStopPlace originalStop) {
        Optional<String> areaTypeOpt = getStopAreaType(netexFlexibleStopPlace);

        if (areaTypeOpt.isEmpty() || !areaTypeOpt.get().equals("UnrestrictedPublicTransportAreas")) {
            return;
        }

        List<no.entur.uttu.model.StopPlaceView> members = new ArrayList<>();

        for (FlexibleArea flexibleArea : originalStop.getFlexibleAreas()) {
            Polygon polygon = flexibleArea.getPolygon();
            List<StopPlaceView> tmpMembers = stopPlaceregistry.getMembersForArea(polygon);
            if (CollectionUtils.isNotEmpty(tmpMembers)) {
                members.addAll(tmpMembers);
            }
        }

        if (CollectionUtils.isNotEmpty(members)) {
            netexFlexibleStopPlace.withMembers(convertStopplacesToRefs(members));
        }


    }

    private PointRefs_RelStructure convertStopplacesToRefs(List<no.entur.uttu.model.StopPlaceView> stopPlaces) {

        PointRefs_RelStructure pointRefs = new PointRefs_RelStructure();
        List<JAXBElement<? extends PointRefStructure>> pointRefList = new ArrayList<>();

        for (no.entur.uttu.model.StopPlaceView stopPlace : stopPlaces) {
            PointRefStructure pointRef = new PointRefStructure();
            pointRef.withRef(stopPlace.getNetexId());
            pointRef.withVersionRef(String.valueOf(stopPlace.getVersion()));
            pointRefList.add(objectFactory.createPointRef(pointRef));
        }
        pointRefs.withPointRef(pointRefList);

        return pointRefs;
    }

    private Optional<String> getStopAreaType(org.rutebanken.netex.model.FlexibleStopPlace stopArea) {

        for (KeyValueStructure keyValueStructure : stopArea.getKeyList().getKeyValue()) {
            if (keyValueStructure.getKey().equals("FlexibleStopAreaType")) {
                return Optional.of(keyValueStructure.getValue());
            }
        }
        return Optional.empty();
    }


    private org.rutebanken.netex.model.FlexibleArea mapFlexibleArea(FlexibleArea localArea, FlexibleStopPlace flexibleStopPlace, NetexExportContext context) {
        return netexObjectFactory.populateId(new org.rutebanken.netex.model.FlexibleArea(), flexibleStopPlace.getRef())
                .withPolygon(NetexGeoUtil.toNetexPolygon(localArea.getPolygon(), context));
    }

    private org.rutebanken.netex.model.HailAndRideArea mapHailAndRideArea(FlexibleStopPlace flexibleStopPlace, NetexExportContext context) {
        HailAndRideArea localArea = flexibleStopPlace.getHailAndRideArea();

        PointRefStructure startPoint = netexObjectFactory.populateRefStructure(new ScheduledStopPointRefStructure(), netexObjectFactory.createScheduledStopPointRefFromQuayRef(localArea.getStartQuayRef(), context), true);
        PointRefStructure endPoint = netexObjectFactory.populateRefStructure(new ScheduledStopPointRefStructure(), netexObjectFactory.createScheduledStopPointRefFromQuayRef(localArea.getEndQuayRef(), context), true);

        return netexObjectFactory.populateId(new org.rutebanken.netex.model.HailAndRideArea(), flexibleStopPlace.getRef())
                .withStartPointRef(startPoint)
                .withEndPointRef(endPoint);
    }
}
