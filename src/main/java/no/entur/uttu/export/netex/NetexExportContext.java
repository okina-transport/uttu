package no.entur.uttu.export.netex;


import no.entur.uttu.export.model.AvailabilityPeriod;
import no.entur.uttu.export.model.ExportError;
import no.entur.uttu.model.FlexibleStopPlace;
import no.entur.uttu.model.Network;
import no.entur.uttu.model.Provider;
import no.entur.uttu.model.Ref;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

public class NetexExportContext {

    private AvailabilityPeriod availabilityPeriod;

    public Provider provider;

    public Instant publicationTimestamp;

    public Set<Network> networks = new HashSet<>();

    public Set<FlexibleStopPlace> flexibleStopPlaces = new HashSet<>();

    public Set<Ref> routePointRefs =new HashSet<>();

    public Set<String> operatorRefs = new HashSet<>();

    public Set<ExportError> errors = new HashSet<>();


    private Map<String, AtomicLong> idSequences = new HashMap<>();

    public NetexExportContext(Provider provider) {
        this.provider = provider;
        this.publicationTimestamp = Instant.now();

    }

    public void updateAvailabilityPeriod(AvailabilityPeriod newPeriod) {
        availabilityPeriod = newPeriod.union(availabilityPeriod);
    }

    public AvailabilityPeriod getAvailabilityPeriod() {
        return availabilityPeriod;
    }

    // TODO not thread safe.. problem?
    public long getAndIncrementIdSequence(String entityName) {
        AtomicLong sequence = idSequences.get(entityName);
        if (sequence == null) {
            sequence = new AtomicLong();
            idSequences.put(entityName, sequence);
        }
        return sequence.getAndIncrement();
    }
}
