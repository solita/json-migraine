// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import java.util.*;

public class DataVersions {

    public final List<DataVersion> versions = new ArrayList<DataVersion>();

    public DataVersion forDataType(Class<?> dataType) {
        for (DataVersion version : versions) {
            if (version.dataType.equals(dataType)) {
                return version;
            }
        }
        throw new IllegalArgumentException("not found: " + dataType);
    }

    public DataVersions add(DataVersion dataVersion) {
        this.versions.add(dataVersion);
        return this;
    }
}
