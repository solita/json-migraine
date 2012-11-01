// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import java.lang.reflect.Field;
import java.util.*;

public class ClassAnalyzer {

    private static final AnnotationUpgraderProvider provider = new AnnotationUpgraderProvider();

    public static UpgradePlan createUpgradePlan(Class<?> dataType) {
        UpgradePlan plan = new UpgradePlan();

        for (; dataType != Object.class; dataType = dataType.getSuperclass()) {
            plan.addFirst(new UpgradeStep(dataType));

            for (Field field : dataType.getDeclaredFields()) {
                Class<?> type = field.getType();
                if (provider.isUpgradeable(type)) {
                    plan.add(new UpgradeStep(type, field.getName()));
                }
                if (type.isArray() && provider.isUpgradeable(type.getComponentType())) {
                    plan.add(new UpgradeStep(type, field.getName()));
                }
            }
        }
        return plan;
    }

    public static DataVersions readCurrentVersions(Class<?> dataType) {
        DataVersions versions = new DataVersions();
        for (Class<?> type : getAllTypes(dataType)) {
            if (provider.isUpgradeable(type)) {
                versions.add(new DataVersion(type, provider.getUpgrader(type).version()));
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
