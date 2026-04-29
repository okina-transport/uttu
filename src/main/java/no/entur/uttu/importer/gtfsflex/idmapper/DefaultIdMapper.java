package no.entur.uttu.importer.gtfsflex.idmapper;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class DefaultIdMapper implements IdMapper {

    public static String santizeId(String id) {
        return id.replace(":", "##3A##");
    }

    private static Map<String, String> buildDefaultIdMapForType(Set<String> originalIds, String format, String datasetId) {
        if (CollectionUtils.isEmpty(originalIds)) {
            return Map.of();
        }
        return originalIds.stream().collect(Collectors.toMap(Function.identity(), id -> String.format(format, datasetId.toUpperCase(), santizeId(id))));
    }

    @Override
    public Map<String, String> buildLineOriginalToNetexIdsMap(Set<String> originalIds, String datasetId) {
        return buildDefaultIdMapForType(originalIds, "%s:Line:%s", datasetId);
    }

    @Override
    public Map<String, String> buildJourneyPatternOriginalToNetexIdsMap(Set<String> originalIds, String datasetId) {
        return buildDefaultIdMapForType(originalIds, "%s:JourneyPattern:%s", datasetId);
    }

    @Override
    public Map<String, String> buildServiceJourneyOriginalToNetexIdsMap(Set<String> originalIds, String datasetId) {
        return buildDefaultIdMapForType(originalIds, "%s:ServiceJourney:%s", datasetId);
    }

    @Override
    public Map<String, String> buildDayTypeIdsOriginalToNetexMap(Set<String> originalIds, String datasetId) {
        return buildDefaultIdMapForType(originalIds, "%s:DayType:%s", datasetId);
    }

    @Override
    public Map<String, String> buildFlexibleStopPlaceOriginalToNetexIdsMap(Set<String> originalIds, String datasetId) {
        return buildDefaultIdMapForType(originalIds, "%s:FlexibleStopPlace:%s", datasetId);
    }

    @Override
    public Map<String, String> buildStopPointInJourneyPatternOriginalToNetexIdsMap(Set<String> originalIds, String datasetId) {
        return buildDefaultIdMapForType(originalIds, "%s:StopPointInJourneyPattern:%s", datasetId);
    }

    @Override
    public Map<String, String> buildTimetabledPassingTimeOriginalToNetexIdsMap(Set<String> originalIds, String datasetId) {
        return buildDefaultIdMapForType(originalIds, "%s:TimetabledPassingTime:%s", datasetId);
    }

    @Override
    public Map<String, String> buildQuayOriginalToNetexIdsMap(Set<String> originalIds, String datasetId) {
        return buildDefaultIdMapForType(originalIds, "%s:Quay:%s", datasetId);
    }

    @Override
    public Map<String, String> buildDestinationDisplayOriginalToNetexIdsMap(Set<String> originalIds, String datasetId) {
        return buildDefaultIdMapForType(originalIds, "%s:DestinationDisplay:%s", datasetId);
    }

    @Override
    public Map<String, String> buildNetworkIdMapper(Set<String> originalIds, String datasetId) {
        return buildDefaultIdMapForType(originalIds, "%s:Network:%s", datasetId);
    }

}
