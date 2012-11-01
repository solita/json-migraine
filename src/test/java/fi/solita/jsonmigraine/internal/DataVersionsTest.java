// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine.internal;

import fi.solita.jsonmigraine.api.TypeRenames;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class DataVersionsTest {

    private final TypeRenames renames = new TypeRenames();

    @Test
    public void round_trip_json_serialization() {
        DataVersions original = new DataVersions()
                .add(new DataVersion(Foo.class, 10))
                .add(new DataVersion(Bar.class, 20));

        DataVersions result = roundTripSerialize(original);

        assertThat(result.getVersion(Foo.class), is(10));
        assertThat(result.getVersion(Bar.class), is(20));
    }

    @Test
    public void json_format() throws Exception {
        String json = "{ \"fi.solita.jsonmigraine.internal.DataVersionsTest$Foo\" : 123 }";

        DataVersions versions = DataVersions.fromJson(new ObjectMapper().readTree(json), renames);

        assertThat(versions.getVersion(Foo.class), is(123));
    }

    @Test
    public void applies_renames_on_deserialization() {
        renames.rename(Foo.class.getName(), Bar.class.getName());
        DataVersions original = new DataVersions()
                .add(new DataVersion(Foo.class, 123));

        DataVersions result = roundTripSerialize(original);

        assertThat(result.getVersion(Bar.class), is(123));
    }

    private DataVersions roundTripSerialize(DataVersions original) {
        JsonNode json = original.toJson();
        return DataVersions.fromJson(json, renames);
    }


    private static class Foo {
    }

    private static class Bar {
    }
}
