package no.entur.uttu.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ExportGtfsFlexRouteBuilder extends RouteBuilder {


    private final ExportGtfsFlexProcessor exportGtfsFlexProcessor;

    public ExportGtfsFlexRouteBuilder(ExportGtfsFlexProcessor exportGtfsFlexProcessor) {
        this.exportGtfsFlexProcessor = exportGtfsFlexProcessor;
    }

    @Override
    public void configure() {
        from("activemq:queue:exportGtfsFlexQueue")
                .process(exportGtfsFlexProcessor)
                .to("activemq:queue:exportGtfsFlexUttuCompleted")
                .routeId("activemq:queue:exportGtfsFlexUttuQueue");
    }
}