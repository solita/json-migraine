// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class JsonMigraine {

    private final ObjectMapper mapper;

    public JsonMigraine(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String serialize(Object data) throws Exception {
        return mapper.writeValueAsString(data);
    }

    public <T> T deserialize(String serializedData, Class<T> dataType) throws Exception {
        ObjectNode data = (ObjectNode) mapper.readTree(serializedData);

        DataVersions from = new DataVersions()
                .add(new DataVersion(Class.forName("fi.solita.jsonmigraine.endToEnd.SimpleV2"), 1)); // TODO
        HowToUpgrade how = new ClassAnalyzer().createUpgradePlan(dataType);
        new UpgradeOrderDecider(new UpgraderInvokerImpl()).upgrade(data, from, how);

        return mapper.readValue(data, dataType);
    }
}
