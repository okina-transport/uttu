/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package no.entur.uttu.stopplace;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import jakarta.annotation.PostConstruct;
import no.entur.uttu.config.NetexHttpMessageConverter;
import no.entur.uttu.model.DTO.QuayIdMapping;
import no.entur.uttu.model.QuayView;
import no.entur.uttu.model.StopPlaceView;
import no.entur.uttu.cache.TadQuayIdCache;
import no.entur.uttu.security.TokenService;
import org.jspecify.annotations.NonNull;
import org.locationtech.jts.geom.Polygon;
import org.rutebanken.netex.model.StopPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.jdo.annotations.Cacheable;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Integrates with https://github.com/entur/mummu stop place registry API
 * to retrieve a stop place given the ID of one of its quays.
 */
@Component
public class DefaultStopPlaceRegistry implements StopPlaceRegistry {

    private static final Logger logger = LoggerFactory.getLogger(DefaultStopPlaceRegistry.class);
    private static final String ET_CLIENT_ID_HEADER = "ET-Client-ID";
    private static final String ET_CLIENT_NAME_HEADER = "ET-Client-Name";
    private final RestTemplate restTemplate = new RestTemplate();
    private final TokenService tokenService;
    private final TadQuayIdCache tadQuayIdCache;

    @Value("${http.client.name:uttu}")
    private String clientName;

    @Value("${http.client.id:uttu}")
    private String clientId;

    @Value("${stopplace.registry.url:https://api.dev.entur.io/stop-places/v1/read}")
    private String stopPlaceRegistryUrl;

    private final LoadingCache<String, org.rutebanken.netex.model.StopPlace> stopPlaceByQuayRefCache = CacheBuilder.newBuilder()
            .expireAfterWrite(6, TimeUnit.HOURS)
            .build(new CacheLoader<>() {
                @Override
                public org.rutebanken.netex.model.StopPlace load(@NonNull String quayRef) {
                    return lookupStopPlaceByQuayRef(quayRef);
                }
            });

    public DefaultStopPlaceRegistry(TokenService tokenService, TadQuayIdCache tadQuayIdCache) {
        this.tokenService = tokenService;
        this.tadQuayIdCache = tadQuayIdCache;
    }

    @PostConstruct
    private void setup() {
        restTemplate.getMessageConverters().add(new NetexHttpMessageConverter());
    }

    @Override
    @Cacheable("stopPlacesByQuayRef")
    public Optional<org.rutebanken.netex.model.StopPlace> getStopPlaceByQuayRef(String quayRef) {
        try {
            return Optional.of(stopPlaceByQuayRefCache.get(quayRef));
        } catch (ExecutionException e) {
            logger.warn("Failed to get stop place by quay ref ${}", quayRef);
            return Optional.empty();
        }

    }

    private org.rutebanken.netex.model.StopPlace lookupStopPlaceByQuayRef(String quayRef) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(stopPlaceRegistryUrl + "tad_stop_place_from_quay");
            builder.replaceQueryParam("quayId", quayRef);

            ResponseEntity<StopPlace> exchange = restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.GET,
                    createHttpEntity(),
                    StopPlace.class
            );

            return exchange.getBody();
        } catch (Exception e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

    private HttpEntity<Void> createHttpEntity() {
        return createHttpEntity(MediaType.APPLICATION_XML);
    }

    private HttpEntity<Void> createHttpEntity(MediaType mediaType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(mediaType));
        headers.set(ET_CLIENT_NAME_HEADER, clientName);
        headers.set(ET_CLIENT_ID_HEADER, clientId);
        headers.set("Authorization", "Bearer " + tokenService.getToken());
        return new HttpEntity<>(headers);
    }

    private <T> HttpEntity<T> createHttpEntity(T body, MediaType mediaType, String provider) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(mediaType);
        headers.setAccept(Collections.singletonList(mediaType));
        headers.set(ET_CLIENT_NAME_HEADER, clientName);
        headers.set(ET_CLIENT_ID_HEADER, clientId);
        headers.set("Authorization", "Bearer " + tokenService.getToken());
        headers.set("provider", provider);
        return new HttpEntity<>(body, headers);
    }

    public void createTadQuays(String provider, List<StopPlaceView> stops) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
                    stopPlaceRegistryUrl + "netex/createTADquays"
            );

            HttpEntity<List<StopPlaceView>> entity = createHttpEntity(
                    stops,
                    MediaType.APPLICATION_JSON,
                    provider
            );

            ResponseEntity<List<QuayIdMapping>> response = restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );
            if (response.getBody() != null) {
                Map<String, String> mappingMap = response.getBody().stream()
                        .filter(m -> m.getUttuNetexId() != null && m.getTiamatSuperId() != null)
                        .collect(Collectors.toMap(
                                QuayIdMapping::getUttuNetexId,
                                QuayIdMapping::getTiamatSuperId,
                                (a, b) -> b
                        ));

                tadQuayIdCache.putAll(mappingMap);
            }
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Tiamat error response: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Error while creating TAD quays for provider: " + provider, e);
        }
    }

    public List<QuayView> getQuayListFromRegistry(String provider, Set<String> originalIds) {
        try {
            UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(
                    stopPlaceRegistryUrl + "netex/quays"
            );

            HttpEntity<Set<String>> entity = createHttpEntity(
                    originalIds,
                    MediaType.APPLICATION_JSON,
                    provider
            );

            ResponseEntity<List<QuayView>> response = restTemplate.exchange(
                    builder.build().encode().toUri(),
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            return response.getBody() != null ? response.getBody() : List.of();

        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("Registry error response: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Error while getting quay list for provider: " + provider, e);
        }
    }

    public List<StopPlaceView> getMembersForArea(Polygon polygon) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(stopPlaceRegistryUrl + "netex/getTADStopPlaces");
        builder.replaceQueryParam("area", polygon.toString());


        try {
            HashMap<String, String> postDataParams = new HashMap<>();

            URL url = URI.create(stopPlaceRegistryUrl + "netex/getTADStopPlaces?" + getPostDataString(postDataParams)).toURL();
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/json");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + tokenService.getToken());
            OutputStream outputStream = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            writer.write(polygon.toString());
            writer.close();

            InputStream inputStream = connection.getInputStream();
            String resultString = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));

            return convertJSONtoObjects(resultString);
        } catch (IOException e) {
            throw new RuntimeException("Error while getting members for area", e);
        }
    }

    private List<StopPlaceView> convertJSONtoObjects(String jsonDisruptions) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonDisruptions, new TypeReference<>() {
        });
    }

    private String getPostDataString(HashMap<String, String> params) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }

        return result.toString();
    }
}
