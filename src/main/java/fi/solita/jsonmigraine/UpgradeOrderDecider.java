// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.node.ObjectNode;

public class UpgradeOrderDecider {

    private final UpgraderInvoker invoker;
    private final UpgraderProvider provider;

    public UpgradeOrderDecider(UpgraderInvoker invoker, UpgraderProvider provider) {
        this.invoker = invoker;
        this.provider = provider;
    }

    public void upgrade(ObjectNode data, DataVersions from, HowToUpgrade how) {
        for (UpgradeStep step : how.steps) {
            Class<?> dataType = step.getDataType();
            int dataVersion = from.getVersion(dataType);
            Upgrader upgrader = provider.getUpgrader(dataType);
            upgrade(data, dataVersion, upgrader);
        }
    }

    private void upgrade(ObjectNode data, int dataVersion, Upgrader upgrader) {
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
