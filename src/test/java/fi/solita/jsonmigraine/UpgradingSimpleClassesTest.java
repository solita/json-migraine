// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.mockito.InOrder;

import static fi.solita.jsonmigraine.JsonFactory.unimportantObject;
import static org.mockito.Mockito.*;

public class UpgradingSimpleClassesTest {

    private static final int LATEST_VERSION = 10;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final UpgraderInvoker invoker = mock(UpgraderInvoker.class);
    private final InOrder inOrder = inOrder(invoker);

    private final UpgradeOrderDecider sut = new UpgradeOrderDecider(invoker);

    private final DummyUpgrader upgrader = new DummyUpgrader();
    private final ObjectNode data = unimportantObject();
    private int dataVersion;

    @Test
    public void upgrades_old_versions_using_the_upgrader() {
        dataVersion = LATEST_VERSION - 1;

        upgrade();

        inOrder.verify(invoker).upgrade(data, dataVersion, upgrader);
        verifyNoMoreInteractions(invoker);
    }

    @Test
    public void upgrades_one_version_at_a_time_until_fully_upgraded() {
        dataVersion = LATEST_VERSION - 3;

        upgrade();

        inOrder.verify(invoker).upgrade(data, LATEST_VERSION - 3, upgrader);
        inOrder.verify(invoker).upgrade(data, LATEST_VERSION - 2, upgrader);
        inOrder.verify(invoker).upgrade(data, LATEST_VERSION - 1, upgrader);
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
                .add(new DataVersion(DummyEntity.class, dataVersion));
        HowToUpgrade how = new HowToUpgrade()
                .add(new UpgradeStep(DummyEntity.class, upgrader));
        sut.upgrade(data, from, how);
    }

    private static class DummyEntity {
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
