package no.entur.uttu.export.netex.producer;


import no.entur.uttu.config.ExportTimeZone;
import no.entur.uttu.export.model.AvailabilityPeriod;
import no.entur.uttu.export.netex.NetexExportContext;
import no.entur.uttu.model.Ref;
import no.entur.uttu.util.DateUtils;
import org.rutebanken.netex.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static no.entur.uttu.export.netex.producer.NetexIdProducer.getEntityName;

@Component(value = "netexObjectFactory")
public class NetexObjectFactory {

    private static final String VERSION_ONE = "1";
    private static final String DEFAULT_LANGUAGE = "no";

    @Value("${netex.export.version:1.08:NO-NeTEx-networktimetable:1.3}")
    private String netexVersion;


    private ObjectFactory objectFactory = new ObjectFactory();

    @Autowired
    private DateUtils dateUtils;

    @Autowired
    private ExportTimeZone exportTimeZone;

    public <E> JAXBElement<E> wrapAsJAXBElement(E entity) {
        if (entity == null) {
            return null;
        }
        return new JAXBElement(new QName("http://www.netex.org.uk/netex", getEntityName(entity)), entity.getClass(), null, entity);
    }

    public <N extends LinkSequence_VersionStructure, L extends no.entur.uttu.model.GroupOfEntities_VersionStructure> N populate(N netex, L local) {
        return (N) populateId(netex, local.getRef())
                           .withName(createMultilingualString(local.getName()))
                           .withPrivateCode(createPrivateCodeStructure(local.getPrivateCode()))
                           .withShortName(createMultilingualString(local.getShortName()))
                           .withDescription(createMultilingualString(local.getDescription()));
    }

    public <N extends EntityInVersionStructure> N populateId(N netex, Ref ref) {
        netex.setId(NetexIdProducer.getId(netex, ref));
        netex.setVersion(ref.version);
        return netex;
    }

    public <N extends VersionOfObjectRefStructure> N populateRefStructure(N netex, Ref ref, boolean withVersion) {
        netex.setRef(NetexIdProducer.getReference(netex, ref));
        if (withVersion) {
            netex.setVersion(ref.version);
        }
        return netex;
    }

    public <N extends VersionOfObjectRefStructure> JAXBElement<N> createRefStructure(N netex, Ref ref, boolean withVersion) {
        populateRefStructure(netex, ref, withVersion);
        return wrapAsJAXBElement(netex);
    }

    public JAXBElement<PublicationDeliveryStructure> createPublicationDelivery(NetexExportContext exportContext, CompositeFrame compositeFrame) {

        PublicationDeliveryStructure.DataObjects dataObjects = objectFactory.createPublicationDeliveryStructureDataObjects();
        dataObjects.getCompositeFrameOrCommonFrame().add(objectFactory.createCompositeFrame(compositeFrame));

        PublicationDeliveryStructure publicationDeliveryStructure = objectFactory.createPublicationDeliveryStructure()
                                                                            .withVersion(netexVersion)
                                                                            .withPublicationTimestamp(dateUtils.toExportLocalDateTime(exportContext.publicationTimestamp))
                                                                            .withParticipantRef(exportContext.provider.getName())
                                                                            .withDescription(createMultilingualString("Flexible lines"))
                                                                            .withDataObjects(dataObjects);
        return objectFactory.createPublicationDelivery(publicationDeliveryStructure);
    }


