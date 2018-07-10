package com.rocket.core.camel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReceiveInterceptor extends AbstractPhaseInterceptor<Message> {

	private static final Logger l = LoggerFactory.getLogger(ReceiveInterceptor.class);

	public ReceiveInterceptor() {
		super(Phase.RECEIVE);
	}

	@Override
	public void handleMessage(Message message) throws Fault {		
		InputStream content = message.getContent(InputStream.class);
		if (content != null) {
			try {
				logInputStreamContent(content);
				content.close();				
			} catch (IOException e) {
				l.error("Error", e);
			}
		} else {
			Reader readContent = message.getContent(Reader.class);
			logReaderContent(readContent);
		}
	}

	private static void logReaderContent(Reader readContent) {
		if (readContent != null)
			l.info("request content : {}", readContent);
	}

	private static void logInputStreamContent(InputStream content) throws IOException {
		String read = null;
		StringBuilder finalString = new StringBuilder();
		BufferedReader reader = new BufferedReader(new InputStreamReader(content));
		if ((read = reader.readLine()) != null) {
			finalString.append(read).append('\n');
		}
		l.info("request content stream : {}", finalString);

	}

}
