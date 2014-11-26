package hangman.util;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonConverterTest {
	
	@Test(expected=NullPointerException.class)
	public void convertToStringIllegalTest() throws JsonProcessingException {
		JsonConverter.convert(null);
	}

	/*
	 * to string - trow exc
	 */
	
	@Test
	public void convertToStringNullValueTest() throws JsonProcessingException {
		assertEquals("{\"value\":null}", JsonConverter.convert(new SimpleClass()));
	}
	
	@Test
	public void convertToStringSimpleValueTest() throws JsonProcessingException {
		assertEquals("{\"value\":\"dog\"}", JsonConverter.convert(new SimpleClass("dog")));
	}
	
	@Test
	public void convertToStringEscapeCharacterTest() throws JsonProcessingException {
		assertEquals("{\"value\":\"cat\\\\\"}", JsonConverter.convert(new SimpleClass("cat\\")));
	}
	
	/*
	 * to string - returns null
	 */
	
	@Test
	public void convertToStringOrNullOkTest() {
		assertTrue(JsonConverter.convertOrNull(new SimpleClass("dog"))!=null);
	}
	
	@Test(expected=NullPointerException.class)
	public void convertToStringOrNullPassingNullTest() {
		JsonConverter.convertOrNull(null);
	}
	
	/*
	 * to object - throws exc
	 */
	
	@Test(expected=NullPointerException.class)
	public void convertToStringIllegalValueTest() throws IOException {
		JsonConverter.convert(null, SimpleClass.class);
	}
	
	@Test(expected=NullPointerException.class)
	public void convertToStringIllegalValueTypeTest() throws IOException {
		JsonConverter.convert("{\"value\":\"dog\"}", null);
	}
	
	@Test
	public void convertToObjectNullValueTest() throws IOException {
		assertEquals(new SimpleClass(), JsonConverter.convert("{\"value\":null}", SimpleClass.class));
	}
	
	@Test
	public void convertToObjectSimpleValueTest() throws IOException {
		assertEquals(new SimpleClass("dog"), JsonConverter.convert("{\"value\":\"dog\"}", SimpleClass.class));
	}
	
	@Test
	public void convertToObjectEscapedCharacterTest() throws IOException {
		assertEquals(new SimpleClass("cat\\"), JsonConverter.convert("{\"value\":\"cat\\\\\"}", SimpleClass.class));
	}
	
	@Test(expected=JsonParseException.class)
	public void convertToObjectIncorrectStringTest() throws IOException {
		assertEquals(new SimpleClass(), JsonConverter.convert("{\"val\"}", SimpleClass.class));
	}
	
	/*
	 * to object - returns null
	 */
	
	@Test
	public void convertOrNullToObjectOkStrngTest() {
		assertTrue(JsonConverter.convertOrNull("{\"value\":\"dog\"}", SimpleClass.class)!=null);
	}
	
	@Test
	public void convertOrNullToObjectNotOkStrngTest() {
		assertTrue(JsonConverter.convertOrNull("{\"value\":", SimpleClass.class)==null);
	}
	
	@Test(expected=NullPointerException.class)
	public void convertOrNullToObject_Null1Test() {
		JsonConverter.convertOrNull(null, SimpleClass.class);
	}
	
	@Test(expected=NullPointerException.class)
	public void convertOrNullToObject_Null2Test() {
		JsonConverter.convertOrNull("", null);
	}
	
	
	
	
	public static class SimpleClass {
		private String value;
		
		public SimpleClass(){}

		public SimpleClass(String value) {
			this.value = value;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SimpleClass other = (SimpleClass) obj;
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value))
				return false;
			return true;
		}
		
	} 
}
