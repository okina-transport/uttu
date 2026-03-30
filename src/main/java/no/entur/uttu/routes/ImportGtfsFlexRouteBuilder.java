package no.entur.uttu.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ImportGtfsFlexRouteBuilder extends RouteBuilder {

    private final ImportGtfsFlexProcessor importGtfsFlexProcessor;

    public ImportGtfsFlexRouteBuilder(ImportGtfsFlexProcessor importGtfsFlexProcessor) {
        this.importGtfsFlexProcessor = importGtfsFlexProcessor;
    }

    @Override
    public void configure() {
        from("activemq:queue:importGtfsFlexUttuQueue")
                .process(importGtfsFlexProcessor)
                .to("activemq:queue:importGtfsFlexUttuCompleted")
                .routeId("activemq:queue:importGtfsFlexUttuQueue");
    }
}