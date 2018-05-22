package com.rocket.core.utils;

import java.io.IOException;

public final class RocketUtils {

	private RocketUtils() {
	}

	public static void writeAbreviatedClassName(String className, Appendable app) throws IOException {
		int lx = className.lastIndexOf('.');
		if (lx < 0) {
			app.append(className);
			return;
		}
		char curr = className.charAt(0);
		app.append(curr).append('.');
		for (int i = 1; i < lx; i++) {
			curr = className.charAt(i);
			if (curr == '.')
				app.append(className.charAt(++i)).append('.');
		}
		app.append(className, lx + 1, className.length());
	}
}
