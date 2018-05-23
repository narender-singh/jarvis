package com.rocket.core.logging;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.LayoutBase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rocket.core.utils.RocketUtils;

public class RLogFormatter extends LayoutBase<ILoggingEvent> {

	private static final DateTimeFormatter D_F = DateTimeFormatter.ISO_DATE_TIME.withZone(ZoneId.of("Asia/Kolkata"));

	/*private static final ObjectWriter JSON_WRITER;

	static {
		JSON_WRITER = (new ObjectMapper()).writer();
	}*/

	public static final ThreadLocal<StringBuilder> S_B = new ThreadLocal<StringBuilder>() {
		@Override
		protected StringBuilder initialValue() {
			return new StringBuilder(256);
		};
	};

	public static StringBuilder getSB() {
		StringBuilder result = S_B.get();
		if (result == null) {
			result = new StringBuilder(256);
		} else {
			result.setLength(0);
		}
		return result;

	}

	@Override
	public String doLayout(ILoggingEvent event) {
		try {
			StringBuilder result = getSB();
			result.append("{\"ts\": \"");
			result.append(D_F.format(Instant.ofEpochMilli(event.getTimeStamp())));
			result.append("\", \"tid\": \"");
			result.append(event.getThreadName());
			result.append("\", \"level\": ");
			result.append(event.getLevel());			
			try {
				result.append("\", \"class\": ");
				RocketUtils.writeAbreviatedClassName(event.getLoggerName(), result);
				result.append('\"');
				Set<? extends Throwable> throwables = writeMessage(event, result);
				if (throwables.size() > 0)
					appendThrowables(event, throwables, result);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			result.append("}\n");
			String log = result.toString();
			recycleSB(result);
			return log;
		} catch (Exception e) {
			return null;
		}
	}

	private static void appendThrowables(ILoggingEvent event, Set<? extends Throwable> throwables, StringBuilder res)
			throws IOException {

		IThrowableProxy proxy = event.getThrowableProxy();
		Throwable th = null;
		if (proxy instanceof ThrowableProxy) {
			th = ((ThrowableProxy) proxy).getThrowable();
		} else {
			Object[] arr = event.getArgumentArray();
			if (arr[arr.length - 1] instanceof Throwable) {
				th = (Throwable) arr[arr.length - 1];
				proxy = new ThrowableProxy(th);
			}
		}
		boolean enrichedThrowable = false;
		if (proxy != null && th != null) {
			for (Throwable object : throwables) {
				if (object != th) {
					th.addSuppressed(object);
					enrichedThrowable = true;
				}
			}
			if (enrichedThrowable)
				proxy = new ThrowableProxy(th);
			res.append(", \"exception\":\"");
			addExceptionInfo(res, proxy);
			res.append("\",").append('\n').append("\"stack\":[");
			writeStackArray(res, proxy);
			res.append(']');
			final IThrowableProxy[] supp = proxy.getSuppressed();
			if (supp != null && supp.length > 0) {
				final IThrowableProxy s1 = supp[0];
				res.append(',').append('\n').append("\"suppressed\":\"");
				addExceptionInfo(res, s1);
				res.append("\", \"suppressedStack\":[");
				writeStackArray(res, s1);
				res.append(']');
			}

		}
	}

	private static void writeStackArray(final StringBuilder result, final IThrowableProxy throwableProxy) {
		StackTraceElementProxy[] stack = throwableProxy.getStackTraceElementProxyArray();
		int limit = Math.min(stack.length - throwableProxy.getCommonFrames() + 1, stack.length);
		for (int i = 0; i < limit; ++i) {
			StackTraceElementProxy proxy = stack[i];
			if (i > 0) {
				result.append(",\n\t\"");
			} else {
				result.append("\n\t\"");
			}
			final StackTraceElement stackTraceElement = proxy.getStackTraceElement();
			result.append(stackTraceElement.toString());
			result.append('\"');
		}
		IThrowableProxy cause = throwableProxy.getCause();
		if (cause != null) {
			result.append(",\n\"Caused by: ");
			addExceptionInfo(result, cause);
			result.append("\",");
			writeStackArray(result, cause);
		}
	}

	private static void addExceptionInfo(final StringBuilder result, final IThrowableProxy e) {
		String msg = e.getMessage();
		result.append(e.getClassName()).append(": ");
		result.append(null == msg ? "<Null>" : msg);
	}

	private static void recycleSB(StringBuilder result) {
		if (result.length() > 8192) {
			S_B.set(null);
		}
	}

	private static Set<Throwable> writeMessage(ILoggingEvent event, StringBuilder app) throws IOException {
		Object[] arg = event.getArgumentArray();
		String msg = event.getMessage();
		if (msg != null) {
			app.append(",\"msg\": \"").append(event.getFormattedMessage()).append('\"');
		}
		if (arg == null || arg.length == 0) {
			return Collections.emptySet();
		} else {
			int extra = 0;
			Set<Throwable> exceptions = new HashSet<>();
			for (Object o : arg) {
				if (o instanceof Throwable) {
					exceptions.add((Throwable) o);
				} else if (o instanceof Map || o instanceof Map.Entry) {
					app.append(",\"extras" + extra++ + " \":").append(o.toString());
				} else {
					app.append(",\"extras" + extra++ + " \": \"" + o + '\"');
				}
			}
			return exceptions;
		}

	}

}
