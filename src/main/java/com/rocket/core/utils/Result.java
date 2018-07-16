package com.rocket.core.utils;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Result<V> implements Future<V> {

	private volatile V result;

	private volatile boolean isCanceled;

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (isDone())
			return false;
		else {
			isCanceled = true;
			return true;
		}
	}

	@Override
	public boolean isCancelled() {
		return isCanceled;
	}

	@Override
	public boolean isDone() {
		return isCanceled | result != null;
	}

	@Override
	public V get() throws InterruptedException, ExecutionException {
		while (result == null) {
			this.wait();
		}
		return result;
	}

	@Override
	public V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {

		long timeOutMillis = unit.toMillis(timeout);
		long toWait = timeOutMillis;
		long startTime = Instant.now().toEpochMilli();
		synchronized (this) {
			while (toWait > 0 && result == null) {
				this.wait(toWait);
				toWait = timeOutMillis - (Instant.now().toEpochMilli() - startTime);
			}
			if (result == null)
				throw new TimeoutException();
			return result;
		}
	}

}
