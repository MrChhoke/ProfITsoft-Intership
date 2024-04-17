package org.prof.it.soft.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * This class is a custom deserializer for collections of strings in JSON.
 * It extends the JsonDeserializer class provided by the Jackson library.
 */
public class StringListDeserializer extends JsonDeserializer<Collection<String>> {

    /**
     * This method overrides the deserialize method from the JsonDeserializer class.
     * It takes a JsonParser and DeserializationContext as parameters, and returns a Collection of Strings.
     * <p>
     * The method first gets the value from the JsonParser as a String.
     * If the value is null or empty, it returns empty list.
     * Otherwise, it splits the value by commas (ignoring any amount of whitespace after the comma) and returns the resulting array as a List.
     *
     * @param p   the JsonParser to deserialize
     * @param ctx the DeserializationContext
     * @return a Collection of Strings
     * @throws IOException if an input or output exception occurred
     *
     *                     <p>Example usage:</p>
     *                     <pre>
     *                         String json = "\"value1, value2, value3\"";
     *                         JsonParser parser = new JsonFactory().createParser(json);
     *                         Collection<String> collection = new StringListDeserializer().deserialize(parser, null);
     *                     </pre>
     */
    @Override
    public Collection<String> deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
        String value = p.getValueAsString();
        if (value == null || value.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Arrays.stream(value.split(",\\s*")).map(String::trim).toList();
        }
    }
}
