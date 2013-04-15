// Copyright © 2012-2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import fi.solita.jsonmigraine.api.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RemovingClassesTest {

    private final TypeRenames renames = new TypeRenames();
    private final JsonMigraine jsonMigraine = new JsonMigraine(new ObjectMapper(), renames);

    {
        renames.rename(ChildV1.class.getName(), ChildV2.class.getName());
        renames.rename(ParentV1.class.getName(), ParentV1.class.getName().replace("V1", "V2")); // class removed in V2
    }

    @Test
    public void can_remove_classes_from_the_class_hierarchy() throws Exception {
        ChildV1 v1 = new ChildV1();
        v1.unmodified = "foo";
        v1.parentField = "bar";

        ChildV2 v2 = upgrade(v1);

        assertThat("should not change unrelated fields", v2.unmodified, is(v1.unmodified));
        assertThat("should migrate values of relocated fields", v2.relocatedField, is(v1.parentField));
    }

    private ChildV2 upgrade(ChildV1 v1) throws Exception {
        String serialized = jsonMigraine.serialize(v1);
        return (ChildV2) jsonMigraine.deserialize(serialized);
    }


    @Upgradeable(ParentUpgraderV1.class)
    static class ParentV1 {
        public String parentField;
    }


    @Upgradeable(ChildUpgraderV1.class)
    static class ChildV1 extends ParentV1 {
        public String unmodified;
    }

    @Upgradeable(ChildUpgraderV2.class)
    static class ChildV2 {
        public String relocatedField;
        public String unmodified;
    }

    static class ParentUpgraderV1 implements Upgrader {

        @Override
        public int version() {
            return 1;
        }

        @Override
        public JsonNode upgrade(JsonNode data, int version) {
            return data;
        }
    }

    static class ChildUpgraderV1 implements Upgrader {

        @Override
        public int version() {
            return 1;
        }

        @Override
        public JsonNode upgrade(JsonNode data, int version) {
            return data;
        }
    }

    static class ChildUpgraderV2 extends ObjectUpgrader {

        @Override
        public int version() {
            return 2;
        }

        @Override
        public ObjectNode upgrade(ObjectNode data, int version) {
            if (version == 1) {
                Refactor.renameField(data, "parentField", "relocatedField");
            }
            return data;
        }
    }
}
