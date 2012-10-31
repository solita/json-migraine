// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SuppressWarnings("unchecked")
public class CreatingUpgradePlanTest {

    @Test
    public void finds_out_which_upgrader_to_use_for_a_class() {
        HowToUpgrade how = ClassAnalyzer.createUpgradePlan(Parent.class);

        assertThat(how.steps, contains(new UpgradeStep(Parent.class)));
    }

    @Test
    public void parent_classes_are_upgraded_first() {
        HowToUpgrade how = ClassAnalyzer.createUpgradePlan(Child.class);

        assertThat(how.steps, contains(new UpgradeStep(Parent.class), new UpgradeStep(Child.class)));
    }

    @Test
    public void upgradeable_fields_are_upgraded() {
        HowToUpgrade how = ClassAnalyzer.createUpgradePlan(ValueWrapper.class);

        assertThat(how.steps, hasItem(new UpgradeStep(Value.class, "fieldName")));
    }

    @Test
    public void upgradeable_array_fields_are_upgraded() {
        HowToUpgrade how = ClassAnalyzer.createUpgradePlan(ValueWrapper.class);

        assertThat(how.steps, hasItem(new UpgradeStep(Value[].class, "arrayFieldName")));
    }


    @Upgradeable(ParentUpgrader.class)
    private static class Parent {
    }

    @Upgradeable(ChildUpgrader.class)
    private static class Child extends Parent {
    }

    @SuppressWarnings("UnusedDeclaration")
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
        public JsonNode upgrade(JsonNode data, int version) {
            return data;
        }
    }
}
