package com.rocket.core.camel;

import java.io.InputStream;
import java.io.Reader;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

public class ReceiveInterceptor extends AbstractPhaseInterceptor<Message> {

	public ReceiveInterceptor() {
		super(Phase.RECEIVE);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		InputStream content = message.getContent(InputStream.class);
		if(content != null){
			logInputStreamContent(content);
		}
		else{
			Reader readContent = message.getContent(Reader.class);
			logReaderContent(readContent);
		}
		
	}

	private static void logReaderContent(Reader readContent) {
		
	}

	private static void logInputStreamContent(InputStream content) {

		
	}

}
