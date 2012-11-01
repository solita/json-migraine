// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

public interface UpgraderInvoker {

    JsonNode upgrade(JsonNode data, int dataVersion, Upgrader upgrader);

    ObjectNode upgradeField(ObjectNode container, String fieldName, int dataVersion, Upgrader upgrader);

    JsonNode upgradeArrayField(ObjectNode container, String fieldName, int dataVersion, Upgrader upgrader);
}
