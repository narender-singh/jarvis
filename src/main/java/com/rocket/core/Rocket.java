package com.rocket.core;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.rocket.core.configuration.CoreSpringConfiguration;

public class Rocket implements AutoCloseable {

	private Properties properties;
	private final Set<Class<?>> MAIN_CLASSES = new HashSet<>(2);
	private final Set<String> MAIN_PACKAGES = new HashSet<>(2);
	private CountDownLatch exitSignal = new CountDownLatch(1);
	private int exitCode = 0;
	private volatile RocketSpringContext context;
	@SuppressWarnings("unused")
	private RocketPropertySource propSource;
	private Properties originalSystemProperties;

	/**
	 * Private constructor singleton class
	 */
	private Rocket() {
	}

	public Properties getProperties() {
		return properties;
	}

	public String getProperty(final String key) {
		return properties.getProperty(key);
	}

	public AnnotationConfigApplicationContext getContext() {
		return this.context;
	}
	
	public static Rocket configure(){
		return Builder.getRocket();
	}
	

	public static Rocket build() {
		return Builder.getRocket();
	}

	public Rocket withProperties(final Properties prop) {
		if (this.properties == null)
			properties = prop;
		else
			prop.forEach((x, y) -> properties.setProperty((String) x, (String) y));
		return this;
	}

	public Rocket withProperty(final String key, final String value) {
		if (properties != null)
			properties.setProperty(key, value);
		else {
			properties = new Properties();
			properties.setProperty(key, value);
		}
		return this;
	}

	private static class Builder {
		
		public static Logger LAZY  = LoggerFactory.getLogger(Rocket.Builder.class);
		
		private static final Rocket rocket = new Rocket();

		static{
			rocket.properties = new Properties();
			try {
				rocket.properties.load(new FileInputStream("default.properties"));
			} catch (IOException e) {
				LAZY.warn("default.properties file not found");
			}
		}
		
		private static Rocket getRocket() {
			return rocket;
		}
	}

	public Rocket withClass(final Class<?> clazz) {
		MAIN_CLASSES.add(clazz);
		return this;
	}

	public Rocket withClasses(final Collection<? extends Class<?>> clazzes) {
		MAIN_CLASSES.addAll(clazzes);
		return this;
	}

	public Rocket withPackage(final String packageName) {
		MAIN_PACKAGES.add(packageName);
		return this;
	}

	public Rocket withPackages(final Set<String> packages) {
		MAIN_PACKAGES.addAll(packages);
		return this;
	}

	private void setExistFlag() {
		exitSignal.countDown();
	}

	public Rocket initialize() {
		initializeInternal();
		return this;
	}

	public void launchAndWait() {
		context.start();
		System.exit(waitForExistSingal());
	}

	private void initializeInternal() {
		RocketPropertySource propSource = new RocketPropertySource(properties);
		this.propSource = propSource;
		originalSystemProperties = updateAndMergeSystemProperties(properties);
		AsyncPostConstructBeanPostProcessor post = new AsyncPostConstructBeanPostProcessor();
		RocketSpringContext context = new RocketSpringContext(propSource, post);
		this.context = context;
		this.context.register(CoreSpringConfiguration.class);
		if (MAIN_CLASSES.size() > 0)
			MAIN_CLASSES.forEach((cls) -> {
				context.register(cls);
			});
		if (MAIN_PACKAGES.size() > 0)
			MAIN_PACKAGES.forEach((p) -> {
				context.scan(p);
			});
		context.refresh();
		context.waitForInitializationToComplete();
	}

	private Properties updateAndMergeSystemProperties(Properties properties) {

		Properties result = (Properties) System.getProperties().clone();
		for (Object p : properties.keySet()) {
			String prop = (String) p;
			System.setProperty(prop, properties.getProperty(prop));
		}
		return result;
	}

	private int waitForExistSingal() {
		try {
			exitSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return exitCode;
	}

	@Override
	public void close() throws Exception {
		if (this.context != null) {
			this.context.stop();
			this.context.close();
			this.context = null;
			System.getProperties().clear();
			System.getProperties().putAll(originalSystemProperties);
			setExistFlag();
		}

	}

}
