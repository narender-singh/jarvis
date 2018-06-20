package com.rocket.core;

import java.lang.management.ManagementFactory;

public class Habitat {

	private static volatile String os;
	private static volatile SupportedOS currentOS;
	private static volatile String appName;
	private static volatile String user;
	private static volatile String logDir;
	private static volatile int processId;
	private static volatile Env env = Env.UNKNOWN;

	static {
		appName = System.getProperty(Rocket.ROCKET_APPNAME);
		os = System.getProperty("os.name");
		currentOS = SupportedOS.parse(os);
		user = System.getProperty("user.name");
		logDir = System.getProperty(currentOS.getlogFileProperty());
		final String jvmName = ManagementFactory.getRuntimeMXBean().getName();
		final int index = jvmName.indexOf('@');
		if (index < 1) {
			processId = -1;
		} else {
			try {
				processId = Integer.parseInt(jvmName.substring(0, index));
			} catch (NumberFormatException e) {
				processId = -1;
			}
		}
		final String envString = System.getProperty("rocket.env");
		if (envString != null)
			env = Env.parse(envString);
	}

	public static void setEnv(Env e) {
		env = e;
	}

	public static String getOs() {
		return os;
	}

	public static SupportedOS getCurrentOS() {
		return currentOS;
	}

	public static String getAppName() {
		return appName;
	}

	public static String getUser() {
		return user;
	}

	public static String getLogDir() {
		return logDir;
	}

	public static int getProcessId() {
		return processId;
	}

	public static Env getEnv() {
		return env;
	}

}
