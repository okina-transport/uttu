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


import no.entur.uttu.config.MockedRoleAssignmentExtractor;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.rutebanken.helper.organisation.RoleAssignment;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Properties;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;

@NotThreadSafe
@Disabled("Disabled until network retrieval is stable")
class ProviderGraphQLIntegrationTest extends AbstractGraphQLResourceIntegrationTest {
    @Autowired
    MockedRoleAssignmentExtractor mockedRoleAssignmentExtractor;

    String getProvidersQuery = """
               query GetProviders {
                 providers {
                   name
                   code
                 }
               }
            """;

    protected String getUrl() {
        return "/services/flexible-lines/providers/graphql";
    }

    @Override
    protected Properties getCredentials() {
        Properties credentials = new Properties();
        credentials.put("username", "user");
        credentials.put("password", "secret");
        return credentials;
    }

    @Test
    void getProvidersTest() {
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(
                RoleAssignment.builder().withRole("editRouteData").withOrganisation("TST").build()
        );

        executeGraphqQLQueryOnly(getProvidersQuery)
                .body("data.providers", iterableWithSize(2))
                .body("data.providers[0].code", equalTo("tst"));

        mockedRoleAssignmentExtractor.reset();
    }

    @Test
    void getMoreProvidersTest() {
        mockedRoleAssignmentExtractor.setNextReturnedRoleAssignment(
                RoleAssignment.builder().withRole("editRouteData").withOrganisation("FOO").build()
        );

        executeGraphqQLQueryOnly(getProvidersQuery)
                .body("data.providers", iterableWithSize(2))
                .body("data.providers[1].code", equalTo("foo"));

        mockedRoleAssignmentExtractor.reset();
    }
}
