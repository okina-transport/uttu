package no.entur.uttu.importer.gtfsflex;

import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.importer.gtfsflex.idmapper.IdMapper;
import no.entur.uttu.model.*;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class GtfsFlexIdMapperService {

    private final IdMapper idMapper;

    public GtfsFlexIdMapperService(IdMapper idMapper) {
        this.idMapper = idMapper;
    }

    public void mapIds(Referential gtfsImportReferential, String datasetId) {
        log.info("Mapping ids from original to NeTeX");
        mapEntitiesId(gtfsImportReferential.getFlexibleLinesByOriginalId(), datasetId);
        mapEntitiesId(gtfsImportReferential.getJourneyPatternsByOriginalId(), datasetId);
        mapEntitiesId(gtfsImportReferential.getServiceJourneysByOriginalId(), datasetId);
        mapEntitiesId(gtfsImportReferential.getDayTypesByOriginalId(), datasetId);
        mapEntitiesId(gtfsImportReferential.getFlexibleStopPlacesByOriginalId(), datasetId);
        mapEntitiesId(gtfsImportReferential.getStopPointInJourneyPatternsByOriginalId(), datasetId);
        mapEntitiesId(gtfsImportReferential.getTimetabledPassingTimesByOriginalId(), datasetId);
        mapEntitiesId(gtfsImportReferential.getStopsByOriginalId(), datasetId);
        mapEntitiesId(gtfsImportReferential.getDestinationDisplaysByOriginalId(), datasetId);
        mapEntitiesId(gtfsImportReferential.getNetworksByOriginalId(), datasetId);
        log.info("Mapping ids from original to NeTeX completed");
    }

    private void mapEntitiesId(Map<String, ? extends ProviderEntity> originalIdToEntity, String datasetId) {
        if (MapUtils.isEmpty(originalIdToEntity)) {
            return;
        }
        Set<? extends ProviderEntity> entities = new HashSet<>(originalIdToEntity.values());
        Set<String> entityOriginalIds = originalIdToEntity.keySet();
        Class<?> entityClass = entities.iterator().next().getClass();

        log.info("Mapping {} {} ids", entities.size(), entityClass.getSimpleName());
        Map<String, String> idMappings;
        if (entityClass == FlexibleLine.class) {
            idMappings = idMapper.buildLineOriginalToNetexIdsMap(entityOriginalIds, datasetId);
        } else if (entityClass == JourneyPattern.class) {
            idMappings = idMapper.buildJourneyPatternOriginalToNetexIdsMap(entityOriginalIds, datasetId);
        } else if (entityClass == ServiceJourney.class) {
            idMappings = idMapper.buildServiceJourneyOriginalToNetexIdsMap(entityOriginalIds, datasetId);
        } else if (entityClass == DayType.class) {
            idMappings = idMapper.buildDayTypeIdsOriginalToNetexMap(entityOriginalIds, datasetId);
        } else if (entityClass == FlexibleStopPlace.class) {
            idMappings = idMapper.buildFlexibleStopPlaceOriginalToNetexIdsMap(entityOriginalIds, datasetId);
        } else if (entityClass == StopPointInJourneyPattern.class) {
            idMappings = idMapper.buildStopPointInJourneyPatternOriginalToNetexIdsMap(entityOriginalIds, datasetId);
        } else if (entityClass == TimetabledPassingTime.class) {
            idMappings = idMapper.buildTimetabledPassingTimeOriginalToNetexIdsMap(entityOriginalIds, datasetId);
        } else if (entityClass == Stop.class) {
            idMappings = idMapper.buildQuayOriginalToNetexIdsMap(entityOriginalIds, datasetId);
        } else if (entityClass == DestinationDisplay.class) {
            idMappings = idMapper.buildDestinationDisplayOriginalToNetexIdsMap(entityOriginalIds, datasetId);
        } else if (entityClass == Network.class) {
            idMappings = idMapper.buildNetworkIdMapper(entityOriginalIds, datasetId);
        } else {
            throw new IllegalArgumentException("Unsupported entity class: " + entityClass.getSimpleName());
        }

        for (Map.Entry<String, ? extends ProviderEntity> entry : originalIdToEntity.entrySet()) {
            String netexId = idMappings.get(entry.getKey());
            if (StringUtils.isBlank(netexId)) {
                throw new IllegalStateException("Could not map id " + entry.getKey() + " to NeTeX id for entity " + entry.getValue().getClass().getSimpleName());
            }
            log.debug("originalId: {}, netexId {}, entity: {}", entry.getKey(), netexId, entry.getValue().getClass().getSimpleName());
            entry.getValue().setNetexId(netexId);
        }
        log.info("Mapping {} {} ids from original to NeTeX completed", entities.size(), entityClass.getSimpleName());
    }

}
