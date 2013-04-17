// Copyright © 2012-2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package examples.v3;

import examples.v2.Foo;
import fi.solita.jsonmigraine.JsonMigraine;
import fi.solita.jsonmigraine.api.TypeRenames;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class ExamplesV3Test {

    @Test
    public void rename() throws Exception {
        Foo original = new Foo();
        original.newField = "abc";

        TypeRenames renames = new TypeRenames();
        renames.rename("examples.v2.Foo", "examples.v3.Bar");
        JsonMigraine jsonMigraine = new JsonMigraine(new ObjectMapper(), renames);

        String serialized = jsonMigraine.serialize(original);
        Bar upgraded = (Bar) jsonMigraine.deserialize(serialized);

        assertThat(upgraded.newField, is("abc"));
    }
}
