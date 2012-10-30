// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import java.util.*;

public class UpgradeStep {

    private final Class<?> dataType;
    private final Upgrader upgrader;
    private final List<String> path;

    public UpgradeStep(Class<?> dataType, Upgrader upgrader, String... path) {
        this.dataType = dataType;
        this.upgrader = upgrader;
        this.path = Arrays.asList(path);
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public Upgrader getUpgrader() {
        return upgrader;
    }

    public List<String> getPath() {
        return path;
    }
}