    public <F extends Common_VersionFrameStructure> CompositeFrame createCompositeFrame(NetexExportContext context, AvailabilityPeriod availabilityPeriod, F... frames) {
        ValidityConditions_RelStructure validityConditionsStruct = objectFactory.createValidityConditions_RelStructure()
                                                                           .withValidityConditionRefOrValidBetweenOrValidityCondition_(createAvailabilityCondition(availabilityPeriod, context));

        Codespace providerCodespace = createCodespace(context.provider.getCodespace());

        Codespaces_RelStructure codespaces = objectFactory.createCodespaces_RelStructure().withCodespaceRefOrCodespace(providerCodespace);

        LocaleStructure localeStructure = objectFactory.createLocaleStructure()
                                                  .withTimeZone(exportTimeZone.getDefaultTimeZoneId().getId())
                                                  .withDefaultLanguage(DEFAULT_LANGUAGE);

        VersionFrameDefaultsStructure versionFrameDefaultsStructure = objectFactory.createVersionFrameDefaultsStructure()
                                                                              .withDefaultLocale(localeStructure);

        Frames_RelStructure frames_relStructure = null;
        if (frames != null) {
            frames_relStructure = new Frames_RelStructure().withCommonFrame(Arrays.stream(frames).map(this::wrapAsJAXBElement).collect(Collectors.toList()));
        }
        String compositeFrameId = NetexIdProducer.generateId(CompositeFrame.class, context);

        CompositeFrame compositeFrame = objectFactory.createCompositeFrame()
                                                .withVersion(VERSION_ONE)
                                                .withCreated(dateUtils.toExportLocalDateTime(context.publicationTimestamp))
                                                .withId(compositeFrameId)
                                                .withValidityConditions(validityConditionsStruct)
                                                .withFrames(frames_relStructure)
                                                .withCodespaces(codespaces)
                                                .withFrameDefaults(versionFrameDefaultsStructure);


        return compositeFrame;
    }


    public ResourceFrame createResourceFrame(NetexExportContext context, Collection<Authority> authorities, Collection<Operator> operators) {
        String resourceFrameId = NetexIdProducer.generateId(ResourceFrame.class, context);
        OrganisationsInFrame_RelStructure organisationsStruct = objectFactory.createOrganisationsInFrame_RelStructure()
                                                                        .withOrganisation_(authorities.stream().map(this::wrapAsJAXBElement).collect(Collectors.toList()))
                                                                        .withOrganisation_(operators.stream().map(this::wrapAsJAXBElement).collect(Collectors.toList()));

        return objectFactory.createResourceFrame()
                       .withOrganisations(organisationsStruct)
                       .withVersion(VERSION_ONE)
                       .withId(resourceFrameId);
    }

    public SiteFrame createSiteFrame(NetexExportContext context, Collection<FlexibleStopPlace> flexibleStopPlaces) {
        String frameId = NetexIdProducer.generateId(SiteFrame.class, context);
        return objectFactory.createSiteFrame()
                       .withFlexibleStopPlaces(new FlexibleStopPlacesInFrame_RelStructure().withFlexibleStopPlace(flexibleStopPlaces))
                       .withVersion(VERSION_ONE)
                       .withId(frameId);
    }

    public ServiceFrame createCommonServiceFrame(NetexExportContext context, Collection<Network> networks, Collection<RoutePoint> routePoints,
                                                        Collection<ScheduledStopPoint> scheduledStopPoints, Collection<? extends StopAssignment_VersionStructure> stopAssignmentElements,
                                                        Collection<Notice> notices) {

        RoutePointsInFrame_RelStructure routePointStruct = objectFactory.createRoutePointsInFrame_RelStructure()
                                                                   .withRoutePoint(routePoints);

        ScheduledStopPointsInFrame_RelStructure scheduledStopPointsStruct = objectFactory.createScheduledStopPointsInFrame_RelStructure().withScheduledStopPoint(scheduledStopPoints);

        StopAssignmentsInFrame_RelStructure stopAssignmentsStruct = objectFactory.createStopAssignmentsInFrame_RelStructure()
                                                                            .withStopAssignment(stopAssignmentElements.stream().map(this::wrapAsJAXBElement).collect(Collectors.toList()));

        NoticesInFrame_RelStructure noticesInFrame_relStructure = null;
        if (!CollectionUtils.isEmpty(notices)) {
            noticesInFrame_relStructure = objectFactory.createNoticesInFrame_RelStructure().withNotice(notices);
        }

        NetworksInFrame_RelStructure additionalNetworks = null;
        Network network = null;
        if (!CollectionUtils.isEmpty(networks)) {
            Iterator<Network> networkIterator = networks.iterator();
            network = networkIterator.next();


            if (networkIterator.hasNext()) {
                additionalNetworks = new NetworksInFrame_RelStructure();
                while (networkIterator.hasNext()) {
                    additionalNetworks.getNetwork().add(networkIterator.next());
                }
            }
        }

        return createServiceFrame(context)
                       .withRoutePoints(routePointStruct)
                       .withScheduledStopPoints(scheduledStopPointsStruct)
                       .withStopAssignments(stopAssignmentsStruct)
                       .withNetwork(network)
                       .withAdditionalNetworks(additionalNetworks)
                       .withNotices(noticesInFrame_relStructure);

    }


