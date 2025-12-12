package no.entur.uttu.graphql.scalars;

import graphql.Scalars;
import graphql.language.StringValue;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProviderCodeScalarTest {
    GraphQLScalarType scalar = ProviderCodeScalar.PROVIDER_CODE;

    @Test
    public void serialize() {
        assertEquals("tst", scalar.getCoercing().serialize("tst"));
    }

    @Test
    public void parseLiteral() {
        assertEquals("tst", scalar.getCoercing().parseLiteral(new StringValue("tst")));
    }

    @Test
    public void parseValue() {
        assertEquals("tst", scalar.getCoercing().parseValue("tst"));
    }

    @Test()
    public void invalidSerializeThrows() {
        Assertions.assertThrows(CoercingSerializeException.class, () ->  scalar.getCoercing().serialize("TST"));
    }

    @Test()
    public void invalidParseLiteralThrows() {
        Assertions.assertThrows(CoercingParseLiteralException.class, () ->  scalar.getCoercing().parseLiteral(new StringValue("TST")));
    }

    @Test()
    public void invalidParseValueThrows() {
        Assertions.assertThrows(CoercingParseValueException.class, () ->   scalar.getCoercing().parseValue("TST"));
    }
}
