package no.entur.uttu.cache;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.export.blob.BlobStoreService;
import no.entur.uttu.repository.StopRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

@Component
@Slf4j
public class QuayMappingFetcher {

    private final BlobStoreService blobStoreService;
    private final StopRepository stopRepository;

    public QuayMappingFetcher(BlobStoreService blobStoreService, StopRepository stopRepository) {
        this.blobStoreService = blobStoreService;
        this.stopRepository = stopRepository;
    }

    public Map<String, String> fetchQuayMapping(String name) {
        Map<String, String> mappings = new HashMap<>();

        if (StringUtils.isBlank(name)) {
            return mappings;
        }

        final InputStream blob = blobStoreService.downloadBlob(name);
        if (blob == null) {
            log.warn("Quay mapping blob not found: {}", name);
            return mappings;
        }

        List<String> uttuNetexIds = new ArrayList<>();
        stopRepository.findAll().forEach(stop -> uttuNetexIds.add(stop.getNetexId()));

        BufferedReader reader = new BufferedReader(new InputStreamReader(blob));
        reader.lines().forEach(line -> {
            StringTokenizer tokenizer = new StringTokenizer(line, ",");
            if (tokenizer.countTokens() < 2) {
                log.warn("Line ignored (unexpected format): {}", line);
                return;
            }
            String producerNetexId = tokenizer.nextToken();
            String mobiItiNetexId  = tokenizer.nextToken();

            if (uttuNetexIds.contains(producerNetexId)) {
                mappings.put(producerNetexId, mobiItiNetexId);
            }
        });

        log.info("Quay mapping fetched: {} entries from {}", mappings.size(), name);
        return mappings;
    }
}