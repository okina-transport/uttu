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
import no.entur.uttu.export.netex.NetexFile;
import no.entur.uttu.export.netex.producer.NetexIdProducer;
import no.entur.uttu.export.netex.producer.NetexObjectFactory;
import no.entur.uttu.model.Ref;
import no.entur.uttu.stopplace.StopPlaceRegistry;
import no.entur.uttu.util.ExportUtil;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.rutebanken.netex.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBElement;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
public class NetexCommonFileProducer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private NetexObjectFactory objectFactory;

    @Autowired
    private OrganisationProducer organisationProducer;

    @Autowired
    private FlexibleStopPlaceProducer flexibleStopPlaceProducer;

    @Autowired
    private ServiceCalendarFrameProducer serviceCalendarFrameProducer;

    @Autowired
    private NetworkProducer networkProducer;

    @Autowired
    private StopPlaceRegistry stopPlaceRegistry;

    @Value("${export.blob.commonFileFilenameSuffix:_flexible_shared_data}")
    private String commonFileFilenameSuffix;


    public NetexFile toCommonFile(NetexExportContext context) {
        ResourceFrame resourceFrame = createResourceFrame(context);
        SiteFrame siteFrame = createSiteFrame(context);
        ServiceFrame serviceFrame = createServiceFrame(context);
        ServiceCalendarFrame serviceCalendarFrame = serviceCalendarFrameProducer.produce(context);
        CompositeFrame compositeFrame = objectFactory.createCompositeFrame(context, context.getAvailabilityPeriod(), resourceFrame, siteFrame, serviceFrame, serviceCalendarFrame);

        JAXBElement<PublicationDeliveryStructure> publicationDelivery = objectFactory.createPublicationDelivery(context, compositeFrame);

        String fileName = ExportUtil.createCommonFileFilename(context.provider, commonFileFilenameSuffix);

        return new NetexFile(fileName, publicationDelivery);
    }

    private ResourceFrame createResourceFrame(NetexExportContext context) {
        List<Operator> netexOperators = organisationProducer.produceOperators(context);
        List<Authority> netexAuthorities = organisationProducer.produceAuthorities(context);
        removeDuplicateOperators(netexAuthorities, netexOperators);
        return objectFactory.createResourceFrame(context, netexAuthorities, netexOperators);
    }

    /**
     * Remove operators if there are already listed in authorities. Uses id to execute checks
     *
     * @param netexAuthorities list of authorities
     * @param netexOperators   list of operators to be cleaned
     */
    private void removeDuplicateOperators(List<Authority> netexAuthorities, List<Operator> netexOperators) {
        List<Operator> cleanedList = new ArrayList<>();
        if (netexAuthorities == null || netexAuthorities.isEmpty()) {
            return;
        }
        List<String> authoritiesIdList = netexAuthorities.stream()
                .map(Authority::getId)
                .collect(Collectors.toList());

        for (Operator netexOperator : netexOperators) {
            if (!authoritiesIdList.contains(netexOperator.getId())) {
                cleanedList.add(netexOperator);
            }
        }

        netexOperators.clear();
        netexOperators.addAll(cleanedList);
    }

    private SiteFrame createSiteFrame(NetexExportContext context) {
        List<FlexibleStopPlace> netexFlexibleStopPlaces = flexibleStopPlaceProducer.produce(context);
        return objectFactory.createSiteFrame(context, netexFlexibleStopPlaces);
    }

    private ServiceFrame createServiceFrame(NetexExportContext context) {
        List<Network> networks = networkProducer.produce(context);
        List<RoutePoint> routePoints = buildRoutePoints(context);
        List<ScheduledStopPoint> scheduledStopPoints = buildScheduledStopPoints(context);


        List<StopAssignment_VersionStructure> stopAssignments = context.flexibleStopPlaces.stream().map(no.entur.uttu.model.FlexibleStopPlace::getRef)
                .map(this::buildFlexibleStopAssignment).collect(Collectors.toList());

        AtomicInteger passengerStopAssignmentOrder = new AtomicInteger(1);

        stopAssignments.addAll(context.quayRefs.stream().map(quayRef -> mapPassengerStopAssignment(quayRef, passengerStopAssignmentOrder.getAndIncrement(), context)).collect(Collectors.toList()));

        List<Notice> notices = context.notices.stream().map(this::mapNotice).collect(Collectors.toList());
        List<DestinationDisplay> destinationDisplays = context.destinationDisplays.stream().map(this::mapDestinationDisplay).collect(Collectors.toList());


        return objectFactory.createCommonServiceFrame(context, networks, routePoints, scheduledStopPoints, stopAssignments, notices, destinationDisplays);
    }

    private List<ScheduledStopPoint> buildScheduledStopPoints(NetexExportContext context) {

        List<ScheduledStopPoint> scheduledStopPoints = new ArrayList<>();

        for (Ref scheduledStopPointRef : context.scheduledStopPointRefs) {
            ScheduledStopPoint scheduledStopPoint = buildScheduledStopPoint(scheduledStopPointRef);
            addLocationToPoint(scheduledStopPoint, context);
            scheduledStopPoints.add(scheduledStopPoint);
        }
        return scheduledStopPoints;
    }


    private List<RoutePoint> buildRoutePoints(NetexExportContext context) {

        List<RoutePoint> routePoints = new ArrayList<>();

        for (Ref routePointRef : context.routePointRefs) {
            RoutePoint routePoint = buildRoutePoint(routePointRef);
            addLocationToPoint(routePoint, context);
            routePoints.add(routePoint);
        }

        return routePoints;
    }

    private void addLocationToPoint(Point_VersionStructure point, NetexExportContext context) {
        LocationStructure location = new LocationStructure();
        String idToRecover = point.getId()
                                .replace("RoutePoint", "FlexibleStopPlace")
                                .replace("ScheduledStopPoint", "FlexibleStopPlace");

        Optional<no.entur.uttu.model.FlexibleStopPlace> flexibleStopOpt = getStopPlaceWithId(idToRecover, context);

        if (point.getId().endsWith("_UTTU")) {
            addLocationFromQuay(point, context);
            return;
        } else if (flexibleStopOpt.isEmpty()) {
            logger.error("FlexibleStopPlace not found for route:" + point.getId());
            return;
        }

        no.entur.uttu.model.FlexibleStopPlace flexibleStop = flexibleStopOpt.get();
        Optional<Point> centroidOpt = getCentroidFromFlexibleStopPlace(flexibleStop);

        centroidOpt.ifPresent(centroid -> {
            location.setLatitude(BigDecimal.valueOf(centroid.getY()));
            location.setLongitude(BigDecimal.valueOf(centroid.getX()));
        });

        point.setLocation(location);
    }

    private void addLocationFromQuay(Point_VersionStructure point, NetexExportContext context) {

        //can be a routePoint of a scheduledStopPoint. replacing both possibilities
        String quayId = point.getId().replace("RoutePoint", "Quay")
                .replace("ScheduledStopPoint", "Quay")
                .replace(context.provider.getCodespace().getXmlns(), "MOBIITI")
                .replace("_UTTU", "");


        Optional<String> quayOpt = context.quayRefs.stream()
                .filter(quay -> quay.equals(quayId)).findFirst();

        if (quayOpt.isEmpty()){
            logger.error("Unable to found quay:" + quayId);
            return;
        }

        Optional<StopPlace> stopPlaceOpt = stopPlaceRegistry.getStopPlaceByQuayRef(quayOpt.get());

        if (stopPlaceOpt.isEmpty()){
            logger.error("Unable to found stopplace for quay id:" + quayOpt.get());
            return;
        }

        StopPlace foundStopPlace = stopPlaceOpt.get();
        Optional<LocationStructure> locationOpt = getLocationFromQuay(foundStopPlace, quayId);
        locationOpt.ifPresent(point::setLocation);
    }

    private Optional<LocationStructure> getLocationFromQuay(StopPlace foundStopPlace, String quayId){
        LocationStructure location = new LocationStructure();
        for (JAXBElement<?> jaxbElement : foundStopPlace.getQuays().getQuayRefOrQuay()) {
            Quay quay =  (Quay) jaxbElement.getValue();
            if (quay.getId().equals(quayId)){
                SimplePoint_VersionStructure centroid = quay.getCentroid();
                location.setLatitude(centroid.getLocation().getLatitude());
                location.setLongitude(centroid.getLocation().getLongitude());
                return Optional.of(location);
            }
        }
        return Optional.empty();
    }

    private Optional<Point> getCentroidFromFlexibleStopPlace(no.entur.uttu.model.FlexibleStopPlace flexibleStop) {
        no.entur.uttu.model.FlexibleArea area = flexibleStop.getFlexibleArea();
        if (area == null) {
            logger.error("null area for flexible stop place:" + flexibleStop.getNetexId());
            return Optional.empty();
        }

        Polygon polygon = area.getPolygon();
        if (polygon == null) {
            logger.error("null polygon for flexible stop place:" + flexibleStop.getNetexId());
            return Optional.empty();
        }

        return Optional.of(polygon.getCentroid());
    }

    private Optional<no.entur.uttu.model.FlexibleStopPlace> getStopPlaceWithId(String idToRecover, NetexExportContext context) {
        for (no.entur.uttu.model.FlexibleStopPlace flexibleStopPlace : context.flexibleStopPlaces) {
            if (flexibleStopPlace.getNetexId() != null && flexibleStopPlace.getNetexId().equals(idToRecover)) {
                return Optional.of(flexibleStopPlace);
            }
        }
        return Optional.empty();
    }

    private RoutePoint buildRoutePoint(Ref ref) {
        Ref scheduledStopPointRef = NetexIdProducer.replaceEntityName(ref, ScheduledStopPoint.class.getSimpleName());
        PointRefStructure pointRefStructure = new PointRefStructure().withRef(scheduledStopPointRef.id).withVersion(scheduledStopPointRef.version);
        PointProjection pointProjection = objectFactory.populateId(new PointProjection(), ref).withProjectToPointRef(pointRefStructure);
        Projections_RelStructure projections_relStructure = new Projections_RelStructure().withProjectionRefOrProjection(objectFactory.wrapAsJAXBElement(pointProjection));
        return objectFactory.populateId(new RoutePoint(), ref)
                .withProjections(projections_relStructure);
    }

    private ScheduledStopPoint buildScheduledStopPoint(Ref ref) {
        return objectFactory.populateId(new ScheduledStopPoint(), ref);
    }

    private FlexibleStopAssignment buildFlexibleStopAssignment(Ref ref) {
        return objectFactory.populateId(new FlexibleStopAssignment(), ref)
                .withScheduledStopPointRef(objectFactory.wrapRefStructure(new ScheduledStopPointRefStructure(), ref, true))
                .withFlexibleStopPlaceRef(objectFactory.populateRefStructure(new FlexibleStopPlaceRefStructure(), ref, true));
    }

    public Notice mapNotice(no.entur.uttu.model.Notice local) {
        return objectFactory.populateId(new Notice(), local.getRef()).withText(objectFactory.createMultilingualString(local.getText()));
    }

    public DestinationDisplay mapDestinationDisplay(no.entur.uttu.model.DestinationDisplay local) {
        return objectFactory.populateId(new DestinationDisplay(), local.getRef()).withFrontText(objectFactory.createMultilingualString(local.getFrontText()));
    }


    public PassengerStopAssignment mapPassengerStopAssignment(String quayRef, int order, NetexExportContext context) {
        Ref scheduledStopPointRef = objectFactory.createScheduledStopPointRefFromQuayRef(quayRef, context);
        return objectFactory.populateId(new PassengerStopAssignment(), scheduledStopPointRef).withOrder(BigInteger.valueOf(order))
                .withScheduledStopPointRef(objectFactory.wrapRefStructure(new ScheduledStopPointRefStructure(), scheduledStopPointRef, true))
                .withQuayRef(objectFactory.wrapAsJAXBElement(new QuayRefStructure().withRef(quayRef)));

    }
}
