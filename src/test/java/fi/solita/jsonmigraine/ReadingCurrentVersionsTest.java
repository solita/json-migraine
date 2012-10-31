// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.JsonNode;
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

        assertThat("version", versions.getVersion(MyEntity.class), is(V100));
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

        assertThat("child version", versions.getVersion(Child.class), is(V100));
        assertThat("parent version", versions.getVersion(Parent.class), is(V200));
    }

    @Test
    public void gets_the_version_number_of_upgradeable_fields() {
        @Upgradeable(UpgraderV200.class)
        class MyValue {
        }
        @Upgradeable(UpgraderV100.class)
        class MyEntity {
            MyValue value;
        }

        DataVersions versions = ClassAnalyzer.readCurrentVersions(MyEntity.class);

        assertThat(versions.getVersion(MyValue.class), is(V200));
    }

    @Test
    public void gets_the_version_number_of_upgradeable_array_fields() {
        @Upgradeable(UpgraderV200.class)
        class MyValue {
        }
        @Upgradeable(UpgraderV100.class)
        class MyEntity {
            MyValue[] values;
        }

        DataVersions versions = ClassAnalyzer.readCurrentVersions(MyEntity.class);

        assertThat(versions.getVersion(MyValue.class), is(V200));
    }


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
        public void upgrade(JsonNode data, int version) {
        }
    }
}
