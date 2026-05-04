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

import io.restassured.response.ValidatableResponse;
import no.entur.uttu.model.job.JobStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@Disabled("Disabled until network retrieval is stable")
class ExportGraphQLIntegrationTest extends AbstractFlexibleLinesGraphQLIntegrationTest {


    @Test
    void createExport() {
        String name = "ExportTest";
        ValidatableResponse flexibleLineResponse = createFlexibleLine(name);

        String createExportQuery = """
                mutation export($export: ExportInput!) {
                 export(input: $export) {
                   id
                   name
                   exportStatus
                   downloadUrl
                   messages {
                       message
                       severity
                   }
                 }
                 }
                """;

        String lineRef = flexibleLineResponse.extract().body().path("data.mutateFlexibleLine.id");
        String variables = """    
                {
                  "export": {
                    "name": "%s",
                    "lineAssociations":[{ "lineRef": "%s" }]
                  }
                }
                """.formatted(name, lineRef);

        ValidatableResponse rsp = executeGraphQL(createExportQuery, variables)
                .body("data.export.id", startsWith("TST:Export"))
                .body("data.export.name", equalTo(name))
                .body("data.export.exportStatus", equalTo(JobStatus.FINISHED.value()))
                .body("data.export.downloadUrl", startsWith("tst/export/"));

        String downloadUrl = rsp.extract().body().path("data.export.downloadUrl");
        authenticatedRequestSpecification()
                .port(port)
                .when()
                .get("/services/flexible-lines/" + downloadUrl)
                .then()
                .log().body()
                .statusCode(200)
                .body(not(is(emptyOrNullString())));


        String deleteLineMutation = """
                mutation DeleteLine($id: ID!) {
                 deleteFlexibleLine(id: $id) {
                   id
                 }
                 }
                """;

        String deleteLineVariables = """
                {
                  "id": "%s"
                }
                """.formatted(lineRef);


        executeGraphQL(deleteLineMutation, deleteLineVariables)
                .body("data.deleteFlexibleLine.id", equalTo(lineRef));
    }

    @Override
    ValidatableResponse createFlexibleLine(String name, String operatorRef) {
        String flexAreaStopPlaceId = getFlexibleStopPlaceId(createFlexibleStopPlaceWithFlexibleArea(name + "FlexArea1"));
        String flexAreaStopPlaceId2 = getFlexibleStopPlaceId(createFlexibleStopPlaceWithFlexibleArea(name + "FlexArea2"));
        return createFlexibleLine(name, operatorRef, NETWORK_ID, flexAreaStopPlaceId, flexAreaStopPlaceId2);
    }


}
