// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import java.util.*;

public class StubUpgraderProvider implements UpgraderProvider {

    private final Map<Class<?>, Upgrader> upgraders = new HashMap<Class<?>, Upgrader>();

    public void put(Class<?> type, Upgrader upgrader) {
        upgraders.put(type, upgrader);
    }

    @Override
    public Upgrader getUpgrader(Class<?> type) {
        Upgrader upgrader = upgraders.get(type);
        if (upgrader == null) {
            throw new IllegalArgumentException("no upgrader for " + type);
        }
        return upgrader;
    }
}
