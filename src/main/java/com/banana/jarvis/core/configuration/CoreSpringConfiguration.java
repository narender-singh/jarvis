package com.banana.jarvis.core.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.banana.jarvis.core.StaticHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

@Configuration
public class CoreSpringConfiguration {

	@Bean
	public ObjectMapper objectMapper() {
		return StaticHolder.getMapper();
	}

	@Bean
	public ObjectReader objectReader() {
		return StaticHolder.getMapper().reader();
	}

	@Bean
	public ObjectWriter objectWriter() {
		return StaticHolder.getMapper().writer();
	}

}
