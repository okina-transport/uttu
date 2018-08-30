package no.entur.uttu.model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        uniqueConstraints = {
                                    @UniqueConstraint(name = "journey_pattern_unique_name_constraint", columnNames = {"provider_pk", "name"})}
)
public class JourneyPattern extends GroupOfEntities_VersionStructure {

    @NotNull
    @ManyToOne
    private FlexibleLine flexibleLine;

    @OneToMany(mappedBy = "journeyPattern", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotNull
    private final List<ServiceJourney> serviceJourneys = new ArrayList<>();

    @OneToMany(mappedBy = "journeyPattern", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotNull
    private final List<StopPointInJourneyPattern> pointsInSequence = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private DirectionTypeEnumeration directionType;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Notice> notices;

    public FlexibleLine getFlexibleLine() {
        return flexibleLine;
    }

    public void setFlexibleLine(FlexibleLine flexibleLine) {
        this.flexibleLine = flexibleLine;
    }

    public List<ServiceJourney> getServiceJourneys() {
        return serviceJourneys;
    }

    public DirectionTypeEnumeration getDirectionType() {
        return directionType;
    }

    public void setDirectionType(DirectionTypeEnumeration directionType) {
        this.directionType = directionType;
    }

    public List<Notice> getNotices() {
        return notices;
    }

    public void setNotices(List<Notice> notices) {
        this.notices = notices;
    }

    public void setServiceJourneys(List<ServiceJourney> serviceJourneys) {
        this.serviceJourneys.clear();
        if (serviceJourneys != null) {
            serviceJourneys.stream().forEach(sj -> sj.setJourneyPattern(this));
            this.serviceJourneys.addAll(serviceJourneys);
        }
    }

    public List<StopPointInJourneyPattern> getPointsInSequence() {
        return pointsInSequence;
    }

    public void setPointsInSequence(List<StopPointInJourneyPattern> pointsInSequence) {
        this.pointsInSequence.clear();
        if (pointsInSequence != null) {
            pointsInSequence.stream().forEach(spinjp -> spinjp.setJourneyPattern(this));
            this.pointsInSequence.addAll(pointsInSequence);
        }
    }
}
