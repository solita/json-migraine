// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine.api;

import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;

import static fi.solita.jsonmigraine.util.JsonFactory.field;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RefactorTest {

    @Test
    public void rename_field() {
        int originalValue = 234;
        ObjectNode data = field("oldName", originalValue);

        Refactor.renameField(data, "oldName", "newName");

        assertThat(data, is(field("newName", originalValue)));
    }
}
