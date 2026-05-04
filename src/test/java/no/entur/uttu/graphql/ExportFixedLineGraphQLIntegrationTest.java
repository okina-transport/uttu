package no.entur.uttu.graphql;

import io.restassured.response.ValidatableResponse;
import no.entur.uttu.model.job.JobStatus;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;

@Disabled("Disabled until network retrieval is stable")
class ExportFixedLineGraphQLIntegrationTest extends AbstractFixedLinesGraphQLIntegrationTest {

    @Test
    void createExport() {
        String name = "Fiktiv linje";
        ValidatableResponse fixedLineResponse = createFixedLine(name);

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

        String lineRef = fixedLineResponse.extract().body().path("data.mutateFixedLine.id");
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
    }
}
