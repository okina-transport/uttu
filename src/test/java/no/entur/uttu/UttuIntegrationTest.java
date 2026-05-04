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

package no.entur.uttu;


import lombok.extern.slf4j.Slf4j;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.lang3.Strings;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.activemq.ArtemisContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = UttuTestApp.class)
@ActiveProfiles({"google-pubsub-emulator", "test"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public abstract class UttuIntegrationTest {


    private static final Path OAUTH_TOKEN = Path.of("src/test/resources/oauth/token.json");
    private static final PostgreSQLContainer postgres;
    private static final ArtemisContainer artemis;

    static {
        postgres = new PostgreSQLContainer(DockerImageName.parse(
                        "postgis/postgis:15-3.5-alpine")
                .asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("uttu")
                .withUsername("uttu")
                .withPassword("uttu")
                .withReuse(true);
        postgres.start();
        artemis = new ArtemisContainer(DockerImageName.parse("registry.okina.fr/oki/activemq-artemis:2.38.0")
                .asCompatibleSubstituteFor("apache/activemq-artemis"))
                .withUser("uttu")
                .withPassword("uttu")
                .withReuse(true);
        artemis.start();
    }

    @Value("${local.server.port}")
    protected int port;
    @Autowired
    protected JmsTemplate jmsTemplate;
    private MockWebServer oauthServerMock;

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("jakarta.persistence.jdbc.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);

        registry.add("spring.activemq.broker-url", artemis::getBrokerUrl);
        registry.add("spring.activemq.user", artemis::getUser);
        registry.add("spring.activemq.password", artemis::getPassword);
        registry.add("activemq.broker.host", artemis::getHost);
    }

    @BeforeEach
    void beforeEach() throws IOException {
        oauthServerMock = new MockWebServer();
        oauthServerMock.setDispatcher(new OauthServerDispatcher());
        oauthServerMock.start(8080);
        
        jmsTemplate.setReceiveTimeout(30000);
    }

    @AfterEach
    void afterEach() throws IOException {
        oauthServerMock.shutdown();
    }

    public static class OauthServerDispatcher extends okhttp3.mockwebserver.Dispatcher {

        @Override
        public @NonNull MockResponse dispatch(@NonNull RecordedRequest recordedRequest) {
            try {
                if (Strings.CS.equals(recordedRequest.getPath(),
                        "/auth/realms/fakeREALM/protocol/openid-connect/token")) {
                    return new MockResponse()
                            .setResponseCode(200)
                            .setHeader("Content-Type", "application/json")
                            .setBody(Files.readString(OAUTH_TOKEN));
                } else {
                    log.error("Unexpected request path: {}", recordedRequest.getPath());
                    return new MockResponse().setResponseCode(404);
                }
            } catch (IOException e) {
                log.error("Error reading oauth token file", e);
            }
            return new MockResponse().setResponseCode(500);
        }
    }

}

