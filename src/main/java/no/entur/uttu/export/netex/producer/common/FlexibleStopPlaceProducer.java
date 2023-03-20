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

import no.entur.uttu.export.netex.NetexExportContext;
import no.entur.uttu.export.netex.producer.NetexObjectFactory;
import no.entur.uttu.model.FlexibleArea;
import no.entur.uttu.model.FlexibleStopPlace;
import no.entur.uttu.model.HailAndRideArea;
import no.entur.uttu.stopplace.StopPlaceRegistry;
import org.locationtech.jts.geom.Polygon;
import org.rutebanken.netex.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class FlexibleStopPlaceProducer {

    @Autowired
    private NetexObjectFactory netexObjectFactory;


    private ObjectFactory objectFactory = new ObjectFactory();

    @Autowired
    private StopPlaceRegistry stopPlaceregistry;

    public List<org.rutebanken.netex.model.FlexibleStopPlace> produce(NetexExportContext context) {
        return context.flexibleStopPlaces.stream().map(localStopPlace -> mapFlexibleStopPlace(localStopPlace, context)).collect(Collectors.toList());
    }

    private org.rutebanken.netex.model.FlexibleStopPlace mapFlexibleStopPlace(FlexibleStopPlace localStopPlace, NetexExportContext context) {
        org.rutebanken.netex.model.FlexibleQuay_VersionStructure netexQuay;

        if (localStopPlace.getFlexibleArea() != null) {
            netexQuay = mapFlexibleArea(localStopPlace, context);
        } else {
            netexQuay = mapHailAndRideArea(localStopPlace, context);
        }

        org.rutebanken.netex.model.FlexibleStopPlace netexFlexibleStopPlace = new org.rutebanken.netex.model.FlexibleStopPlace()
                            .withId(localStopPlace.getNetexId())
                            .withVersion(localStopPlace.getNetexVersion())
                            .withName(netexObjectFactory.createMultilingualString(localStopPlace.getName()))
                            .withDescription(netexObjectFactory.createMultilingualString(localStopPlace.getDescription()))
                            .withTransportMode(netexObjectFactory.mapEnum(localStopPlace.getTransportMode(), AllVehicleModesOfTransportEnumeration.class))
                            .withPrivateCode(netexObjectFactory.createPrivateCodeStructure(localStopPlace.getPrivateCode()))
                            .withAreas(new FlexibleStopPlace_VersionStructure.Areas().withFlexibleAreaOrFlexibleAreaRefOrHailAndRideArea(netexQuay))
                            .withKeyList(netexObjectFactory.mapKeyValues(localStopPlace.getKeyValues()));

        //For fixedStopAreas, we need to write members in the area
        feedMembers(netexFlexibleStopPlace, localStopPlace);

        return netexFlexibleStopPlace;
    }

    private void feedMembers(org.rutebanken.netex.model.FlexibleStopPlace netexFlexibleStopPlace, FlexibleStopPlace originalStop) {
        Optional<String> areaTypeOpt = getStopAreaType(netexFlexibleStopPlace);

        if(areaTypeOpt.isEmpty() || !areaTypeOpt.get().equals("UnrestrictedPublicTransportAreas")){
            return;
        }

        Polygon polygon = originalStop.getFlexibleArea().getPolygon();
        List<no.entur.uttu.model.StopPlaceView> members = stopPlaceregistry.getMembersForArea(polygon);
        if (members.size() > 0){
            netexFlexibleStopPlace.withMembers(convertStopplacesToRefs(members));
        }


    }

    private PointRefs_RelStructure convertStopplacesToRefs(List<no.entur.uttu.model.StopPlaceView> stopPlaces){

        PointRefs_RelStructure pointRefs = new PointRefs_RelStructure();
        List<JAXBElement<? extends PointRefStructure>> pointRefList = new ArrayList<>();

        for (no.entur.uttu.model.StopPlaceView stopPlace : stopPlaces){
            PointRefStructure pointRef = new PointRefStructure();
            pointRef.withRef(stopPlace.getNetexId());
            pointRef.withVersionRef(String.valueOf(stopPlace.getVersion()));
            pointRefList.add(objectFactory.createPointRef(pointRef));
        }
        pointRefs.withPointRef(pointRefList);

        return pointRefs;
    }

    private Optional<String> getStopAreaType(org.rutebanken.netex.model.FlexibleStopPlace stopArea){

        for (KeyValueStructure keyValueStructure : stopArea.getKeyList().getKeyValue()) {
            if (keyValueStructure.getKey().equals("FlexibleStopAreaType")){
                return Optional.of(keyValueStructure.getValue());
            }
        }
        return Optional.empty();
    }


    private org.rutebanken.netex.model.FlexibleArea mapFlexibleArea(FlexibleStopPlace flexibleStopPlace, NetexExportContext context) {
        FlexibleArea localArea = flexibleStopPlace.getFlexibleArea();
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
