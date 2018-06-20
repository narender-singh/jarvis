package com.rocket.core;

public enum SupportedOS {
	WINDOWS("Windows", true, "rocket.win.logdir"), MACOS("Mac", false, "rocket.macos.logdir"), LINUX("Linux", true,
			"rocket.linux.logdir"), SOLARIS("SunOS", true, "rocket.sunos.logdir"), UNKNOWN("unknown", false, "");

	private String name;
	private String logfileProperty;
	private boolean isSupported;

	private SupportedOS(String name, boolean isSupported, String logfile) {
		this.name = name;
		this.isSupported = isSupported;
		this.logfileProperty = logfile;
	}

	public String getName() {
		return name;
	}

	public static SupportedOS parse(String os) {
		if (os == null)
			return UNKNOWN;
		for (SupportedOS c : SupportedOS.values()) {
			if (os.startsWith(c.name))
				return c;
		}
		return UNKNOWN;
	}

	public boolean isSupported() {
		return isSupported;
	}

	public String getlogFileProperty() {
		return logfileProperty;
	}

}
