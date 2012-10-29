// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.node.ObjectNode;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("unchecked")
public class CreatingUpgradePlanTest {

    @Test
    public void finds_out_which_upgrader_to_use_for_a_class() {
        HowToUpgrade how = ClassAnalyzer.createUpgradePlan(Parent.class);

        assertThat(how.steps, contains(
                step(Parent.class, ParentUpgrader.class)));
    }

    @Test
    public void parent_classes_are_upgraded_first() {
        HowToUpgrade how = ClassAnalyzer.createUpgradePlan(Child.class);

        assertThat(how.steps, contains(
                step(Parent.class, ParentUpgrader.class),
                step(Child.class, ChildUpgrader.class)));
    }

    private static Matcher<Object> step(Class<?> dataType, Class<? extends Upgrader> upgraderType) {
        return describedAs("UpgradeStep(%0, %1)",
                both(hasProperty("dataType", equalTo(dataType))).and(hasProperty("upgrader", instanceOf(upgraderType))),
                dataType.getSimpleName(), upgraderType.getSimpleName());
    }


    @Upgradeable(ParentUpgrader.class)
    private static class Parent {
    }

    @Upgradeable(ChildUpgrader.class)
    private static class Child extends Parent {
    }

    private static class ParentUpgrader extends DummyUpgrader {
    }

    private static class ChildUpgrader extends DummyUpgrader {
    }

    private static class DummyUpgrader implements Upgrader {

        @Override
        public int version() {
            return 1;
        }

        @Override
        public void upgrade(ObjectNode data, int version) {
        }
    }
}
