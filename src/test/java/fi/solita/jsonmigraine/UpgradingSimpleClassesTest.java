// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.node.*;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.util.*;

import static fi.solita.jsonmigraine.JsonFactory.object;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.fail;

public class UpgradingSimpleClassesTest {

    private static final int LATEST_VERSION = 10;
    private static final ObjectNode unusedJson = JsonNodeFactory.instance.objectNode();

    @Rule
    public final ExpectedException thrown = ExpectedException.none();


    @Test
    public void upgrades_old_versions_using_the_upgrader() {
        int oldVersion = LATEST_VERSION - 1;
        ObjectNode data = object("oldField", 123);

        Foo.upgrade(data, oldVersion, new DummyUpgrader() {
            @Override
            public void upgrade(ObjectNode data, int dataVersion) {
                Refactor.renameField(data, "oldField", "newField");
            }
        });

        assertThat(data, is(object("newField", 123)));
    }

    @Test
    public void upgrades_one_version_at_a_time_until_fully_upgraded() {
        final List<Integer> versionSpy = new ArrayList<Integer>();
        int oldVersion = LATEST_VERSION - 3;

        Foo.upgrade(unusedJson, oldVersion, new DummyUpgrader() {
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

        Foo.upgrade(data, latestVersion, new DummyUpgrader() {
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
        Foo.upgrade(unusedJson, tooNewVersion, new DummyUpgrader());
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
