// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import java.util.*;

public class TypeRenames {

    private final Map<String, String> renames = new HashMap<String, String>();

    public void rename(String oldName, String newName) {
        renames.put(oldName, newName);
    }

    public String getLatestName(String name) {
        String renamed = renames.get(name);
        if (renamed != null) {
            return getLatestName(renamed);
        }
        return name;
    }
}
