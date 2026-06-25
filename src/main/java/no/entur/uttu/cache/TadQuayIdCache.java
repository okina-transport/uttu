package no.entur.uttu.cache;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class TadQuayIdCache {

    private static final Object LOCK = new Object();

    private final ConcurrentHashMap<String, String> quayMappings = new ConcurrentHashMap<>();
    private final QuayMappingFetcher quayMappingFetcher;

    @Value("${tiamat.storage.path}")
    private Path tiamatPath;

    @Value("${uttu.tad.quay.cache.refresh.frequency.min:60}")
    private int refreshFrequencyMin;

    private final ScheduledExecutorService executor =
            Executors.newSingleThreadScheduledExecutor();

    public TadQuayIdCache(QuayMappingFetcher quayMappingFetcher) {
        this.quayMappingFetcher = quayMappingFetcher;
    }

    @PostConstruct
    private void initialize() {
        executor.scheduleAtFixedRate(
                this::refreshCache,
                0,
                refreshFrequencyMin,
                TimeUnit.MINUTES
        );
    }

    @PreDestroy
    private void destroy() {
        executor.shutdown();
    }

    public void refreshCache() {
        synchronized (LOCK) {
            Map<String, String> fresh = quayMappingFetcher.fetchQuayMapping(tiamatPath.resolve("technique").resolve("quayIdMappings.csv").toAbsolutePath().toString());
            if (fresh.isEmpty()) {
                log.warn("TadQuayIdCache: file is empty or inaccessible; cache retained ({} entries)",
                        quayMappings.size());
                return;
            }
            quayMappings.clear();
            quayMappings.putAll(fresh);
            log.info("TadQuayIdCache: {} entries loaded", quayMappings.size());
        }
    }

    public void putAll(Map<String, String> mappings) {
        if (MapUtils.isEmpty(mappings)) {
            return;
        }
        quayMappings.putAll(mappings);
        log.debug("TadQuayIdCache: {} mappings added since import, size={}",
                mappings.size(), quayMappings.size());
    }

    public void put(String producerNetexId, String mobiItiNetexId) {
        if (producerNetexId != null && mobiItiNetexId != null) {
            quayMappings.put(producerNetexId, mobiItiNetexId);
        }
    }

    public Optional<String> getMobiItiNetexId(String producerNetexId) {
        if (quayMappings.isEmpty()) {
            synchronized (LOCK) {
                if (quayMappings.isEmpty()) {
                    refreshCache();
                }
            }
        }
        return Optional.ofNullable(quayMappings.get(producerNetexId));
    }

    public String getMobiItiNetexIdOrFallback(String producerNetexId) {
        return getMobiItiNetexId(producerNetexId).orElse(producerNetexId);
    }
}
