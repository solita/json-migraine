// Copyright © 2012-2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package examples.v2;

import fi.solita.jsonmigraine.JsonMigraine;
import fi.solita.jsonmigraine.api.TypeRenames;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ExamplesV2Test {

    @Test
    public void upgrade() throws Exception {
        examples.v1.Foo original = new examples.v1.Foo();
        original.oldField = "abc";

        TypeRenames renames = new TypeRenames();
        renames.rename("examples.v1.Foo", "examples.v2.Foo");
        JsonMigraine jsonMigraine = new JsonMigraine(new ObjectMapper(), renames);

        String serialized = jsonMigraine.serialize(original);
        examples.v2.Foo upgraded = (examples.v2.Foo) jsonMigraine.deserialize(serialized);

        assertThat(upgraded.newField, is("abc"));
    }
}
