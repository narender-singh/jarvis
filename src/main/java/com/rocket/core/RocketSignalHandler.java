package com.rocket.core;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sun.misc.Signal;
import sun.misc.SignalHandler;

public class RocketSignalHandler implements SignalHandler {

	private static final Logger l = LoggerFactory.getLogger(RocketSignalHandler.class);

	private final Future<SignalHandler> oldHandler;

	public RocketSignalHandler(Future<SignalHandler> sigHandler) {
		this.oldHandler = sigHandler;
	}

	@Override
	public void handle(Signal sig) {
		try {
			l.info("Signal Received : " + sig);
		} catch (Throwable t) {
			try {
				SignalHandler old = oldHandler.get();
				old.handle(sig);
			} catch (InterruptedException | ExecutionException e) {
				Error er = new Error(e);
				er.addSuppressed(t);
				throw er;
			}
			return;
		}
		try {
			oldHandler.get().handle(sig);
		} catch (InterruptedException | ExecutionException e) {
			throw new Error(e);
		}
	}

	@Override
	public String toString() {
		return "RocketSingalHandler{ oldHandler=" + oldHandler + '}';
	}
}
