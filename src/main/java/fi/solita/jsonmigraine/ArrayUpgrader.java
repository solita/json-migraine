// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;

public abstract class ArrayUpgrader implements Upgrader {

    @Override
    public final JsonNode upgrade(JsonNode data, int version) {
        if (data instanceof ArrayNode) {
            return upgrade((ArrayNode) data, version);
        } else {
            throw new IllegalArgumentException("Expected an array, but got " + data);
        }
    }

    public abstract ArrayNode upgrade(ArrayNode data, int version);
}
