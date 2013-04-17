// Copyright © 2012-2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package examples.v2;

import fi.solita.jsonmigraine.api.*;
import org.codehaus.jackson.node.ObjectNode;

public class FooUpgrader extends ObjectUpgrader {

    @Override
    public int version() {
        return 2;
    }

    @Override
    public ObjectNode upgrade(ObjectNode data, int version) {
        if (version == 1) {
            Refactor.renameField(data, "oldField", "newField");
        }
        return data;
    }
}
