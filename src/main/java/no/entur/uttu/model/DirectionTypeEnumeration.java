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

package no.entur.uttu.model;

public enum DirectionTypeEnumeration {

    INBOUND("inbound"),
    OUTBOUND("outbound"),
    CLOCKWISE("clockwise"),
    ANTICLOCKWISE("anticlockwise");

    private final String value;

    DirectionTypeEnumeration(String v) {
        this.value = v;
    }

    public static DirectionTypeEnumeration fromDirectionId(String directionId) {
        return switch (directionId) {
            case "0" -> INBOUND;
            case "1" -> OUTBOUND;
            default -> throw new IllegalArgumentException("Invalid directionId: " + directionId);
        };
    }

    public String value() {
        return this.value;
    }

    public String toDirectionId() {
        return switch (this) {
            case INBOUND -> "0";
            case OUTBOUND -> "1";
            default -> throw new IllegalArgumentException("No directionId for: " + this);
        };
    }

}
