package com.banana.jarvis.core.camel;

import org.apache.camel.spring.SpringCamelContext;
import org.springframework.context.ApplicationContext;

public class NoAutoStartSpringCamelContext extends SpringCamelContext {

	public NoAutoStartSpringCamelContext(final ApplicationContext ctx) {		
		super(ctx);
		setNoStart(true);
	}	
}
