package org.prof.it.soft.databind.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MapSerializerTest {

    @Test
    void serialize_shouldGenerateCorrectJson_whenInputIsMap() throws IOException {
        Map<Object, Object> inputMap = new HashMap<>();
        inputMap.put("key1", 1);
        inputMap.put("key2", 2);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonGenerator jsonGenerator = mock(JsonGenerator.class);
        SerializerProvider serializerProvider = mock(SerializerProvider.class);

        MapSerializer serializer = new MapSerializer();
        serializer.serialize(inputMap, jsonGenerator, serializerProvider);

        String expectedJson = objectMapper.writeValueAsString(new JsonSerializable() {
            @Override
            public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeStartObject();
                gen.writeObjectFieldStart("item");
                gen.writeFieldName("key");
                gen.writeString("key1");
                gen.writeFieldName("count");
                gen.writeNumber(1);
                gen.writeEndObject();

                gen.writeObjectFieldStart("item");
                gen.writeFieldName("key");
                gen.writeString("key2");
                gen.writeFieldName("count");
                gen.writeNumber(2);
                gen.writeEndObject();
                gen.writeEndObject();
            }

            @Override
            public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
                serialize(gen, serializers);
            }
        });

        assertThat(expectedJson).isEqualTo("{\"item\":{\"key\":\"key1\",\"count\":1},\"item\":{\"key\":\"key2\",\"count\":2}}");
    }
}