    public <N extends Line_VersionStructure> ServiceFrame createLineServiceFrame(NetexExportContext context, N line, List<Route> routes,
                                                                                        Collection<JourneyPattern> journeyPatterns,
                                                                                        Collection<NoticeAssignment> noticeAssignments) {
        RoutesInFrame_RelStructure routesInFrame = objectFactory.createRoutesInFrame_RelStructure();
        for (Route route : routes) {
            JAXBElement<Route> routeElement = objectFactory.createRoute(route);
            routesInFrame.getRoute_().add(routeElement);
        }

        LinesInFrame_RelStructure linesInFrame = objectFactory.createLinesInFrame_RelStructure();
        linesInFrame.getLine_().add(wrapAsJAXBElement(line));

        JourneyPatternsInFrame_RelStructure journeyPatternsInFrame = objectFactory.createJourneyPatternsInFrame_RelStructure();
        for (JourneyPattern journeyPattern : journeyPatterns) {
            JAXBElement<JourneyPattern> journeyPatternElement = objectFactory.createJourneyPattern(journeyPattern);
            journeyPatternsInFrame.getJourneyPattern_OrJourneyPatternView().add(journeyPatternElement);
        }

        return createServiceFrame(context)
                       .withRoutes(routesInFrame)
                       .withLines(linesInFrame)
                       .withJourneyPatterns(journeyPatternsInFrame)
                       .withNoticeAssignments(wrapNoticeAssignments(noticeAssignments));
    }

    private ServiceFrame createServiceFrame(NetexExportContext context) {
        String serviceFrameId = NetexIdProducer.generateId(ServiceFrame.class, context);

        return objectFactory.createServiceFrame()
                       .withVersion(VERSION_ONE)
                       .withId(serviceFrameId);
    }


    public TimetableFrame createTimetableFrame(NetexExportContext context, Collection<ServiceJourney> serviceJourneys, Collection<NoticeAssignment> noticeAssignments) {
        JourneysInFrame_RelStructure journeysInFrameRelStructure = objectFactory.createJourneysInFrame_RelStructure();
        journeysInFrameRelStructure.getDatedServiceJourneyOrDeadRunOrServiceJourney().addAll(serviceJourneys);

        String timetableFrameId = NetexIdProducer.generateId(TimetableFrame.class, context);
        return objectFactory.createTimetableFrame()
                       .withVersion(VERSION_ONE)
                       .withId(timetableFrameId)
                       .withNoticeAssignments(wrapNoticeAssignments(noticeAssignments))
                       .withVehicleJourneys(journeysInFrameRelStructure);

    }

    private NoticeAssignmentsInFrame_RelStructure wrapNoticeAssignments(Collection<NoticeAssignment> noticeAssignments) {
        if (CollectionUtils.isEmpty(noticeAssignments)) {
            return null;
        }
        return new NoticeAssignmentsInFrame_RelStructure().withNoticeAssignment_(noticeAssignments.stream().map(this::wrapAsJAXBElement).collect(Collectors.toList()));
    }

