// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine.internal;

import fi.solita.jsonmigraine.api.*;
import org.codehaus.jackson.JsonNode;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class AnnotationUpgraderProviderTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final UpgraderProvider upgraderProvider = new AnnotationUpgraderProvider();


    @Test
    public void constructs_the_upgrader_in_the_upgradeable_annotation() {
        @Upgradeable(DummyUpgrader.class)
        class HasUpgradeableAnnotation {
        }

        Upgrader upgrader = upgraderProvider.getUpgrader(HasUpgradeableAnnotation.class);

        assertThat(upgrader, is(instanceOf(DummyUpgrader.class)));
    }

    @Test
    public void fails_if_the_upgradeable_annotation_is_missing() {
        class UpgradeableAnnotationMissing {
        }

        Class<?> annotationMissingClass = UpgradeableAnnotationMissing.class;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("annotation was missing");
        thrown.expectMessage(annotationMissingClass.getName());
        thrown.expectMessage(Upgradeable.class.getName());
        upgraderProvider.getUpgrader(annotationMissingClass);
    }


    private static class DummyUpgrader implements Upgrader {

        @Override
        public int version() {
            return 1;
        }

        @Override
        public JsonNode upgrade(JsonNode data, int dataVersion) {
            return data;
        }
    }
}
