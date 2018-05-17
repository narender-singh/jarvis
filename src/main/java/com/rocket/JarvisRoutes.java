package com.rocket;

import org.apache.camel.builder.RouteBuilder;

public class JarvisRoutes extends RouteBuilder {

	@Override
	public void configure() throws Exception {
		try {
			from("jetty://http://0.0.0.0:9292/jarvis?matchOnUriPrefix=true")
					.to("cxfbean:userService");//?bus=#springBus&providers=#jsonProvider");			
		} catch (Exception e) {
			System.out.println(e);
			throw new RuntimeException(e);
		}
	}
}
