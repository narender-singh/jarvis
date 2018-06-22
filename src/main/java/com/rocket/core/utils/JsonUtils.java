package com.rocket.core.utils;

import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonUtils {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public static final String Serialize(Object obj) throws JsonProcessingException {
		return MAPPER.writeValueAsString(obj);
	}

	public static final JsonNode Deserialize(String json) throws IOException {
		return MAPPER.readTree(json);
	}

	public static final <T> T Deserialize(InputStream stream, Class<T> t)
			throws JsonParseException, JsonMappingException, IOException {
		return MAPPER.readValue(stream, t);
	}

	public static final <T> T Deserialize(String json, Class<T> t)
			throws JsonParseException, JsonMappingException, IOException {
		return MAPPER.readValue(json, t);
	}

	public static final <T> T Deserialize(String json, TypeReference<T> t)
			throws JsonParseException, JsonMappingException, IOException {
		return MAPPER.readValue(json, t);
	}
}
