package hangman.util;

import com.fasterxml.jackson.core.JsonParseException;
import org.junit.Test;

import java.io.IOException;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonConverterTest {

	/*
	 * to object - throws exc
	 */

    @Test(expected = NullPointerException.class)
    public void convertToStringIllegalValue() throws IOException {
        JsonConverter.convert(null, SimpleClass.class);
    }

    @Test(expected = NullPointerException.class)
    public void convertToStringIllegalValueType() throws IOException {
        JsonConverter.convert("{\"value\":\"dog\"}", null);
    }

    @Test
    public void convertToObjectNullValue() throws IOException {
        assertEquals(new SimpleClass(), JsonConverter.convert("{\"value\":null}", SimpleClass.class));
    }

    @Test
    public void convertToObjectSimpleValue() throws IOException {
        assertEquals(new SimpleClass("dog"), JsonConverter.convert("{\"value\":\"dog\"}", SimpleClass.class));
    }

    @Test
    public void convertToObjectEscapedCharacter() throws IOException {
        assertEquals(new SimpleClass("cat\\"), JsonConverter.convert("{\"value\":\"cat\\\\\"}", SimpleClass.class));
    }

    @Test(expected = JsonParseException.class)
    public void convertToObjectIncorrectString() throws IOException {
        assertEquals(new SimpleClass(), JsonConverter.convert("{\"val\"}", SimpleClass.class));
    }
	
	/*
	 * to object - returns null
	 */

    @Test
    public void convertOrNullToObjectOkString() {
        assertTrue(JsonConverter.convertOrNull("{\"value\":\"dog\"}", SimpleClass.class) != null);
    }

    @Test
    public void convertOrNullToObjectNotOkString() {
        assertTrue(JsonConverter.convertOrNull("{\"value\":", SimpleClass.class) == null);
    }

    @Test(expected = NullPointerException.class)
    public void convertOrNullToObject_Null1() {
        JsonConverter.convertOrNull(null, SimpleClass.class);
    }

    @Test(expected = NullPointerException.class)
    public void convertOrNullToObject_Null2() {
        JsonConverter.convertOrNull("", null);
    }


    public static class SimpleClass {
        public String value;

        public SimpleClass() {
        }

        public SimpleClass(String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(this.value);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;

            SimpleClass other = (SimpleClass) obj;
            return Objects.equals(this.value, other.value);
        }

    }
}
