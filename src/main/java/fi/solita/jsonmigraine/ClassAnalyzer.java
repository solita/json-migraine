// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

public class ClassAnalyzer {

    public static HowToUpgrade createUpgradePlan(Class<?> dataType) {
        HowToUpgrade how = new HowToUpgrade();
        for (; dataType != Object.class; dataType = dataType.getSuperclass()) {
            Upgrader upgrader = new UpgraderFactory().getUpgrader(dataType);
            how.addFirst(new UpgradeStep(dataType, upgrader));
        }
        return how;
    }

    public static DataVersions readCurrentVersions(Class<?> dataType) {
        DataVersions versions = new DataVersions();
        for (; dataType != Object.class; dataType = dataType.getSuperclass()) {
            versions.add(new DataVersion(dataType, getVersion(dataType)));
        }
        return versions;
    }

    private static int getVersion(Class<?> cl) {
        Upgrader upgrader = new UpgraderFactory().getUpgrader(cl);
        return upgrader.version();
    }
}
