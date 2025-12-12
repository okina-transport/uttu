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

package no.entur.uttu.util;

import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

import java.time.LocalTime;

public class ValidationHelperTest {


    @Test
    public void isNotAfter_whenEitherTimeIsNull_shouldBeTrue() {
        Assertions.assertTrue(ValidationHelper.isNotAfter(null, 1, LocalTime.MIDNIGHT, 0));
        Assertions.assertTrue(ValidationHelper.isNotAfter(LocalTime.MIDNIGHT, 1, null, 0));
        Assertions.assertTrue(ValidationHelper.isNotAfter(null, 1, null, 0));
    }


    @Test
    public void isNotAfter_whenOtherDayOffsetIsGreater_shouldBeTrue() {
        Assertions.assertTrue(ValidationHelper.isNotAfter(LocalTime.MAX, 0, LocalTime.MIN, 1));
    }

    @Test
    public void isNotAfter_whenOtherLocalTimeIsGreater_shouldBeTrue() {
        Assertions.assertTrue(ValidationHelper.isNotAfter(LocalTime.of(10, 0), 0, LocalTime.of(10, 1), 0));
    }

    @Test
    public void isNotAfter_whenEqual_shouldBeTrue() {
        Assertions.assertTrue(ValidationHelper.isNotAfter(LocalTime.of(10, 0), 0, LocalTime.of(10, 0), 1));
    }

    @Test
    public void isNotAfter_whenOtherDayOffsetIsSmaller_shouldBeFalse() {
        Assertions.assertFalse(ValidationHelper.isNotAfter(LocalTime.MIN, 1, LocalTime.MAX, 0));
    }

    @Test
    public void isNotAfter_whenOtherLocalTimeIsSmaller_shouldBeFalse() {
        Assertions.assertFalse(ValidationHelper.isNotAfter(LocalTime.of(10, 1), 0, LocalTime.of(10, 0), 0));
    }
}
