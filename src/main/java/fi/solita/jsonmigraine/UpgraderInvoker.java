// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.JsonNode;

public interface UpgraderInvoker {

    void upgrade(JsonNode data, int dataVersion, Upgrader upgrader);
}
