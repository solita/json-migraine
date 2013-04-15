// Copyright © 2012-2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine.internal;

import fi.solita.jsonmigraine.api.TypeRenames;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.*;

import java.util.*;

public class DataVersions {

    public final List<DataVersion> versions = new ArrayList<DataVersion>();

    public int getVersion(Class<?> dataType) {
        return forDataType(dataType.getName()).dataVersion;
    }

    private DataVersion forDataType(String dataType) {
        for (DataVersion version : versions) {
            if (version.dataType.equals(dataType)) {
                return version;
            }
        }
        throw new IllegalArgumentException("Version information not found for " + dataType);
    }

    public DataVersions add(DataVersion dataVersion) {
        this.versions.add(dataVersion);
        return this;
    }

    public JsonNode toJson() {
        ObjectNode json = JsonNodeFactory.instance.objectNode();
        for (DataVersion version : versions) {
            json.put(version.dataType, version.dataVersion);
        }
        return json;
    }

    public static DataVersions fromJson(JsonNode json, TypeRenames renames) {
        DataVersions versions = new DataVersions();
        for (String dataType : asIterable(json.getFieldNames())) {
            int dataVersion = json.get(dataType).asInt();
            versions.add(new DataVersion(renames.getLatestName(dataType), dataVersion));
        }
        return versions;
    }

    private static <T> Iterable<T> asIterable(final Iterator<T> iterator) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return iterator;
            }
        };
    }
}
