// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RefactorTest {

    @Test
    public void rename_field() {
        int originalValue = 234;
        ObjectNode data = JsonFactory.object("oldName", originalValue);

        Refactor.renameField(data, "oldName", "newName");

        assertThat("removes old field", data.get("oldName"), is(nullValue()));
        assertThat("adds new field", data.has("newName"));
        assertThat("keeps the original value", data.get("newName").getIntValue(), is(originalValue));
    }
}
