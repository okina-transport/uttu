package no.entur.uttu.importer.gtfsflex.idmapper;

import java.util.Map;
import java.util.Set;

public interface IdMapper {
    Map<String, String> buildLineOriginalToNetexIdsMap(Set<String> originalIds, String datasetId);

    Map<String, String> buildJourneyPatternOriginalToNetexIdsMap(Set<String> originalIds, String datasetId);

    Map<String, String> buildServiceJourneyOriginalToNetexIdsMap(Set<String> originalIds, String datasetId);

    Map<String, String> buildDayTypeIdsOriginalToNetexMap(Set<String> originalIds, String datasetId);

    Map<String, String> buildFlexibleStopPlaceOriginalToNetexIdsMap(Set<String> originalIds, String datasetId);

    Map<String, String> buildStopPointInJourneyPatternOriginalToNetexIdsMap(Set<String> originalIds, String datasetId);

    Map<String, String> buildTimetabledPassingTimeOriginalToNetexIdsMap(Set<String> originalIds, String datasetId);

    Map<String, String> buildQuayOriginalToNetexIdsMap(Set<String> originalIds, String datasetId);

    Map<String, String> buildDestinationDisplayOriginalToNetexIdsMap(Set<String> originalIds, String datasetId);

    Map<String, String> buildNetworkIdMapper(Set<String> originalIds, String datasetId);
}