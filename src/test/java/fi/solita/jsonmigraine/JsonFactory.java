// Copyright © 2012 Solita Oy <www.solita.fi>
// This software is released under the MIT License.
// The license text is at http://opensource.org/licenses/MIT

package fi.solita.jsonmigraine;

import org.codehaus.jackson.node.*;

import java.util.Random;

public class JsonFactory {

    public static ObjectNode object() {
        return JsonNodeFactory.instance.objectNode();
    }

    public static ObjectNode unimportantObject() {
        return field("unimportantField", new Random().nextInt(100));
    }

    public static ObjectNode field(String fieldName, int value) {
        return field(fieldName, value, object());
    }

    private static ObjectNode field(String fieldName, int value, ObjectNode data) {
        data.put(fieldName, value);
        return data;
    }

    public static ObjectNode field(String fieldName, boolean value) {
        return field(fieldName, value, object());
    }

    public static ObjectNode field(String fieldName, boolean value, ObjectNode data) {
        data.put(fieldName, value);
        return data;
    }
}
