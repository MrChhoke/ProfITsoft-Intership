package org.prof.it.soft.databind.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Collection;

/**
 * This class is a custom serializer for Collection<String> in JSON.
 * It extends the JsonSerializer class provided by the Jackson library.
 *
 * <p>Example usage:</p>
 * <pre>
 *     ObjectMapper mapper = new ObjectMapper();
 *     SimpleModule module = new SimpleModule();
 *     module.addSerializer(Collection.class, new StringListSerializer());
 *     mapper.registerModule(module);
 *
 *     Collection<String> list = new ArrayList<>();
 *     list.add("value1");
 *     list.add("value2");
 *     list.add("value3");
 *
 *     String json = mapper.writeValueAsString(list);
 * </pre>
 *
 * <p>Example output:</p>
 * <pre>
 *     "value1, value2, value3"
 * </pre>
 */
public class StringListSerializer extends JsonSerializer<Collection<String>> {

    /**
     * This method overrides the serialize method from the JsonSerializer class.
     * It takes a Collection<String>, JsonGenerator and SerializerProvider as parameters.
     *
     * The method uses the String.join function to concatenate all strings in the collection,
     * separated by a comma and a space. The resulting string is then written to the JsonGenerator.
     *
     * @param list the Collection<String> to serialize
     * @param gen the JsonGenerator to write the JSON
     * @param serializers the SerializerProvider
     * @throws IOException if an input or output exception occurred
     *
     * <p>Example usage:</p>
     * <pre>
     *     Collection<String> list = new ArrayList<>();
     *     list.add("value1");
     *     list.add("value2");
     *     list.add("value3");
     *
     *     JsonGenerator generator = new JsonFactory().createGenerator(System.out);
     *     new StringListSerializer().serialize(list, generator, null);
    * </pre>
     *
     * <p>Example output:</p>
     * <pre>
     *     "value1, value2, value3"
     * </pre>
     */
    @Override
    public void serialize(Collection<String> list, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (list == null) {
            throw new IllegalArgumentException("Cannot serialize null list");
        }

        gen.writeString(String.join(", ", list));
    }

    /**
     * This method overrides the isEmpty method from the JsonSerializer class.
     * It takes a SerializerProvider and a Collection<String> as parameters.
     *
     * The method checks if the collection is null or empty and returns the result.
     *
     * @param provider the SerializerProvider
     * @param list the Collection<String> to check
     * @return true if the collection is null or empty, false otherwise
     */
    @Override
    public boolean isEmpty(SerializerProvider provider, Collection<String> list) {
        return list == null || list.isEmpty();
    }

}