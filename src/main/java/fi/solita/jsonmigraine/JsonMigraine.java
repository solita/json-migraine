// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class JsonMigraine {

    private static final String DATA_FIELD = "data";
    private static final String TYPE_FIELD = "type";

    private final ObjectMapper mapper;
    private final TypeRenames renames;

    public JsonMigraine(ObjectMapper mapper, TypeRenames renames) {
        this.mapper = mapper;
        this.renames = renames;
    }

    public String serialize(Object data) throws Exception {
        ObjectNode meta = mapper.createObjectNode();
        meta.put(DATA_FIELD, mapper.valueToTree(data));
        meta.put(TYPE_FIELD, data.getClass().getName());
        return mapper.writeValueAsString(meta);
    }

    public Object deserialize(String json) throws Exception {
        ObjectNode meta = (ObjectNode) mapper.readTree(json);
        ObjectNode data = (ObjectNode) meta.get(DATA_FIELD);
        String className = meta.get(TYPE_FIELD).asText();

        className = renames.getLatestName(className);

        // TODO: read from json
        int dataVersion = 1;
        Class<?> dataType = Class.forName(className);

        DataVersions from = new DataVersions()
                .add(new DataVersion(dataType, dataVersion));
        HowToUpgrade how = ClassAnalyzer.createUpgradePlan(dataType);
        new UpgradeOrderDecider(new UpgraderInvokerImpl())
                .upgrade(data, from, how);

        return mapper.readValue(data, dataType);
    }
}
