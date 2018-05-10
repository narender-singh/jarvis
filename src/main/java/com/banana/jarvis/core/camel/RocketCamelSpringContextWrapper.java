package com.banana.jarvis.core.camel;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.SpringCamelContext;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;

import com.banana.jarvis.JarvisRoutes;
import com.banana.jarvis.core.annotation.AsyncPostConstruct;

public class RocketCamelSpringContextWrapper implements SmartLifecycle, DisposableBean {

	private SpringCamelContext camelContext;
	private ApplicationContext appContext;
	private volatile boolean isRunning = false;

	public RocketCamelSpringContextWrapper(SpringCamelContext camelCtx, ApplicationContext ctx) {
		this.camelContext = camelCtx;
		this.appContext = ctx;
	}

	public SpringCamelContext getContext() {
		return camelContext;
	}

	@AsyncPostConstruct
	public void configure() {

		appContext.getBeansOfType(RouteBuilder.class).values().forEach((route) -> {
			try {
				route.addRoutesToCamelContext(camelContext);
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		});
	}

	@Override
	public void start() {
		try {
			if (camelContext.isStarted())
				System.out.print("Context already running");			
			appContext.getBean(JarvisRoutes.class).addRoutesToCamelContext(camelContext);			
			SpringCamelContext.setNoStart(false);
			camelContext.start();
			if (camelContext.isStarting())
				System.out.print("Context started running");
			System.out.println("Routes : " + camelContext.getRoutes());
			isRunning = true;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	@Override
	public void stop() {
		try {
			camelContext.stop();
			isRunning = false;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public boolean isRunning() {
		return isRunning;
	}

	@Override
	public int getPhase() {
		return 5;
	}

	@Override
	public void destroy() throws Exception {
		camelContext.stop();
		isRunning = false;
	}

	@Override
	public boolean isAutoStartup() {
		return false;
	}

	@Override
	public void stop(Runnable callback) {
		stop();
		callback.run();
	}

}
