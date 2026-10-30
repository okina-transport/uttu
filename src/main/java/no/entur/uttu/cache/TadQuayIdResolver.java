package no.entur.uttu.cache;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.model.QuayView;
import no.entur.uttu.stopplace.StopPlaceRegistry;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Resolves the Mobi-iti quay ids of stops imported from GTFS flex (registered in Tiamat as TAD quays).
 * <p>
 * The {@link TadQuayIdCache} is reloaded from Tiamat's mapping file, which may not yet contain recently created quays:
 * missing ids are asked to Tiamat and added to the cache.
 */
@Component
@Slf4j
public class TadQuayIdResolver {

    private final TadQuayIdCache tadQuayIdCache;
    private final StopPlaceRegistry stopPlaceRegistry;

    public TadQuayIdResolver(TadQuayIdCache tadQuayIdCache, StopPlaceRegistry stopPlaceRegistry) {
        this.tadQuayIdCache = tadQuayIdCache;
        this.stopPlaceRegistry = stopPlaceRegistry;
    }

    /**
     * @param provider          provider code, sent to Tiamat
     * @param producerNetexIds  netex ids of imported stops (e.g. PROVIDER:Quay:stopId)
     * @return Mobi-iti quay id by producer netex id; ids unknown to Tiamat are absent from the result
     */
    public Map<String, String> resolve(String provider, Collection<String> producerNetexIds) {
        Map<String, String> resolved = new HashMap<>();
        Set<String> missing = new HashSet<>();
        for (String producerNetexId : producerNetexIds) {
            Optional<String> mobiItiNetexId = tadQuayIdCache.getMobiItiNetexId(producerNetexId);
            if (mobiItiNetexId.isPresent()) {
                resolved.put(producerNetexId, mobiItiNetexId.get());
            } else {
                missing.add(producerNetexId);
            }
        }

        if (missing.isEmpty()) {
            return resolved;
        }

        log.info("{} quay id(s) missing from TAD quay cache, asking Tiamat", missing.size());
        for (QuayView quayView : stopPlaceRegistry.getQuayListFromRegistry(provider, missing)) {
            if (quayView.getImportedId() != null && quayView.getNetexId() != null && missing.contains(quayView.getImportedId())) {
                tadQuayIdCache.put(quayView.getImportedId(), quayView.getNetexId());
                resolved.put(quayView.getImportedId(), quayView.getNetexId());
            }
        }
        return resolved;
    }
}
