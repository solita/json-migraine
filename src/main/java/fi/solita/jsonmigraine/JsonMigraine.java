// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class JsonMigraine {

    private static final String DATA_FIELD = "data";
    private static final String TYPE_FIELD = "type";
    private static final String VERSIONS_FIELD = "versions";

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
        meta.put(VERSIONS_FIELD, ClassAnalyzer.readCurrentVersions(data.getClass()).toJson());
        return mapper.writeValueAsString(meta);
    }

    public Object deserialize(String json) throws Exception {
        ObjectNode meta = (ObjectNode) mapper.readTree(json);

        ObjectNode data = (ObjectNode) meta.get(DATA_FIELD);
        Class<?> type = Class.forName(renames.getLatestName(meta.get(TYPE_FIELD).asText()));
        DataVersions versions = DataVersions.fromJson(meta.get(VERSIONS_FIELD), renames);

        upgrade(data, type, versions);
        return mapper.readValue(data, type);
    }

    private void upgrade(ObjectNode data, Class<?> type, DataVersions versions) {
        UpgraderInvoker invoker = new UpgraderInvokerImpl();
        UpgraderProvider provider = new AnnotationUpgraderProvider();
        UpgradeOrderDecider decider = new UpgradeOrderDecider(invoker, provider);

        decider.upgrade(data, versions, ClassAnalyzer.createUpgradePlan(type));
    }
}
