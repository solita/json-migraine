// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine.endToEnd;

import fi.solita.jsonmigraine.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class SimpleClassesTest {

    private final TypeRenames renames = new TypeRenames();
    private final JsonMigraine jsonMigraine = new JsonMigraine(new ObjectMapper(), renames);

    {
        renames.rename(SimpleV1.class.getName(), SimpleV2.class.getName());
    }

    @Test
    public void upgrades_classes() throws Exception {
        SimpleV1 v1 = new SimpleV1();
        v1.unmodified = "foo";
        v1.oldField = "bar";

        String serialized = jsonMigraine.serialize(v1);
        SimpleV2 v2 = (SimpleV2) jsonMigraine.deserialize(serialized);

        assertThat("should not change unrelated fields", v2.unmodified, is(v1.unmodified));
        assertThat("should migrate values of renamed fields", v2.newField, is(v1.oldField));
    }


    @Upgradeable(SimpleUpgraderV1.class)
    static class SimpleV1 {
        public String unmodified;
        public String oldField;
    }

    @Upgradeable(SimpleUpgraderV2.class)
    static class SimpleV2 {
        public String unmodified;
        public String newField;
    }

    static class SimpleUpgraderV1 implements Upgrader {

        @Override
        public int version() {
            return 1;
        }

        @Override
        public void upgrade(ObjectNode data, int version) {
            throw new AssertionError("cannot upgrade initial version");
        }
    }

    static class SimpleUpgraderV2 implements Upgrader {

        @Override
        public int version() {
            return 2;
        }

        @Override
        public void upgrade(ObjectNode data, int version) {
            if (version == 1) {
                Refactor.renameField(data, "oldField", "newField");
            }
        }
    }
}
