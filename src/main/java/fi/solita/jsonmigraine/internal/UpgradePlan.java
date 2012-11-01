// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine.internal;

import java.util.*;

public class UpgradePlan {

    public final List<UpgradeStep> steps = new ArrayList<UpgradeStep>();

    public UpgradePlan add(UpgradeStep step) {
        steps.add(step);
        return this;
    }

    public UpgradePlan addFirst(UpgradeStep step) {
        steps.add(0, step);
        return this;
    }
}
