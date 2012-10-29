// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ReadingCurrentVersionsTest {

    private static final int V100 = 100;
    private static final int V200 = 200;

    @Test
    public void gets_the_version_number_of_the_class() {
        @Upgradeable(UpgraderV100.class)
        class MyEntity {
        }

        DataVersions versions = ClassAnalyzer.readCurrentVersions(MyEntity.class);

        assertThat("version", versions.forDataType(MyEntity.class).dataVersion, is(V100));
    }

    @Test
    public void gets_the_version_number_of_parent_classes() {
        @Upgradeable(UpgraderV200.class)
        class Parent {
        }
        @Upgradeable(UpgraderV100.class)
        class Child extends Parent {
        }

        DataVersions versions = ClassAnalyzer.readCurrentVersions(Child.class);

        assertThat("child version", versions.forDataType(Child.class).dataVersion, is(V100));
        assertThat("parent version", versions.forDataType(Parent.class).dataVersion, is(V200));
    }

    // TODO: read version numbers of upgradeable fields


    static class UpgraderV100 extends DummyUpgrader {
        @Override
        public int version() {
            return V100;
        }
    }

    static class UpgraderV200 extends DummyUpgrader {
        @Override
        public int version() {
            return V200;
        }
    }

    static abstract class DummyUpgrader implements Upgrader {
        @Override
        public void upgrade(ObjectNode data, int version) {
        }
    }
}
