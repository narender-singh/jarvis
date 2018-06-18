package com.rocket.core.camel;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.apache.cxf.BusFactory;
import org.apache.cxf.bus.spring.SpringBus;
import org.apache.cxf.bus.spring.SpringBusFactory;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.jaxrs.provider.ServerProviderFactory;
import org.apache.cxf.message.Message;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.rocket.core.camel.cxf.jaxrs.providers.RocketExceptionMapper;

public final class RocketSpringCxfBusFactory extends SpringBusFactory implements ApplicationContextAware {

	private static ApplicationContext appContext;

	public RocketSpringCxfBusFactory() {
		super(appContext);
	}

	public RocketSpringCxfBusFactory(ApplicationContext ctx) {
		super(ctx);
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
		final ArrayList<Object> rocketProviders = new ArrayList<>();
		rocketProviders.add(new JacksonJsonProvider());
		rocketProviders.add(new RocketExceptionMapper());
		ServiceLoader<UserJaxRsProvider> userProviders = ServiceLoader.load(UserJaxRsProvider.class);
		userProviders.forEach(x -> rocketProviders.add(x));
		sf.setUserProviders(rocketProviders);
		bus.setProperty("jaxrs.shared.server.factory", sf);
		bus.setProperty("org.apache.cxf.jaxrs.bus.providers", rocketProviders);
		BusFactory.setDefaultBus(bus);
		BusFactory.setThreadDefaultBus(bus);
		return bus;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		appContext = applicationContext;
	}
}
