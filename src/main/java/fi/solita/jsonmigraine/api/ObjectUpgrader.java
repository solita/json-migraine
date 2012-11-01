// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine.api;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

public abstract class ObjectUpgrader implements Upgrader {

    @Override
    public final JsonNode upgrade(JsonNode data, int version) {
        return upgrade((ObjectNode) data, version);
    }

    public abstract ObjectNode upgrade(ObjectNode data, int version);
}
