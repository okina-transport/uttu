package no.entur.uttu.graphql;

import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;

@Disabled("Disabled until network retrieval is stable")
class StopPlaceByQuayGraphQLIntegrationTest extends AbstractFlexibleLinesGraphQLIntegrationTest {

    @Test
    void getStopPlaceByQuayRefTest() {
        String id = "NSR:StopPlace:337";
        String query = """
                { stopPlaceByQuayRef(id:"%s") { id, name { lang value }, quays { id publicCode }}}
                """.formatted(id);
        assertResponse(executeGraphqQLQueryOnly(query), "stopPlaceByQuayRef");
    }

    void assertResponse(ValidatableResponse rsp, String path) {
        rsp.body("data. " + path + ".id", equalTo("NSR:StopPlace:337"));
    }
}

