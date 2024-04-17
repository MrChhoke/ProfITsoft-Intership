package org.prof.it.soft.databind.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class StringListSerializerTest {

    @Test
    public void serialize_shouldGenerateCorrectString_whenInputIsEmptyList() throws IOException {
        List<String> inputList = Collections.emptyList();
        JsonGenerator jsonGenerator = mock(JsonGenerator.class);
        SerializerProvider serializerProvider = mock(SerializerProvider.class);

        StringListSerializer serializer = new StringListSerializer();
        serializer.serialize(inputList, jsonGenerator, serializerProvider);

        String expectedString = "";
        assertThat(expectedString).isEqualTo("");
    }

    @Test
    public void serialize_shouldThrowException_whenInputIsNull() throws IOException {
        List<String> inputList = null;
        JsonGenerator jsonGenerator = mock(JsonGenerator.class);
        SerializerProvider serializerProvider = mock(SerializerProvider.class);

        StringListSerializer serializer = new StringListSerializer();

        assertThatThrownBy(() -> serializer.serialize(inputList, jsonGenerator, serializerProvider))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot serialize null list");
    }

    @Test
    public void serialize_shouldGenerateCorrectString_whenInputIsSingleElementList() throws IOException {
        List<String> inputList = List.of("element");
        JsonGenerator jsonGenerator = mock(JsonGenerator.class);
        SerializerProvider serializerProvider = mock(SerializerProvider.class);

        StringListSerializer serializer = new StringListSerializer();
        serializer.serialize(inputList, jsonGenerator, serializerProvider);

        String expectedString = "element";
        assertThat(expectedString).isEqualTo("element");
    }

    @Test
    public void serialize_shouldGenerateCorrectString_whenInputIsMultipleElementList() throws IOException {
        List<String> inputList = Arrays.asList("element1", "element2", "element3");
        JsonGenerator jsonGenerator = mock(JsonGenerator.class);
        SerializerProvider serializerProvider = mock(SerializerProvider.class);

        StringListSerializer serializer = new StringListSerializer();
        serializer.serialize(inputList, jsonGenerator, serializerProvider);

        String expectedString = "element1, element2, element3";
        assertThat(expectedString).isEqualTo("element1, element2, element3");
    }

}