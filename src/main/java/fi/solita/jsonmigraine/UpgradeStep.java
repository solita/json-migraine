// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

public class UpgradeStep {

    private final Class<?> dataType;
    private final Upgrader upgrader;

    public UpgradeStep(Class<?> dataType, Upgrader upgrader) {
        this.dataType = dataType;
        this.upgrader = upgrader;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public Upgrader getUpgrader() {
        return upgrader;
    }
}
