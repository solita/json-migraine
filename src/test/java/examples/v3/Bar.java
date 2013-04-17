// Copyright © 2012-2013 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package examples.v3;

import fi.solita.jsonmigraine.api.Upgradeable;

@Upgradeable(BarUpgrader.class)
public class Bar {

    public String newField;
}
