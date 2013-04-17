// Copyright © 2012-2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package examples.v1;

import fi.solita.jsonmigraine.api.ObjectUpgrader;
import org.codehaus.jackson.node.ObjectNode;

public class FooUpgrader extends ObjectUpgrader {

    @Override
    public int version() {
        return 1;
    }

    @Override
    public ObjectNode upgrade(ObjectNode data, int version) {
        return data;
    }
}
