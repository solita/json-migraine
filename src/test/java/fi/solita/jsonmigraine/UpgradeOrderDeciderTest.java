// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.JsonNode;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;

import static fi.solita.jsonmigraine.JsonFactory.unimportantObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class UpgradeOrderDeciderTest {

    private static final int LATEST_VERSION = 10;
    private static final int FIRST_VERSION = 100;
    private static final int SECOND_VERSION = 200;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final UpgraderInvoker invoker = mock(UpgraderInvoker.class);
    private final InOrder inOrder = inOrder(invoker);
    private final StubUpgraderProvider provider = new StubUpgraderProvider();

    private final UpgradeOrderDecider sut = new UpgradeOrderDecider(invoker, provider);

    private final DummyUpgrader upgrader = new DummyUpgrader();
    private JsonNode data = unimportantObject();
    private int dataVersion;

    {
        provider.put(First.class, upgrader);
        provider.put(Second.class, upgrader);
        provider.put(Dummy.class, upgrader);
    }

    @Test
    public void upgrades_old_versions_using_the_upgrader() {
        dataVersion = LATEST_VERSION - 1;

        upgrade();

        inOrder.verify(invoker).upgradeWholeObject(any(JsonNode.class), eq(dataVersion), eq(upgrader));
        verifyNoMoreInteractions(invoker);
    }

    @Test
    public void upgrades_one_version_at_a_time_until_fully_upgraded() {
        dataVersion = LATEST_VERSION - 3;

        upgrade();

        inOrder.verify(invoker).upgradeWholeObject(any(JsonNode.class), eq(LATEST_VERSION - 3), eq(upgrader));
        inOrder.verify(invoker).upgradeWholeObject(any(JsonNode.class), eq(LATEST_VERSION - 2), eq(upgrader));
        inOrder.verify(invoker).upgradeWholeObject(any(JsonNode.class), eq(LATEST_VERSION - 1), eq(upgrader));
        verifyNoMoreInteractions(invoker);
    }

    @Test
    public void does_nothing_when_already_at_latest_version() {
        dataVersion = LATEST_VERSION;

        upgrade();

        verifyNoMoreInteractions(invoker);
    }

    @Test
    public void fails_if_the_data_version_is_newer_than_the_upgrader_version() {
        dataVersion = LATEST_VERSION + 1;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("data had version 11");
        thrown.expectMessage("upgrader had version 10");
        upgrade();
    }

    private void upgrade() {
        DataVersions from = new DataVersions()
                .add(new DataVersion(Dummy.class, dataVersion));
        HowToUpgrade how = new HowToUpgrade()
                .add(new UpgradeStep(Dummy.class));
        data = sut.upgrade(data, from, how);
    }

    @Test
    public void upgrades_the_first_step_fully_before_following_steps() {
        FirstUpgrader firstUpgrader = new FirstUpgrader();
        SecondUpgrader secondUpgrader = new SecondUpgrader();
        provider.put(First.class, firstUpgrader);
        provider.put(Second.class, secondUpgrader);

        DataVersions from = new DataVersions()
                .add(new DataVersion(First.class, FIRST_VERSION - 2))
                .add(new DataVersion(Second.class, SECOND_VERSION - 2));
        HowToUpgrade how = new HowToUpgrade()
                .add(new UpgradeStep(First.class))
                .add(new UpgradeStep(Second.class));
        data = sut.upgrade(data, from, how);

        inOrder.verify(invoker).upgradeWholeObject(any(JsonNode.class), eq(FIRST_VERSION - 2), eq(firstUpgrader));
        inOrder.verify(invoker).upgradeWholeObject(any(JsonNode.class), eq(FIRST_VERSION - 1), eq(firstUpgrader));
        inOrder.verify(invoker).upgradeWholeObject(any(JsonNode.class), eq(SECOND_VERSION - 2), eq(secondUpgrader));
        inOrder.verify(invoker).upgradeWholeObject(any(JsonNode.class), eq(SECOND_VERSION - 1), eq(secondUpgrader));
        verifyNoMoreInteractions(invoker);
    }


    private static class Dummy {
    }

    private static class DummyUpgrader implements Upgrader {

        @Override
        public int version() {
            return LATEST_VERSION;
        }

        @Override
        public JsonNode upgrade(JsonNode data, int dataVersion) {
            return data;
        }
    }

    private static class First {
    }

    private static class Second {
    }

    private static class FirstUpgrader extends DummyUpgrader {
        @Override
        public int version() {
            return FIRST_VERSION;
        }
    }

    private static class SecondUpgrader extends DummyUpgrader {
        @Override
        public int version() {
            return SECOND_VERSION;
        }
    }
}
