// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import java.util.List;

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
            List<String> path = step.getPath();
            if (dataType.isArray()) {
                dataType = dataType.getComponentType();
            }
            int dataVersion = from.getVersion(dataType);
            Upgrader upgrader = provider.getUpgrader(dataType);
            if (path.isEmpty()) {
                upgrade(data, dataVersion, upgrader);
            } else {
                upgrade(data.get(path.get(0)), dataVersion, upgrader);
            }
        }
    }

    private void upgrade(JsonNode data, int dataVersion, Upgrader upgrader) {
        int latestVersion = upgrader.version();
        if (dataVersion > latestVersion) {
            throw new IllegalArgumentException("The data is newer than the upgrader. " +
                    "The data had version " + dataVersion + ", but the upgrader had version " + latestVersion + ".");
        }
        while (dataVersion < latestVersion) {
            // TODO: use the returned value
            invoker.upgrade(data, dataVersion, upgrader);
            dataVersion++;
        }
    }
}
