package com.rocket.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.rocket.core.configuration.CamelConfiguration;
import com.rocket.core.configuration.CoreSpringConfiguration;
import com.rocket.core.utils.Result;
import com.rocket.core.utils.RocketUtils;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public final class Rocket implements AutoCloseable {

	private Properties properties;
	private static boolean isinitialized = false;
	private final Set<Class<?>> MAIN_CLASSES = new HashSet<>(2);
	private final Set<String> MAIN_PACKAGES = new HashSet<>(2);
	private CountDownLatch exitSignal = new CountDownLatch(1);
	private int exitCode = 0;
	private static boolean isStarted = false;
	private volatile RocketSpringContext context;
	private RocketPropertySource propSource;
	private Properties originalSystemProperties;
	private static final RocketUncaughtExceptionHandlerImpl UEH = new RocketUncaughtExceptionHandlerImpl(true);
	public static final String ROCKET_APPNAME = "rocket.applicationName";
	public static final String ROCKET_PORT = "http.portNo";
	public static final String ROCKET_LOG_DIR_PROPERTY = "rocket.logdir";

	static {
		Thread.setDefaultUncaughtExceptionHandler(UEH);
	}

	/**
	 * Private constructor singleton class
	 */
	private Rocket() {
		Thread.currentThread().setUncaughtExceptionHandler(UEH);
		registerRocketSignalHandler();
	}

	public Rocket withEnv(Env env) {
		Habitat.setEnv(env);
		return this;
	}

	public boolean isRunning() {
		if (context != null)
			context.isRunning();
		return false;
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

	public static Rocket configure() {
		return Builder.getRocket();
	}

	public static Rocket build() {
		return Builder.getRocket();
	}

	public Rocket withProperties(final Properties prop) {
		if(isRunning())
			throw new IllegalStateException("Rocket is running, can't add/modify properties");		
		if (this.properties == null)
			properties = prop;
		else
			prop.forEach((x, y) -> properties.setProperty((String) x, (String) y));
		return this;
	}

	public Rocket withProperty(final String key, final String value) {
		if(isRunning())
			throw new IllegalStateException("Rocket is running, can't add/modify properties");
		if (properties != null)
			properties.setProperty(key, value);
		else {
			properties = new Properties();
			properties.setProperty(key, value);
		}
		return this;
	}

	public static class RocketLogger {
		public static Logger LAZY = LoggerFactory.getLogger(RocketLogger.class);
	}

	private static class Builder {

		private static final Rocket rocket = new Rocket();

		static {
			rocket.properties = new Properties();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			try (InputStream resourceStream = loader.getResourceAsStream("default.properties")) {
				rocket.properties.load(resourceStream);
			} catch (IOException e) {
				RocketLogger.LAZY.warn("default.properties file not found");
			}
			if (!Habitat.getCurrentOS().equals(SupportedOS.UNKNOWN) && Habitat.getCurrentOS().isSupported()) {
				rocket.properties.setProperty(ROCKET_LOG_DIR_PROPERTY,
						rocket.properties.getProperty(Habitat.getCurrentOS().getlogFileProperty()));
			} else {
				rocket.properties.setProperty(ROCKET_LOG_DIR_PROPERTY, System.getProperty("user.dir"));
			}
		}

		private static Rocket getRocket() {
			return rocket;
		}
	}

	public Rocket withClass(final Class<?> clazz) {
		if(isRunning())
			throw new IllegalStateException("Rocket is already running, can't modify state");
		MAIN_CLASSES.add(clazz);
		return this;
	}

	public Rocket withClasses(final Class<?>... clazzes) {
		if(isRunning())
			throw new IllegalStateException("Rocket is already running, can't modify state");
		MAIN_CLASSES.addAll(Arrays.asList(clazzes));
		return this;
	}

	public Rocket withClasses(final Collection<? extends Class<?>> clazzes) {
		if(isRunning())
			throw new IllegalStateException("Rocket is already running, can't modify state");
		MAIN_CLASSES.addAll(clazzes);
		return this;
	}

	public Rocket withPackage(final String packageName) {
		if(isRunning())
			throw new IllegalStateException("Rocket is already running, can't modify state");
		MAIN_PACKAGES.add(packageName);
		return this;
	}

	public Rocket withPackages(final String... packages) {
		if(isRunning())
			throw new IllegalStateException("Rocket is already running, can't modify state");
		MAIN_PACKAGES.addAll(Arrays.asList(packages));
		return this;
	}

	public Rocket withPackages(final Set<String> packages) {
		if(isRunning())
			throw new IllegalStateException("Rocket is already running, can't modify state");
		MAIN_PACKAGES.addAll(packages);
		return this;
	}

	private void setExistFlag() {
		exitSignal.countDown();
	}

	public Rocket initialize() {
		if (!isinitialized) {
			initializeInternal();
			Rocket.isinitialized = !isinitialized;
		} else {
			RocketLogger.LAZY.warn("Rocket Already Initialized, Ignoring this request !!");
		}
		return this;
	}

	public Rocket start() {
		if (!isStarted) {
			context.start();
			isStarted = true;
		}
		return this;
	}

	public RocketPropertySource getPropertySource() {
		return this.propSource;
	}

	public void launchAndWait() {
		RocketLogger.LAZY.info("Rocket context starting...");
		if (!isStarted) {
			start();
		}
		System.exit(waitForExistSignal());
	}

	private void initializeInternal() {

		try {
			RocketPropertySource propSource = new RocketPropertySource(properties);
			this.propSource = propSource;
			originalSystemProperties = updateAndMergeSystemProperties(properties);
			RocketLogger.LAZY.info("Orignal system properties are : " + originalSystemProperties);
			RocketLogger.LAZY.info("rocket properties are : " + properties);
			RocketLogger.LAZY.info("Current os on which rocket is running {} ", Habitat.getCurrentOS());
			AsyncPostConstructBeanPostProcessor post = new AsyncPostConstructBeanPostProcessor();
			RocketSpringContext context = new RocketSpringContext(propSource, post);
			this.context = context;
			this.context.register(CamelConfiguration.class);
			this.context.register(CoreSpringConfiguration.class);
			RocketLogger.LAZY.info("Registered Classes with rocket are : " + MAIN_CLASSES);
			if (MAIN_CLASSES.size() > 0)
				MAIN_CLASSES.forEach((cls) -> {
					context.register(cls);
				});
			RocketLogger.LAZY.info("Registered Packages with rocket are : " + MAIN_PACKAGES);
			if (MAIN_PACKAGES.size() > 0)
				MAIN_PACKAGES.forEach((p) -> {
					context.scan(p);
				});
			context.refresh();
			context.waitForInitializationToComplete();
		} catch (Exception e) {
			RocketLogger.LAZY.error("Unexpected error occurred ", e);
			throw new RuntimeException(e);
		}
	}

	private Properties updateAndMergeSystemProperties(Properties properties) {

		Properties result = (Properties) System.getProperties().clone();
		for (Object p : properties.keySet()) {
			String prop = (String) p;
			System.setProperty(prop, properties.getProperty(prop));
		}
		return result;
	}

	private void registerRocketSignalHandler() {
		Result<SignalHandler> oldTermSh = new Result<>();
		RocketSignalHandler termSh = new RocketSignalHandler(oldTermSh);
		oldTermSh.setResult(Signal.handle(new Signal("TERM"), termSh));
		Result<SignalHandler> oldIntSh = new Result<>();
		RocketSignalHandler intSh = new RocketSignalHandler(oldIntSh);
		oldIntSh.setResult(Signal.handle(new Signal("INT"), intSh));
		if (Habitat.getCurrentOS() != SupportedOS.WINDOWS) {
			Result<SignalHandler> oldHupSh = new Result<>();
			RocketSignalHandler hupSh = new RocketSignalHandler(oldHupSh);
			oldHupSh.setResult(Signal.handle(new Signal("HUP"), hupSh));
		}
	}

	private int waitForExistSignal() {
		try {
			RocketLogger.LAZY.info("Rocket launched successfully...:)");
			exitSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return exitCode;
	}

	@Override
	public void close() throws Exception {
		if (this.context != null) {
			RocketLogger.LAZY.info("Rocket is stopping..:(");
			this.context.stop();
			this.context.close();
			this.context = null;
			System.getProperties().clear();
			System.getProperties().putAll(originalSystemProperties);
			setExistFlag();
		}

	}

	public static class RocketUncaughtExceptionHandlerImpl implements Thread.UncaughtExceptionHandler {

		private boolean shutdownOnError;

		public RocketUncaughtExceptionHandlerImpl(boolean shutdownOnError) {
			this.shutdownOnError = shutdownOnError;
		}

		@Override
		public void uncaughtException(Thread t, Throwable e) {
			try {
				if (RocketUtils.isNonRecoverableError(e)) {
					RocketLogger.LAZY.error("Shutting down", e);
					Rocket.configure().setExistFlag();
				}
			} finally {
				if (shutdownOnError) {
					RocketLogger.LAZY.error("Shutting down", e);
					Rocket.configure().setExistFlag();
				}
			}
		}
	}
}
