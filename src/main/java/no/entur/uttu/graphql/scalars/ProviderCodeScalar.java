package no.entur.uttu.graphql.scalars;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import org.jspecify.annotations.Nullable;

public class ProviderCodeScalar {


    public static final GraphQLScalarType PROVIDER_CODE =
            GraphQLScalarType.newScalar()
                    .name("ProviderCode")
                    .description("Provider codes must be lower-case strings")
                    .coercing(new Coercing<String, String>()
                    {
        @Override
        public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
            return serializeProviderCode(dataFetcherResult);
        }

        @Override
        public  String parseValue(Object input) throws CoercingParseValueException {
            return parseProviderCodeFromValue(input);
        }

        @Override
        public  String parseLiteral(Object input) throws CoercingParseLiteralException {
            return parseProviderCodeAsLiteral(input);
        }
    }).build();

    private static boolean isValidProviderCode(String code) {
        return code.toLowerCase().equals(code);
    }

    private static String serializeProviderCode(Object dataFetcherResult) {
        String providerCode = String.valueOf(dataFetcherResult);
        if (isValidProviderCode(providerCode)) {
            return providerCode;
        } else {
            throw new CoercingSerializeException("Unable to serialize " + providerCode + " as a provider code");
        }
    }

    private static String parseProviderCodeFromValue(Object input) {
        if (input instanceof String) {
            String providerCode = input.toString();
            if (isValidProviderCode(providerCode)) {
                return providerCode;
            }
        }
        throw new CoercingParseValueException("Unable to parse variable value " + input + " as a provider code");
    }

    private static String parseProviderCodeAsLiteral(Object input) {
        if (input instanceof StringValue) {
            String providerCode = ((StringValue) input).getValue();
            if (isValidProviderCode(providerCode)) {
                return providerCode;
            }
        }
        throw new CoercingParseLiteralException("Value is not a provider code: '" + String.valueOf(input) + "'");
    }
}
