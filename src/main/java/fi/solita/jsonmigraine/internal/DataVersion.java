// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine.internal;

public class DataVersion {

    public final Class<?> dataType;
    public final int dataVersion;

    public DataVersion(Class<?> dataType, int dataVersion) {
        this.dataType = dataType;
        this.dataVersion = dataVersion;
    }
}
