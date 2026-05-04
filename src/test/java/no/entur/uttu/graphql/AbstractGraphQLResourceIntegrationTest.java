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

package no.entur.uttu.graphql;

import graphql.Assert;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import no.entur.uttu.UttuIntegrationTest;
import no.entur.uttu.repository.CodespaceRepository;
import no.entur.uttu.repository.ProviderRepository;
import no.entur.uttu.repository.RemoteNetworkRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Properties;

import static io.restassured.RestAssured.given;


@Slf4j
abstract class AbstractGraphQLResourceIntegrationTest extends UttuIntegrationTest {

    public static final String NETWORK_ID = "TST:Network:1";
    protected static final LocalDate TODAY = LocalDate.now();
    @Autowired
    private RemoteNetworkRepository networkRepository;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private CodespaceRepository codeSpaceRepository;


    @BeforeEach
    void configureRestAssured() { // non static
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
    }

    String extractId(ValidatableResponse response, String path) {
        String id = response.extract().body().path("data." + path + ".id");
        Assert.assertNotNull(id);
        return id;
    }

    protected ValidatableResponse executeGraphQL(String query, String variables) {
        return executeGraphQL(query, variables, 200);
    }

    protected ValidatableResponse executeGraphqQLQueryOnly(String query) {
        return executeGraphQLQueryOnly(query, 200);
    }

    /**
     * When sending empty parameters, specify 'query' directly.
     * Escapes quotes and newlines
     */
    protected ValidatableResponse executeGraphQLQueryOnly(String query, int httpStatusCode) {
        return executeGraphQL(query, null, httpStatusCode);
    }

    protected ValidatableResponse executeGraphQL(String query, String variables, int httpStatusCode) {

        String graphQlJsonQuery = "{ \"query\": \"%s\", \"variables\": \"%s\"}".formatted(
                query.replaceAll("\"", "\\\\\"")
                        .replaceAll("\n", "\\\\n")
                        .replaceAll("\r", "\\\\r"),
                variables != null ? variables.replaceAll("\"", "\\\\\"")
                        .replaceAll("\n", "\\\\n")
                        .replaceAll("\r", "\\\\r") : "{}"
        );
        log.info("Executing query: {}", graphQlJsonQuery);
        return executeGraphQL(graphQlJsonQuery, httpStatusCode);
    }

    protected ValidatableResponse executeGraphQL(String graphQlJsonQuery, int httpStatusCode) {
        return authenticatedRequestSpecification()
                .port(port)
                .contentType(ContentType.JSON)
                .body(graphQlJsonQuery)
                .when()
                .post(getUrl())
                .then()
                .log().body()
                .statusCode(httpStatusCode)
                .assertThat();
    }

    protected RequestSpecification authenticatedRequestSpecification() {
        Properties credentials = getCredentials();
        return given()
                .auth().preemptive().basic((String) credentials.get("username"), (String) credentials.get("password"));
    }

    protected abstract String getUrl();

    protected Properties getCredentials() {
        Properties credentials = new Properties();
        credentials.put("username", "admin");
        credentials.put("password", "topsecret");
        return credentials;
    }
}
