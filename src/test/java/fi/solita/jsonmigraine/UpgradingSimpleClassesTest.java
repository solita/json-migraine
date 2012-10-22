// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.*;
import org.junit.rules.ExpectedException;

import java.lang.annotation.Annotation;

import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.ANY;
import static org.codehaus.jackson.map.SerializationConfig.Feature.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UpgradingSimpleClassesTest {

    private static final int LATEST_VERSION = 10;

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final ObjectMapper mapper;

    public UpgradingSimpleClassesTest() {
        mapper = new ObjectMapper();
        mapper.setVisibilityChecker(mapper.getVisibilityChecker()
                .withFieldVisibility(ANY));
        mapper.setSerializationConfig(mapper.getSerializationConfig()
                .without(FAIL_ON_EMPTY_BEANS)
                .with(AUTO_DETECT_FIELDS)
                .without(AUTO_DETECT_GETTERS)
                .without(AUTO_DETECT_IS_GETTERS));
    }

    @Ignore
    @Test
    public void upgrades_using_the_class_upgrader() {
        // TODO
    }

    @Test
    public void does_nothing_when_already_at_latest_version() {
        int dataVersion = LATEST_VERSION;
        String original = "{ oldField: 1 }";

        String result = upgrade(dataVersion, original, DummyEntity.class);

        assertThat(result, is(original));
    }

    @Test
    public void fails_if_the_data_version_is_newer_than_the_upgrader_version() {
        int dataVersion = LATEST_VERSION + 1;
        String original = "{ oldField: 1 }";

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("data had version 11");
        thrown.expectMessage("upgrader had version 10");
        upgrade(dataVersion, original, DummyEntity.class);
    }

    @Test
    public void fails_if_the_upgradeable_annotation_is_missing() {
        Class<?> annotationMissingClass = UpgradeableAnnotationMissing.class;

        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("annotation was missing");
        thrown.expectMessage(annotationMissingClass.getName());
        thrown.expectMessage(Upgradeable.class.getName());
        upgrade(LATEST_VERSION, "{}", annotationMissingClass);
    }


    private String upgrade(int dataVersion, String original, Class<?> type) {
        Upgradeable upgradeable = getRequiredAnnotation(type, Upgradeable.class);

        int latestVersion = upgradeable.version();
        if (dataVersion > latestVersion) {
            throw new IllegalArgumentException("The data is newer than the upgrader. " +
                    "The data had version " + dataVersion + ", but the upgrader had version " + latestVersion + ".");
        }
        return original;
    }

    private static <T extends Annotation> T getRequiredAnnotation(Class<?> type, Class<T> annotationClass) {
        T annotation = type.getAnnotation(annotationClass);
        if (annotation == null) {
            throw new IllegalArgumentException(
                    "Expected " + type + " to be annotated with " + annotationClass + " but the annotation was missing.");
        }
        return annotation;
    }


    @Upgradeable(version = LATEST_VERSION)
    private static class DummyEntity {
    }

    private static class UpgradeableAnnotationMissing {
    }
}
