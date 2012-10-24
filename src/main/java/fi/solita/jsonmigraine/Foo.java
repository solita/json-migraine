// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.node.ObjectNode;

public class Foo { // TODO: rename

//        ObjectMapper mapper;
//        mapper = new ObjectMapper();
//        mapper.setVisibilityChecker(mapper.getVisibilityChecker()
//                .withFieldVisibility(ANY));
//        mapper.setSerializationConfig(mapper.getSerializationConfig()
//                .without(FAIL_ON_EMPTY_BEANS)
//                .with(AUTO_DETECT_FIELDS)
//                .without(AUTO_DETECT_GETTERS)
//                .without(AUTO_DETECT_IS_GETTERS));


    public static void upgrade(ObjectNode data, int dataVersion, Upgrader upgrader) {
        int latestVersion = upgrader.version();
        if (dataVersion > latestVersion) {
            throw new IllegalArgumentException("The data is newer than the upgrader. " +
                    "The data had version " + dataVersion + ", but the upgrader had version " + latestVersion + ".");
        }
        while (dataVersion < latestVersion) {
            upgrader.upgrade(data, dataVersion);
            dataVersion++;
        }
    }
}
