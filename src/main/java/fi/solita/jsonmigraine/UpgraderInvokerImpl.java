// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.node.ObjectNode;

public class UpgraderInvokerImpl implements UpgraderInvoker {

    @Override
    public void upgrade(ObjectNode data, int dataVersion, Upgrader upgrader) {
        upgrader.upgrade(data, dataVersion);
    }
}
