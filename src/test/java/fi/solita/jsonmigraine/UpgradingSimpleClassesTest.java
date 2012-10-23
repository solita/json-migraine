// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.*;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.*;

import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.ANY;
import static org.codehaus.jackson.map.SerializationConfig.Feature.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class UpgradingSimpleClassesTest {

    private static final int LATEST_VERSION = 10;
    private static final ObjectNode unusedJson = JsonNodeFactory.instance.objectNode();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final ObjectMapper mapper;

    public UpgradingSimpleClassesTest() {
        mapper = new ObjectMapper();
        mapper.setVisibilityChecker(mapper.getVisibilityChecker()
                .withFieldVisibility(ANY));
        mapper.setSerializationConfig(mapper.getSerializationConfig()
                .without(FAIL_ON_EMPTY_BEANS)
                .with(AUTO_DETECT_FIELDS)
                .without(AUTO_DETECT_GETTERS)
                .without(AUTO_DETECT_IS_GETTERS));
    }

    @Test
    public void upgrades_old_versions_using_the_upgrader() {
        int oldVersion = LATEST_VERSION - 1;
        ObjectNode data = object("oldField", 123);

        upgrade(data, oldVersion, new DummyUpgrader() {
            @Override
            public void upgrade(ObjectNode data, int dataVersion) {
                JsonNode value = data.get("oldField");
                data.remove("oldField");
                data.put("newField", value);
            }
        });

        assertThat(data, is(object("newField", 123)));
    }

    @Test
    public void upgrades_one_version_at_a_time_until_fully_upgraded() {
        final List<Integer> versionSpy = new ArrayList<Integer>();
        int oldVersion = LATEST_VERSION - 3;

        upgrade(unusedJson, oldVersion, new DummyUpgrader() {
            @Override
            public void upgrade(ObjectNode data, int dataVersion) {
                versionSpy.add(dataVersion);
            }
        });

        assertThat(versionSpy, contains(LATEST_VERSION - 3, LATEST_VERSION - 2, LATEST_VERSION - 1));
    }

    @Test
    public void does_nothing_when_already_at_latest_version() {
        int latestVersion = LATEST_VERSION;
        ObjectNode data = object("oldField", 1);

        upgrade(data, latestVersion, new DummyUpgrader() {
            @Override
            public void upgrade(ObjectNode data, int dataVersion) {
                fail("should not be called");
            }
        });

        assertThat(data, is(object("oldField", 1)));
    }

    @Test
    public void fails_if_the_data_version_is_newer_than_the_upgrader_version() {
        int tooNewVersion = LATEST_VERSION + 1;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("data had version 11");
        thrown.expectMessage("upgrader had version 10");
        upgrade(unusedJson, tooNewVersion, new DummyUpgrader());
    }


    private void upgrade(ObjectNode data, int dataVersion, Upgrader upgrader) {
        int latestVersion = upgrader.version();
        if (dataVersion > latestVersion) {
            throw new IllegalArgumentException("The data is newer than the upgrader. " +
                    "The data had version " + dataVersion + ", but the upgrader had version " + latestVersion + ".");
        }
        while (dataVersion < latestVersion) {
            upgrader.upgrade(data, dataVersion);
            dataVersion++;
        }
    }

    private static ObjectNode object(String fieldName, int value) {
        ObjectNode data = JsonNodeFactory.instance.objectNode();
        data.put(fieldName, value);
        return data;
    }

    private static class DummyUpgrader implements Upgrader {

        @Override
        public int version() {
            return LATEST_VERSION;
        }

        @Override
        public void upgrade(ObjectNode data, int dataVersion) {
        }
    }
}
