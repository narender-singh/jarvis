package com.banana.jarvis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.banana.jarvis.ws.UserService;

@Configuration
public class JarvisConfiguration {

	@Bean
	public UserService userService() {
		return new UserService();
	}

}
