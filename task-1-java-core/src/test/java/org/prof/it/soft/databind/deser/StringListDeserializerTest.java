package org.prof.it.soft.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StringListDeserializerTest {

    @Test
    void deserialize_shouldReturnEmptyList_whenInputIsEmpty() throws IOException {
        JsonParser jsonParser = mock(JsonParser.class);
        DeserializationContext ctx = mock(DeserializationContext.class);

        when(jsonParser.getValueAsString()).thenReturn("");

        StringListDeserializer deserializer = new StringListDeserializer();
        assertThat(deserializer.deserialize(jsonParser, ctx)).isEmpty();
    }

    @Test
    void deserialize_shouldReturnEmptyList_whenInputIsNull() throws IOException {
        JsonParser jsonParser = mock(JsonParser.class);
        DeserializationContext ctx = mock(DeserializationContext.class);

        when(jsonParser.getValueAsString()).thenReturn(null);

        StringListDeserializer deserializer = new StringListDeserializer();
        assertThat(deserializer.deserialize(jsonParser, ctx)).isEmpty();
    }

    @Test
    void deserialize_shouldReturnSingleValue_whenInputIsSingleValue() throws IOException {
        JsonParser jsonParser = mock(JsonParser.class);
        DeserializationContext ctx = mock(DeserializationContext.class);

        when(jsonParser.getValueAsString()).thenReturn("test");

        StringListDeserializer deserializer = new StringListDeserializer();
        assertThat(deserializer.deserialize(jsonParser, ctx)).containsExactly("test");
    }

    @Test
    void deserialize_shouldReturnMultipleValues_whenInputHasMultipleValues() throws IOException {
        JsonParser jsonParser = mock(JsonParser.class);
        DeserializationContext ctx = mock(DeserializationContext.class);

        when(jsonParser.getValueAsString()).thenReturn("test1,test2,test3");

        StringListDeserializer deserializer = new StringListDeserializer();
        assertThat(deserializer.deserialize(jsonParser, ctx)).containsExactly("test1", "test2", "test3");
    }

    @Test
    void deserialize_shouldReturnMultipleValues_whenInputHasMultipleValuesWithSpaces() throws IOException {
        JsonParser jsonParser = mock(JsonParser.class);
        DeserializationContext ctx = mock(DeserializationContext.class);

        when(jsonParser.getValueAsString()).thenReturn("test1, test2 , test3");

        StringListDeserializer deserializer = new StringListDeserializer();
        assertThat(deserializer.deserialize(jsonParser, ctx)).containsExactly("test1", "test2", "test3");
    }
}
