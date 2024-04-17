package org.prof.it.soft.databind.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Map;

/**
 * This class is a custom serializer for Map<Object, Object> in JSON.
 * It extends the JsonSerializer class provided by the Jackson library.
 *
 * <p>Example usage:</p>
 * <pre>
 *     ObjectMapper mapper = new ObjectMapper();
 *     SimpleModule module = new SimpleModule();
 *     module.addSerializer(Map.class, new MapSerializer());
 *     mapper.registerModule(module);
 *
 *     Map<Object, Object> map = new HashMap<>();
 *     map.put("key1", 1);
 *     map.put("key2", 2);
 *     map.put("key3", 3);
 *
 *     String json = mapper.writeValueAsString(map);
 * </pre>
 */
public class MapSerializer extends JsonSerializer<Map<Object, Object>> {

    /**
     * This method overrides the serialize method from the JsonSerializer class.
     * It takes a Map<Object, Object>, JsonGenerator and SerializerProvider as parameters.
     *
     * The method first starts writing a JSON object using the JsonGenerator.
     * Then, for each entry in the Map, it writes a new JSON object with the key "item".
     * Inside this object, it writes two fields: "key" and "count".
     * The "key" field is the string representation of the Map entry's key.
     * The "count" field is the string representation of the Map entry's value.
     * After writing all fields for an entry, it ends the "item" object.
     * Once all entries have been written, it ends the outer JSON object.
     *
     * @param value the Map to serialize
     * @param gen the JsonGenerator to write the JSON
     * @param serializers the SerializerProvider
     * @throws IOException if an input or output exception occurred
     *
     * <p>Example usage:</p>
     * <pre>
     *     Map<Object, Object> map = new HashMap<>();
     *     map.put("key1", 1);
     *     map.put("key2", 2);
     *     map.put("key3", 3);
     *
     *     JsonGenerator generator = new JsonFactory().createGenerator(System.out);
     *     new MapSerializer().serialize(map, generator, null);
     * </pre>
     *
     * <p>Example output:</p>
     * <pre>
     * {
     *     "item": {
     *         "key": "key1",
     *         "count": "1"
     *     },
     *     "item": {
     *         "key": "key2",
     *         "count": "2"
     *     },
     *     "item": {
     *         "key": "key3",
     *         "count": "3"
     *     }
     * }
     * </pre>
     */
    @Override
    public void serialize(Map<Object, Object> value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject();
        for (var entry : value.entrySet()) {
            gen.writeObjectFieldStart("item");
            gen.writeFieldName("key");
            gen.writeString(entry.getKey().toString());
            gen.writeFieldName("count");
            gen.writeNumber(entry.getValue().toString());
            gen.writeEndObject();
        }
        gen.writeEndObject();
    }
}