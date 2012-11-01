// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import java.util.List;

public class UpgradePlanExecutor {

    private final UpgraderInvoker invoker;
    private final UpgraderProvider provider;

    public UpgradePlanExecutor(UpgraderInvoker invoker, UpgraderProvider provider) {
        this.invoker = invoker;
        this.provider = provider;
    }

    public JsonNode upgrade(JsonNode data, DataVersions versions, UpgradePlan plan) {
        for (UpgradeStep step : plan.steps) {
            List<String> path = step.getPath();
            String fieldName = path.isEmpty() ? null : path.get(0);
            Class<?> dataType = step.getDataType();
            Class<?> upgraderType = dataType.isArray() ? dataType.getComponentType() : dataType;
            int dataVersion = versions.getVersion(upgraderType);
            Upgrader upgrader = provider.getUpgrader(upgraderType);

            int latestVersion = upgrader.version();
            if (dataVersion > latestVersion) {
                throw new IllegalArgumentException("The data is newer than the upgrader. " +
                        "The data had version " + dataVersion + ", but the upgrader had version " + latestVersion + ".");
            }
            while (dataVersion < latestVersion) {

                if (fieldName == null) {
                    data = invoker.upgrade(data, dataVersion, upgrader);
                } else if (dataType.isArray()) {
                    data = invoker.upgradeArrayField((ObjectNode) data, fieldName, dataVersion, upgrader);
                } else {
                    data = invoker.upgradeField((ObjectNode) data, fieldName, dataVersion, upgrader);
                }

                dataVersion++;
            }
        }
        return data;
    }
}
