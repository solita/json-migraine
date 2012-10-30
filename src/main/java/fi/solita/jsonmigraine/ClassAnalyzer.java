// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import java.lang.reflect.Field;
import java.util.*;

public class ClassAnalyzer {

    private static final UpgraderFactory upgraderFactory = new UpgraderFactory();

    public static HowToUpgrade createUpgradePlan(Class<?> dataType) {
        HowToUpgrade how = new HowToUpgrade();

        for (; dataType != Object.class; dataType = dataType.getSuperclass()) {
            how.addFirst(new UpgradeStep(dataType, upgraderFactory.getUpgrader(dataType)));

            for (Field field : dataType.getDeclaredFields()) {
                Class<?> type = field.getType();
                if (upgraderFactory.isUpgradeable(type)) {
                    how.add(new UpgradeStep(type, upgraderFactory.getUpgrader(type), field.getName()));
                }
                if (type.isArray() && upgraderFactory.isUpgradeable(type.getComponentType())) {
                    how.add(new UpgradeStep(type, upgraderFactory.getUpgrader(type.getComponentType()), field.getName()));
                }
            }
        }
        return how;
    }

    public static DataVersions readCurrentVersions(Class<?> dataType) {
        DataVersions versions = new DataVersions();
        for (Class<?> type : getAllTypes(dataType)) {
            if (upgraderFactory.isUpgradeable(type)) {
                versions.add(new DataVersion(type, upgraderFactory.getUpgrader(type).version()));
            }
        }
        return versions;
    }

    private static Set<Class<?>> getAllTypes(Class<?> type) {
        Set<Class<?>> results = new HashSet<Class<?>>();
        getAllTypes(results, type);
        return results;
    }

    private static Set<Class<?>> getAllTypes(Set<Class<?>> results, Class<?> type) {
        if (type == null) {
            return results;
        }
        if (results.contains(type)) {
            return results;
        }
        if (type.isArray()) {
            getAllTypes(results, type.getComponentType());
            return results;
        }

        results.add(type);
        getAllTypes(results, type.getSuperclass());
        for (Field field : type.getDeclaredFields()) {
            getAllTypes(results, field.getType());
        }
        return results;
    }
}
