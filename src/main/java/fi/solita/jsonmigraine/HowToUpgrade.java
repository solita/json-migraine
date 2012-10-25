// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import java.util.*;

public class HowToUpgrade {

    public final List<UpgradeStep> steps = new ArrayList<UpgradeStep>();

    public HowToUpgrade add(UpgradeStep step) {
        steps.add(step);
        return this;
    }
}
