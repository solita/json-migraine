// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.node.ObjectNode;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.util.Arrays;

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

    @Test
    public void upgradeable_fields_are_upgraded() {
        HowToUpgrade how = ClassAnalyzer.createUpgradePlan(ValueWrapper.class);

        assertThat(how.steps, hasItem(step(Value.class, ValueUpgrader.class, "fieldName")));
    }

    @Test
    public void upgradeable_array_fields_are_upgraded() {
        HowToUpgrade how = ClassAnalyzer.createUpgradePlan(ValueWrapper.class);

        assertThat(how.steps, hasItem(step(Value[].class, ValueUpgrader.class, "arrayFieldName")));
    }

    private static Matcher step(Class<?> dataType, Class<? extends Upgrader> upgraderType, String... path) {
        return describedAs("UpgradeStep(%0, %1)",
                allOf(hasProperty("dataType", equalTo(dataType)),
                        hasProperty("upgrader", instanceOf(upgraderType)),
                        hasProperty("path", equalTo(Arrays.asList(path)))),
                dataType.getSimpleName(), upgraderType.getSimpleName());
    }


    @Upgradeable(ParentUpgrader.class)
    private static class Parent {
    }

    @Upgradeable(ChildUpgrader.class)
    private static class Child extends Parent {
    }

    @Upgradeable(ValueWrapperUpgrader.class)
    private static class ValueWrapper {
        private Value fieldName;
        private Value[] arrayFieldName;
    }

    @Upgradeable(ValueUpgrader.class)
    private static class Value {
    }

    private static class ParentUpgrader extends DummyUpgrader {
    }

    private static class ChildUpgrader extends DummyUpgrader {
    }

    private static class ValueWrapperUpgrader extends DummyUpgrader {
    }

    private static class ValueUpgrader extends DummyUpgrader {
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
