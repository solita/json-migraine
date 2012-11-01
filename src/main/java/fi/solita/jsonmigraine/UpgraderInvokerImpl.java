// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.*;

public class UpgraderInvokerImpl implements UpgraderInvoker {

    @Override
    public JsonNode upgrade(JsonNode data, int dataVersion, Upgrader upgrader) {
        return upgrader.upgrade(data, dataVersion);
    }

    @Override
    public ObjectNode upgradeField(ObjectNode container, String fieldName, int dataVersion, Upgrader upgrader) {
        JsonNode original = container.get(fieldName);
        try {
            JsonNode upgraded = upgrader.upgrade(original, dataVersion);
            container.put(fieldName, upgraded);
        } catch (ValueRemovedException e) {
            container.remove(fieldName);
        }
        return container;
    }

    @Override
    public JsonNode upgradeArrayField(ObjectNode container, String fieldName, int dataVersion, Upgrader upgrader) {
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
                JsonNode upgraded = upgrader.upgrade(original, dataVersion);
                results.add(upgraded);
            } catch (ValueRemovedException e) {
                // removed; don't add to results
            }
        }
        return results;
    }
}
