package com.rocket.core;

public enum Env {
	DEV("dev"), ALPHA("alpha"), BETA("beta"), DELTA("delta"), PROD("prod"), SANDBOX("sandbox"), DR("dr"), UNKNOWN(
			"unknown");

	private final String code;

	Env(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public static Env parse(String code) {
		if (code == null)
			return UNKNOWN;
		for (Env e : Env.values()) {
			if (e.code.equals(code))
				return e;
		}
		return UNKNOWN;
	}

}