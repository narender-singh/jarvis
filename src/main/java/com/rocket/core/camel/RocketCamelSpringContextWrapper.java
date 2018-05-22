package com.rocket.core.camel;

import java.util.Map;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.SpringCamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;

import com.rocket.core.annotation.AsyncPostConstruct;

public class RocketCamelSpringContextWrapper implements SmartLifecycle, DisposableBean {

	private Logger l = LoggerFactory.getLogger(RocketCamelSpringContextWrapper.class);

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
		Map<String, RouteBuilder> beans = appContext.getBeansOfType(RouteBuilder.class);
		l.info("Routes", beans);
		beans.values().forEach((route) -> {
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
			SpringCamelContext.setNoStart(false);
			camelContext.start();
			l.info("Routes : " + camelContext.getRoutes());
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
