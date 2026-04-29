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

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static no.entur.uttu.model.ModelTestUtil.assertCheckPersistableFails;

class FlexibleStopPlaceTest {

    @Test
    void checkPersistable_whenOnlyHailAndRideArea_thenSuccess() {
        FlexibleStopPlace flexibleStopPlace = new FlexibleStopPlace();
        flexibleStopPlace.setHailAndRideArea(hailAndRideArea("q1", "q2"));
        flexibleStopPlace.checkPersistable();
    }

    @Test
    void checkPersistable_whenOnlyFlexibleArea_thenSuccess() {
        FlexibleStopPlace flexibleStopPlace = new FlexibleStopPlace();
        List<FlexibleArea> flexibleAreas = new ArrayList<>();
        flexibleAreas.add(new FlexibleArea());
        flexibleStopPlace.setFlexibleAreas(flexibleAreas);
        flexibleStopPlace.checkPersistable();
    }

    @Test
    void checkPersistable_whenBothFlexibleAreaAndHailAndRideArea_giveException() {
        FlexibleStopPlace flexibleStopPlace = new FlexibleStopPlace();
        List<FlexibleArea> flexibleAreas = new ArrayList<>();
        flexibleAreas.add(new FlexibleArea());
        flexibleStopPlace.setFlexibleAreas(flexibleAreas);
        flexibleStopPlace.setHailAndRideArea(hailAndRideArea("q1", "q2"));
        assertCheckPersistableFails(flexibleStopPlace);
    }

    @Test
    void checkPersistable_whenNeitherFlexibleAreaNorHailAndRideArea_giveException() {
        assertCheckPersistableFails(new FlexibleStopPlace());
    }


    private HailAndRideArea hailAndRideArea(String quayStart, String quayEnd) {
        HailAndRideArea hailAndRideArea = new HailAndRideArea();
        hailAndRideArea.setStartQuayRef(quayStart);
        hailAndRideArea.setEndQuayRef(quayEnd);
        return hailAndRideArea;
    }
}
