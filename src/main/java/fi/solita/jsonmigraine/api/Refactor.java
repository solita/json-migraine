// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine.api;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

public class Refactor {

    public static void renameField(ObjectNode data, String oldName, String newName) {
        JsonNode value = data.remove(oldName);
        data.put(newName, value);
    }
}
