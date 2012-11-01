// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.*;

import java.util.List;

public class UpgradeOrderDecider {

    private final UpgraderInvoker invoker;
    private final UpgraderProvider provider;

    public UpgradeOrderDecider(UpgraderInvoker invoker, UpgraderProvider provider) {
        this.invoker = invoker;
        this.provider = provider;
    }

    public JsonNode upgrade(JsonNode data, DataVersions from, HowToUpgrade how) {
        for (UpgradeStep step : how.steps) {
            Class<?> dataType = step.getDataType();
            List<String> path = step.getPath();
            if (dataType.isArray()) {
                dataType = dataType.getComponentType();
            }
            int dataVersion = from.getVersion(dataType);
            Upgrader upgrader = provider.getUpgrader(dataType);
            if (path.isEmpty()) {
                data = upgrade(data, dataVersion, upgrader);
            } else {
                ObjectNode obj = (ObjectNode) data;
                String fieldName = path.get(0);
                JsonNode original = obj.get(fieldName);

                JsonNode upgraded;
                if (original instanceof ArrayNode) {
                    ArrayNode values = (ArrayNode) original;
                    ArrayNode result = JsonNodeFactory.instance.arrayNode();

                    for (JsonNode value : values) {
                        try {
                            result.add(upgrade(value, dataVersion, upgrader));
                        } catch (ValueRemovedException e) {
                            // removed
                        }
                    }

                    upgraded = result;
                } else {
                    upgraded = upgrade(original, dataVersion, upgrader);
                }

                obj.put(fieldName, upgraded);  // XXX: not tested
                data = obj;
            }
        }
        return data;
    }

    private JsonNode upgrade(JsonNode data, int dataVersion, Upgrader upgrader) {
        int latestVersion = upgrader.version();
        if (dataVersion > latestVersion) {
            throw new IllegalArgumentException("The data is newer than the upgrader. " +
                    "The data had version " + dataVersion + ", but the upgrader had version " + latestVersion + ".");
        }
        while (dataVersion < latestVersion) {
            data = invoker.upgrade(data, dataVersion, upgrader);
            dataVersion++;
        }
        return data;
    }
}
