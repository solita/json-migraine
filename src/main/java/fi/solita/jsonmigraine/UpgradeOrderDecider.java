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
                String fieldName = path.get(0);
                data = upgradeField((ObjectNode) data, dataVersion, upgrader, fieldName);
            }
        }
        return data;
    }

    private ObjectNode upgradeField(ObjectNode container, int dataVersion, Upgrader upgrader, String fieldName) {
        JsonNode original = container.get(fieldName);
        try {

            JsonNode upgraded;
            if (original instanceof ArrayNode) {
                upgraded = upgradeArray((ArrayNode) original, dataVersion, upgrader);
            } else {
                upgraded = upgrade(original, dataVersion, upgrader); // XXX: not tested
            }

            container.put(fieldName, upgraded);  // XXX: not tested
        } catch (ValueRemovedException e) {
            container.remove(fieldName);
        }
        return container;
    }

    private ArrayNode upgradeArray(ArrayNode values, int dataVersion, Upgrader upgrader) {
        ArrayNode results = JsonNodeFactory.instance.arrayNode();
        for (JsonNode value : values) {
            try {
                JsonNode upgraded = upgrade(value, dataVersion, upgrader);
                results.add(upgraded);
            } catch (ValueRemovedException e) {
                // removed
            }
        }
        return results;
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
