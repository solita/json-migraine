// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.*;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UpgraderFactoryTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final UpgraderFactory upgraderFactory = new UpgraderFactory();


    @Test
    public void constructs_the_upgrader_in_the_upgradeable_annotation() {
        @Upgradeable(DummyUpgrader.class)
        class HasUpgradeableAnnotation {
        }

        Upgrader upgrader = upgraderFactory.getUpgrader(HasUpgradeableAnnotation.class);

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
        upgraderFactory.getUpgrader(annotationMissingClass);
    }


    static class DummyUpgrader implements Upgrader {

        @Override
        public int version() {
            return 1;
        }

        @Override
        public void upgrade(ObjectNode data, int dataVersion) {
        }
    }
}
