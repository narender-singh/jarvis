package com.rocket.core.configuration;

import org.apache.camel.component.properties.PropertiesComponent;
import org.apache.camel.component.properties.PropertiesResolver;
import org.apache.camel.spring.SpringCamelContext;
import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rocket.core.RocketSpringContext;
import com.rocket.core.annotation.InstantiateFirst;
import com.rocket.core.camel.NoAutoStartSpringCamelContext;
import com.rocket.core.camel.RocketCamelPropertiesResolver;
import com.rocket.core.camel.RocketCamelSpringContextWrapper;
import com.rocket.core.camel.RocketSpringCxfBusFactory;

@Configuration
public class CamelConfiguration implements ApplicationContextAware {

	private RocketSpringContext springContext;

	// currently not in use, need to check more on this.
	@Bean
	@InstantiateFirst(priority = 1)
	public SpringBus cxf() {
		return RocketSpringCxfBusFactory.create(springContext);
	}

	@Bean
	@InstantiateFirst
	public BusFactory cxfbusFactory() {
		return new RocketSpringCxfBusFactory(springContext);
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
