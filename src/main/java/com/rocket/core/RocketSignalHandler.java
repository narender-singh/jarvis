package com.rocket.core;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class RocketSignalHandler implements SignalHandler {

	private static final Logger l = LoggerFactory.getLogger(RocketSignalHandler.class);

	private final Supplier<SignalHandler> oldHandler;

	public RocketSignalHandler(Supplier<SignalHandler> sigHandler) {
		this.oldHandler = sigHandler;
	}

	@Override
	public void handle(Signal sig) {
		try {
			l.info("Signal Received : " + sig);
		} catch (Throwable t) {
			SignalHandler old = oldHandler.get();
			old.handle(sig);
			return;
		}
		oldHandler.get().handle(sig);
	}

	@Override
	public String toString() {
		return "RocketSingalHandler{ oldHandler=" + oldHandler + '}';
	}
}
