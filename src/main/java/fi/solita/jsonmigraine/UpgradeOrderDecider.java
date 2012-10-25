// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.node.ObjectNode;

public class UpgradeOrderDecider {

    private final UpgraderInvoker invoker;
    private final UpgraderFactory upgraderFactory = new UpgraderFactory();

    public UpgradeOrderDecider(UpgraderInvoker invoker) {
        this.invoker = invoker;
    }

    public void upgrade(ObjectNode data, int dataVersion, Class<?> dataType) {
        for (Upgrader upgrader : upgraderFactory.getUpgraders(dataType)) {
            upgrade(data, upgrader, dataVersion);
        }
    }

    public void upgrade(ObjectNode data, DataVersion from, HowToUpgrade how) {
        upgrade(data, how.upgrader, from.dataVersion);
    }

    private void upgrade(ObjectNode data, Upgrader upgrader, int dataVersion) {
        int latestVersion = upgrader.version();
        if (dataVersion > latestVersion) {
            throw new IllegalArgumentException("The data is newer than the upgrader. " +
                    "The data had version " + dataVersion + ", but the upgrader had version " + latestVersion + ".");
        }
        while (dataVersion < latestVersion) {
            invoker.upgrade(data, dataVersion, upgrader);
            dataVersion++;
        }
    }
}
