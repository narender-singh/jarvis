package com.banana.jarvis.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class StaticHolder {

	private static final ObjectMapper OBJECT_MAPPER;
	private static final ExecutorService DEFAULT_EXECUTOR;

	static {
		OBJECT_MAPPER = new ObjectMapper();
		OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
		OBJECT_MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
		OBJECT_MAPPER.configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, true);
		OBJECT_MAPPER.configure(SerializationFeature.CLOSE_CLOSEABLE, true);
		OBJECT_MAPPER.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
		OBJECT_MAPPER.enable(SerializationFeature.WRITE_DATE_KEYS_AS_TIMESTAMPS);

		DEFAULT_EXECUTOR = Executors.newFixedThreadPool(2);
	}

	public static ObjectMapper getMapper() {
		return OBJECT_MAPPER;
	}

	public static ExecutorService getDefaultExecutor() {
		return DEFAULT_EXECUTOR;
	}
}
