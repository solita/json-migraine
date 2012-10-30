// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import java.util.*;

public class UpgradeStep {

    private final Class<?> dataType;
    private final List<String> path;

    public UpgradeStep(Class<?> dataType, String... path) {
        this.dataType = dataType;
        this.path = Arrays.asList(path);
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public List<String> getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "UpgradeStep{" +
                "dataType=" + dataType +
                ", path=" + path +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UpgradeStep)) {
            return false;
        }
        UpgradeStep that = (UpgradeStep) obj;
        return dataType.equals(that.dataType) && path.equals(that.path);
    }

    @Override
    public int hashCode() {
        return dataType.hashCode() + path.hashCode();
    }
}
