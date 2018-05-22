package com.rocket.core.camel;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

public class GenericInterceptor extends AbstractPhaseInterceptor<Message> {

	public Logger l = LoggerFactory.getLogger(GenericInterceptor.class);
	
	public GenericInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		
		for(String key : message.keySet())
		{
			l.info("key : " + key + "value : " + message.get(key));			
		}
	}
}
