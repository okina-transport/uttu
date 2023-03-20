package no.entur.uttu.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;

public class CustomStopPlaceViewDeserializer extends StdDeserializer<StopPlaceView> {


    public CustomStopPlaceViewDeserializer() {
        this(null);
    }

    public CustomStopPlaceViewDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public StopPlaceView deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        StopPlaceView spView = new StopPlaceView();
        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);

        JsonNode idNode = node.get("id");
        String idodeTxt = idNode.asText();

        return spView;
    }


}
