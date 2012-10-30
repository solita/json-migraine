// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import java.lang.reflect.Field;

public class ClassAnalyzer {

    public static HowToUpgrade createUpgradePlan(Class<?> dataType) {
        HowToUpgrade how = new HowToUpgrade();

        for (; dataType != Object.class; dataType = dataType.getSuperclass()) {
            how.addFirst(new UpgradeStep(dataType, new UpgraderFactory().getUpgrader(dataType)));

            for (Field field : dataType.getDeclaredFields()) {
                Class<?> type = field.getType();
                if (type.isAnnotationPresent(Upgradeable.class)) {
                    how.add(new UpgradeStep(type, new UpgraderFactory().getUpgrader(type), field.getName()));
                }
                if (type.isArray() && type.getComponentType().isAnnotationPresent(Upgradeable.class)) {
                    how.add(new UpgradeStep(type, new UpgraderFactory().getUpgrader(type.getComponentType()), field.getName()));
                }
            }
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
