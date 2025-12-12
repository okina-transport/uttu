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

package no.entur.uttu.graphql.scalars;

import graphql.language.ArrayValue;
import graphql.language.FloatValue;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;
import org.jspecify.annotations.Nullable;
import org.locationtech.jts.geom.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class GeoJSONCoordinatesScalar {

    public static GraphQLScalarType getGraphQGeoJSONCoordinatesScalar() {
        return GraphQLGeoJSONCoordinates;
    }

    private static GraphQLScalarType GraphQLGeoJSONCoordinates =
            GraphQLScalarType.newScalar()
                    .name("Coordinates")
                    .description("GeoJSON coordinates as [[x,y], [x,y], ...]; runtime type: org.locationtech.jts.geom.Coordinate[]")
                    .coercing(new Coercing<Coordinate[], List<List<Double>>>()
                    {
        @Override
        public List<List<Double>> serialize(Object input) {
            if (input instanceof Coordinate[]) {
                Coordinate[] coordinates = ((Coordinate[]) input);
                List<List<Double>> coordinateList = new ArrayList<>();
                for (Coordinate coordinate : coordinates) {
                    List<Double> coordinatePair = new ArrayList<>();
                    coordinatePair.add(coordinate.x);
                    coordinatePair.add(coordinate.y);

                    coordinateList.add(coordinatePair);
                }
                return coordinateList;
            }
            return null;
        }

        @Override
        public Coordinate[] parseValue(Object input) {
            List<List<? extends Number>> coordinateList = (List<List<? extends Number>>) input;

            Coordinate[] coordinates = new Coordinate[coordinateList.size()];

            for (int i = 0; i < coordinateList.size(); i++) {
                coordinates[i] = new Coordinate(coordinateList.get(i).get(0).doubleValue(), coordinateList.get(i).get(1).doubleValue());
            }

            return coordinates;
        }

        @Override
        public Coordinate[] parseLiteral(Object input) {
            if (input instanceof ArrayValue) {
                ArrayList<ArrayValue> coordinateList = (ArrayList) ((ArrayValue) input).getValues();
                Coordinate[] coordinates = new Coordinate[coordinateList.size()];

                for (int i = 0; i < coordinateList.size(); i++) {
                    ArrayValue v = coordinateList.get(i);

                    FloatValue longitude = (FloatValue) v.getValues().get(0);
                    FloatValue latitude = (FloatValue) v.getValues().get(1);
                    coordinates[i] = new Coordinate(longitude.getValue().doubleValue(), latitude.getValue().doubleValue());

                }
                return coordinates;
            }
            return null;
        }
    }).build();
}
