package com.banana.jarvis.core.camel;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.jaxrs.provider.ServerProviderFactory;
import org.apache.cxf.message.Message;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.springframework.context.ApplicationContext;

public class RocketSpringCxfBusFactory {

	private RocketSpringCxfBusFactory() {

	}

	public static SpringBus create(final ApplicationContext ctx) {
		SpringBus bus = new SpringBus();
		List<Interceptor<? extends Message>> inInterceptors = bus.getInInterceptors();
		inInterceptors.add(new GenericInterceptor());
		ServiceLoader<InterceptorProvider> load = ServiceLoader.load(InterceptorProvider.class);
		for (InterceptorProvider provider : load) {
			inInterceptors.addAll(provider.getInInterceptors());
			bus.getOutInterceptors().addAll(provider.getOutInterceptors());
			bus.getOutFaultInterceptors().addAll(provider.getOutFaultInterceptors());
			bus.getInFaultInterceptors().addAll(provider.getInFaultInterceptors());
		}
		ServerProviderFactory sf = ServerProviderFactory.createInstance(bus);
		final ArrayList<Object> userProviders = new ArrayList<>();
		userProviders.add(new JacksonJsonProvider());
		sf.setUserProviders(userProviders);
		bus.setProperty("jaxrs.shared.server.factory", sf);
		bus.setProperty("org.apache.cxf.jaxrs.bus.providers", userProviders);
		BusFactory.setDefaultBus(bus);
		BusFactory.setThreadDefaultBus(bus);
		return bus;
	}

}
