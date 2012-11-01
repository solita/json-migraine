// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import fi.solita.jsonmigraine.api.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UpgradeableFieldsTest {

    private final TypeRenames renames = new TypeRenames();
    private final JsonMigraine jsonMigraine = new JsonMigraine(new ObjectMapper(), renames);

    {
        renames.rename(EnumV1.class.getName(), EnumV2.class.getName());
        renames.rename(WrapperV1a.class.getName(), WrapperV1b.class.getName());
    }

    @Test
    public void can_remove_values_from_array_fields() throws Exception {
        WrapperV1a v1 = new WrapperV1a();
        v1.values = new EnumV1[]{EnumV1.BAR};

        WrapperV1b v2 = upgrade(v1);

        assertThat(v2.values, is(emptyArray()));
    }

    @Test
    public void can_remove_values_from_regular_fields() throws Exception {
        WrapperV1a v1 = new WrapperV1a();
        v1.value = EnumV1.BAR;

        WrapperV1b v2 = upgrade(v1);

        assertThat(v2.value, is(nullValue()));
    }

    @Test
    public void can_rename_values_in_array_fields() throws Exception {
        WrapperV1a v1 = new WrapperV1a();
        v1.values = new EnumV1[]{EnumV1.FOO};

        WrapperV1b v2 = upgrade(v1);

        assertThat(v2.values, is(arrayContaining(EnumV2.FOO_RENAMED)));
    }

    @Test
    public void can_rename_values_in_regular_fields() throws Exception {
        WrapperV1a v1 = new WrapperV1a();
        v1.value = EnumV1.FOO;

        WrapperV1b v2 = upgrade(v1);

        assertThat(v2.value, is(EnumV2.FOO_RENAMED));
    }

    private WrapperV1b upgrade(WrapperV1a v1) throws Exception {
        String serialized = jsonMigraine.serialize(v1);
        return (WrapperV1b) jsonMigraine.deserialize(serialized);
    }


    @Upgradeable(EnumUpgraderV1.class)
    enum EnumV1 {
        FOO, BAR
    }

    @Upgradeable(EnumUpgraderV2.class)
    enum EnumV2 {
        FOO_RENAMED
    }

    @Upgradeable(WrapperUpgraderV1.class)
    static class WrapperV1a {
        public EnumV1[] values;
        public EnumV1 value;
    }

    @Upgradeable(WrapperUpgraderV1.class)
    static class WrapperV1b {
        public EnumV2[] values;
        public EnumV2 value;
    }

    static class EnumUpgraderV1 implements Upgrader {

        @Override
        public int version() {
            return 1;
        }

        @Override
        public JsonNode upgrade(JsonNode data, int version) {
            return data;
        }
    }

    static class EnumUpgraderV2 implements Upgrader {

        @Override
        public int version() {
            return 2;
        }

        @Override
        public JsonNode upgrade(JsonNode data, int version) {
            if (version == 1) {
                if (data.asText().equals("BAR")) {
                    throw new ValueRemovedException();
                }
                if (data.asText().equals("FOO")) {
                    return JsonNodeFactory.instance.textNode("FOO_RENAMED");
                }
            }
            return data;
        }
    }

    static class WrapperUpgraderV1 implements Upgrader {

        @Override
        public int version() {
            return 1;
        }

        @Override
        public JsonNode upgrade(JsonNode data, int version) {
            return data;
        }
    }
}
