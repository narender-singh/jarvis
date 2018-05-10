package com.banana.jarvis.core.configuration;

import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.component.properties.PropertiesResolver;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.cxf.bus.spring.SpringBus;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.banana.jarvis.core.RocketSpringContext;
import com.banana.jarvis.core.annotation.InstantiateFirst;
import com.banana.jarvis.core.camel.JsonProvider;
import com.banana.jarvis.core.camel.NoAutoStartSpringCamelContext;
import com.banana.jarvis.core.camel.RocketCamelPropertiesResolver;
import com.banana.jarvis.core.camel.RocketCamelSpringContextWrapper;
import com.banana.jarvis.core.camel.RocketSpringCxfBusFactory;

@Configuration
public class CamelConfiguration implements ApplicationContextAware {

	private RocketSpringContext springContext;

	// currently not in use, need to check more on this.
	@Bean
	@InstantiateFirst
	public SpringBus springBus() {
		return RocketSpringCxfBusFactory.create(springContext);
	}

	@Bean
	public JacksonJsonProvider jsonProvider() {
		return new JsonProvider();
	}

	@Bean
	public SpringCamelContext camelContext() {
		return camelContextWrapper().getContext();
	}

	@Bean
	public RocketCamelSpringContextWrapper camelContextWrapper() {

		PropertiesComponent ppc = new PropertiesComponent("localOnly");
		PropertiesResolver resolver = new RocketCamelPropertiesResolver(springContext.getPropsSource().getProperties());
		ppc.setPropertiesResolver(resolver);
		SpringCamelContext ctx = new NoAutoStartSpringCamelContext(springContext);
		ctx.setStreamCaching(true);
		ctx.setAllowUseOriginalMessage(true);
		ctx.setName("rocketContext");
		ctx.addComponent("properties", ppc);
		return new RocketCamelSpringContextWrapper(ctx, springContext);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.springContext = (RocketSpringContext) applicationContext;

	}
}
