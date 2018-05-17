package com.rocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rocket.ws.UserService;

@Configuration
public class JarvisConfiguration {

	@Bean
	public UserService userService() {
		return new UserService();
	}

}
