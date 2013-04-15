// Copyright © 2012-2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine.internal;

public class DataVersion {

    public final String dataType;
    public final int dataVersion;

    public DataVersion(Class<?> dataType, int dataVersion) {
        this(dataType.getName(), dataVersion);
    }

    public DataVersion(String dataType, int dataVersion) {
        this.dataType = dataType;
        this.dataVersion = dataVersion;
    }
}
