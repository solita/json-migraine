// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class TypeRenamesTest {

    private static final String FOO = "com.example.Foo";
    private static final String BAR = "com.example.Bar";
    private static final String GAZONK = "com.example.Gazonk";

    private final TypeRenames renames = new TypeRenames();

    @Test
    public void returns_the_name_as_is_when_not_renamed() {
        String result = renames.getLatestName(FOO);

        assertThat(result, is(FOO));
    }

    @Test
    public void returns_the_new_name_when_renamed() {
        renames.rename(FOO, BAR);

        String result = renames.getLatestName(FOO);

        assertThat(result, is(BAR));
    }

    @Test
    public void works_for_multiple_chained_renames() {
        renames.rename(FOO, BAR);
        renames.rename(BAR, GAZONK);

        String result = renames.getLatestName(FOO);

        assertThat(result, is(GAZONK));
    }
}
