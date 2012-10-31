// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.JsonNode;

public interface Upgrader {

    /**
     * Current version of the class.
     */
    int version();

    /**
     * Upgrades the {@code data} from the given {@code version} to the next version.
     * Will be called multiple times, once for each skipped version, if the data is multiple
     * versions older than the current {@link #version()} number.
     *
     * @param data    data to be upgraded.
     * @param version version of the data to be upgraded. Is always smaller than the current {@link #version()}.
     */
    void upgrade(JsonNode data, int version);
}
