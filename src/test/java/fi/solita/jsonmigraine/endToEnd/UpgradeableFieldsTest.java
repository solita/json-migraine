// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine.endToEnd;

import fi.solita.jsonmigraine.*;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UpgradeableFieldsTest {

    private final TypeRenames renames = new TypeRenames();
    private final JsonMigraine jsonMigraine = new JsonMigraine(new ObjectMapper(), renames);

    {
        renames.rename(EnumV1.class.getName(), EnumV2.class.getName());
        renames.rename(WrapperV1a.class.getName(), WrapperV1b.class.getName());
    }

    @Ignore // TODO
    @Test
    public void upgrades_upgradeable_fields() throws Exception {
        WrapperV1a v1 = new WrapperV1a();
        v1.field = new EnumV1[]{EnumV1.FOO, EnumV1.BAR};

        String serialized = jsonMigraine.serialize(v1);
        WrapperV1b v2 = (WrapperV1b) jsonMigraine.deserialize(serialized);

        assertThat(v2.field, is(arrayContaining(EnumV2.FOO)));
    }


    @Upgradeable(EnumUpgraderV1.class)
    enum EnumV1 {
        FOO, BAR
    }

    @Upgradeable(EnumUpgraderV2.class)
    enum EnumV2 {
        FOO
    }

    @Upgradeable(WrapperUpgraderV1.class)
    static class WrapperV1a {
        public EnumV1[] field;
    }

    @Upgradeable(WrapperUpgraderV1.class)
    static class WrapperV1b {
        public EnumV2[] field;
    }

    static class EnumUpgraderV1 implements Upgrader {

        @Override
        public int version() {
            return 1;
        }

        @Override
        public void upgrade(ObjectNode data, int version) {
        }
    }

    static class EnumUpgraderV2 implements Upgrader {

        @Override
        public int version() {
            return 2;
        }

        @Override
        public void upgrade(ObjectNode data, int version) {
            // TODO
        }
    }

    static class WrapperUpgraderV1 implements Upgrader {

        @Override
        public int version() {
            return 1;
        }

        @Override
        public void upgrade(ObjectNode data, int version) {
        }
    }
}