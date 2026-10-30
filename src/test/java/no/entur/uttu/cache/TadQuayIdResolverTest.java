package no.entur.uttu.cache;

import no.entur.uttu.model.QuayView;
import no.entur.uttu.stopplace.StopPlaceRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TadQuayIdResolverTest {

    private TadQuayIdCache tadQuayIdCache;
    private StopPlaceRegistry stopPlaceRegistry;
    private TadQuayIdResolver tested;

    @BeforeEach
    void setUp() {
        tadQuayIdCache = mock(TadQuayIdCache.class);
        stopPlaceRegistry = mock(StopPlaceRegistry.class);
        tested = new TadQuayIdResolver(tadQuayIdCache, stopPlaceRegistry);
        when(tadQuayIdCache.getMobiItiNetexId(anyString())).thenReturn(Optional.empty());
    }

    @Test
    void resolve_whenAllIdsCached_thenDoesNotCallTiamat() {
        when(tadQuayIdCache.getMobiItiNetexId("TEST:Quay:s_1")).thenReturn(Optional.of("MOBIITI:Quay:1"));

        Map<String, String> result = tested.resolve("test", Set.of("TEST:Quay:s_1"));

        assertEquals(Map.of("TEST:Quay:s_1", "MOBIITI:Quay:1"), result);
        verify(stopPlaceRegistry, never()).getQuayListFromRegistry(anyString(), any());
    }

    @Test
    void resolve_whenIdsMissingFromCache_thenAsksTiamatOnlyForMissingIdsAndCachesThem() {
        when(tadQuayIdCache.getMobiItiNetexId("TEST:Quay:s_1")).thenReturn(Optional.of("MOBIITI:Quay:1"));
        when(stopPlaceRegistry.getQuayListFromRegistry("test", Set.of("TEST:Quay:s_2", "TEST:Quay:s_3")))
                .thenReturn(List.of(quayView("TEST:Quay:s_2", "MOBIITI:Quay:2")));

        Map<String, String> result = tested.resolve("test", Set.of("TEST:Quay:s_1", "TEST:Quay:s_2", "TEST:Quay:s_3"));

        assertEquals(Map.of("TEST:Quay:s_1", "MOBIITI:Quay:1", "TEST:Quay:s_2", "MOBIITI:Quay:2"), result);
        verify(tadQuayIdCache).put("TEST:Quay:s_2", "MOBIITI:Quay:2");
    }

    private static QuayView quayView(String importedId, String netexId) {
        QuayView quayView = new QuayView();
        quayView.setImportedId(importedId);
        quayView.setNetexId(netexId);
        return quayView;
    }
}
