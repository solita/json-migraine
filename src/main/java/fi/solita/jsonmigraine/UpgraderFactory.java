// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import java.lang.annotation.Annotation;

public class UpgraderFactory {

    public Upgrader getUpgrader(Class<?> type) {
        Class<? extends Upgrader> upgrader = getRequiredAnnotation(type, Upgradeable.class).value();
        try {
            return upgrader.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static <T extends Annotation> T getRequiredAnnotation(Class<?> type, Class<T> annotationClass) {
        T annotation = type.getAnnotation(annotationClass);
        if (annotation == null) {
            throw new IllegalArgumentException(
                    "Expected " + type + " to be annotated with " + annotationClass + " but the annotation was missing");
        }
        return annotation;
    }
}
