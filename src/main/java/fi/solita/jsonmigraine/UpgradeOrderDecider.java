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
            List<String> path = step.getPath();
            String fieldName = path.isEmpty() ? null : path.get(0);
            Class<?> dataType = step.getDataType();
            Class<?> upgraderType = dataType.isArray() ? dataType.getComponentType() : dataType;
            int dataVersion = from.getVersion(upgraderType);
            Upgrader upgrader = provider.getUpgrader(upgraderType);

            int latestVersion = upgrader.version();
            if (dataVersion > latestVersion) {
                throw new IllegalArgumentException("The data is newer than the upgrader. " +
                        "The data had version " + dataVersion + ", but the upgrader had version " + latestVersion + ".");
            }
            while (dataVersion < latestVersion) {

                if (fieldName == null) {
                    data = upgradeWholeObject(data, dataVersion, upgrader);
                } else if (dataType.isArray()) {
                    data = upgradeArrayField((ObjectNode) data, fieldName, dataVersion, upgrader);
                } else {
                    data = upgradeField((ObjectNode) data, fieldName, dataVersion, upgrader);
                }

                dataVersion++;
            }
        }
        return data;
    }

    private ObjectNode upgradeField(ObjectNode container, String fieldName, int dataVersion, Upgrader upgrader) {
        JsonNode original = container.get(fieldName);
        try {
            JsonNode upgraded = upgradeWholeObject(original, dataVersion, upgrader);
            container.put(fieldName, upgraded);
        } catch (ValueRemovedException e) {
            container.remove(fieldName);
        }
        return container;
    }

    private JsonNode upgradeArrayField(ObjectNode container, String fieldName, int dataVersion, Upgrader upgrader) {
        JsonNode original = container.get(fieldName);
        if (!original.isNull()) {
            JsonNode upgraded = upgradeArray((ArrayNode) original, dataVersion, upgrader);
            container.put(fieldName, upgraded);
        }
        return container;
    }

    private ArrayNode upgradeArray(ArrayNode values, int dataVersion, Upgrader upgrader) {
        ArrayNode results = JsonNodeFactory.instance.arrayNode();
        for (JsonNode original : values) {
            try {
                JsonNode upgraded = upgradeWholeObject(original, dataVersion, upgrader);
                results.add(upgraded);
            } catch (ValueRemovedException e) {
                // removed; don't add to results
            }
        }
        return results;
    }

    private JsonNode upgradeWholeObject(JsonNode data, int dataVersion, Upgrader upgrader) {
        return invoker.upgradeWholeObject(data, dataVersion, upgrader);
    }
}
