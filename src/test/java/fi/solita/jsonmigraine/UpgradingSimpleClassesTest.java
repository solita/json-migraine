// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import static org.codehaus.jackson.annotate.JsonAutoDetect.Visibility.ANY;
import static org.codehaus.jackson.map.SerializationConfig.Feature.*;

public class UpgradingSimpleClassesTest {

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

    @Test
    public void upgrades_using_the_class_upgrader() {
        // TODO
    }

    @Test
    public void does_nothing_when_already_at_latest_version() {
        // TODO
    }

    @Test
    public void fails_if_the_data_version_is_newer_than_the_upgrader_version() {
        // TODO
    }
}
