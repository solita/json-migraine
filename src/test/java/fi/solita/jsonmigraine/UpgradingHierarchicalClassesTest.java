// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.mockito.InOrder;

import static fi.solita.jsonmigraine.JsonFactory.unimportantObject;
import static org.mockito.Mockito.*;

public class UpgradingHierarchicalClassesTest {

    private static final int PARENT_VERSION = 10;
    private static final int CHILD_VERSION = 20;

    private final UpgraderInvoker invoker = mock(UpgraderInvoker.class);
    private final InOrder inOrder = inOrder(invoker);

    private final UpgradeOrderDecider sut = new UpgradeOrderDecider(invoker);

    private final ObjectNode data = unimportantObject();
    private int parentVersion;
    private int childVersion;
    private final ParentUpgrader parentUpgrader = new ParentUpgrader();
    private final ChildUpgrader childUpgrader = new ChildUpgrader();

    @Test
    public void uses_each_upgrader_in_the_class_hierarchy() { // XXX: not really testing the hierarchy, just that all steps are executed
        parentVersion = PARENT_VERSION - 1;
        childVersion = CHILD_VERSION - 1;

        upgrade();

        verify(invoker).upgrade(data, PARENT_VERSION - 1, parentUpgrader);
        verify(invoker).upgrade(data, CHILD_VERSION - 1, childUpgrader);
        verifyNoMoreInteractions(invoker);
    }

    // TODO: upgrades_the_parent_class_fully_before_the_child_class


    private void upgrade() {
        // TODO: test that the right order is generated
        DataVersions from = new DataVersions()
                .add(new DataVersion(Parent.class, parentVersion))
                .add(new DataVersion(Child.class, childVersion));
        HowToUpgrade how = new HowToUpgrade()
                .add(new UpgradeStep(Parent.class, parentUpgrader))
                .add(new UpgradeStep(Child.class, childUpgrader));
        sut.upgrade(data, from, how);
    }

    @Upgradeable(ParentUpgrader.class)
    private static class Parent {
    }

    @Upgradeable(ChildUpgrader.class)
    private static class Child extends Parent {
    }

    private static abstract class DummyUpgrader implements Upgrader {
        @Override
        public void upgrade(ObjectNode data, int dataVersion) {
        }
    }

    private static class ParentUpgrader extends DummyUpgrader {
        @Override
        public int version() {
            return PARENT_VERSION;
        }
    }

    private static class ChildUpgrader extends DummyUpgrader {
        @Override
        public int version() {
            return CHILD_VERSION;
        }
    }
}