    public ServiceCalendarFrame createServiceCalendarFrame(NetexExportContext context, Collection<DayType> dayTypes, Collection<DayTypeAssignment> dayTypeAssignments,
                                                                  Collection<OperatingPeriod> operatingPeriods) {
        String frameId = NetexIdProducer.generateId(ServiceCalendarFrame.class, context);

        DayTypesInFrame_RelStructure dayTypesStruct = null;
        if (dayTypes != null) {
            dayTypesStruct = new DayTypesInFrame_RelStructure().withDayType_(dayTypes.stream().map(this::wrapAsJAXBElement).collect(Collectors.toList()));
        }

        DayTypeAssignmentsInFrame_RelStructure dayTypeAssignmentsInFrameRelStructure = null;
        if (dayTypeAssignments != null) {
            dayTypeAssignmentsInFrameRelStructure = new DayTypeAssignmentsInFrame_RelStructure().withDayTypeAssignment(dayTypeAssignments);
            dayTypeAssignmentsInFrameRelStructure.getDayTypeAssignment().sort(Comparator.comparing(DayTypeAssignment::getOrder));
        }

        OperatingPeriodsInFrame_RelStructure operatingPeriodsInFrameRelStructure = null;
        if (!CollectionUtils.isEmpty(operatingPeriods)) {
            operatingPeriodsInFrameRelStructure = new OperatingPeriodsInFrame_RelStructure();
            operatingPeriodsInFrameRelStructure.getOperatingPeriodOrUicOperatingPeriod().addAll(operatingPeriods);
            operatingPeriodsInFrameRelStructure.getOperatingPeriodOrUicOperatingPeriod().sort(Comparator.comparing(OperatingPeriod_VersionStructure::getFromDate));
        }

        return objectFactory.createServiceCalendarFrame()
                       .withVersion(VERSION_ONE)
                       .withId(frameId)
                       .withDayTypes(dayTypesStruct)
                       .withDayTypeAssignments(dayTypeAssignmentsInFrameRelStructure)
                       .withOperatingPeriods(operatingPeriodsInFrameRelStructure);

    }

    public JAXBElement<AvailabilityCondition> createAvailabilityCondition(AvailabilityPeriod availabilityPeriod, NetexExportContext context) {
        String availabilityConditionId = NetexIdProducer.generateId(AvailabilityCondition.class, context);

        AvailabilityCondition availabilityCondition = objectFactory.createAvailabilityCondition()
                                                              .withVersion(VERSION_ONE)
                                                              .withId(availabilityConditionId)
                                                              .withFromDate(availabilityPeriod.getFrom().atStartOfDay())
                                                              .withToDate(availabilityPeriod.getTo().atStartOfDay());

        return objectFactory.createAvailabilityCondition(availabilityCondition);
    }

    public <N extends Enum<N>, L extends Enum> List<N> mapEnums(Collection<L> local, Class<N> netexEnumClass) {
        if (local == null) {
            return null;
        }
        return local.stream().map(localEnum -> mapEnum(localEnum, netexEnumClass)).collect(Collectors.toList());
    }

    public <N extends Enum<N>, L extends Enum> N mapEnum(L local, Class<N> netexEnumClass) {
        if (local == null) {
            return null;
        }
        return Enum.valueOf(netexEnumClass, local.name());
    }

    public Codespace createCodespace(no.entur.uttu.model.Codespace local) {
        return objectFactory.createCodespace()
                       .withId(local.getXmlns().toLowerCase())
                       .withXmlns(local.getXmlns())
                       .withXmlnsUrl(local.getXmlnsUrl());
    }


    public MultilingualString createMultilingualString(String value) {
        if (value == null) {
            return null;
        }
        return objectFactory.createMultilingualString().withValue(value);
    }

    public PrivateCodeStructure createPrivateCodeStructure(String value) {
        if (value == null) {
            return null;
        }
        return objectFactory.createPrivateCodeStructure().withValue(value);
    }

    public OperatorRefStructure createOperatorRefStructure(String operatorId, boolean withRefValidation) {
        OperatorRefStructure operatorRefStruct = objectFactory.createOperatorRefStructure()
                                                         .withRef(operatorId);
        return withRefValidation ? operatorRefStruct.withVersion(VERSION_ONE) : operatorRefStruct;
    }

    public GroupOfLinesRefStructure createGroupOfLinesRefStructure(String groupOfLinesId) {
        return objectFactory.createGroupOfLinesRefStructure().withRef(groupOfLinesId);
    }

    public DestinationDisplayRefStructure createDestinationDisplayRefStructure(String destinationDisplayId) {
        return objectFactory.createDestinationDisplayRefStructure()
                       .withVersion(VERSION_ONE)
                       .withRef(destinationDisplayId);
    }


}