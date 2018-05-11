package com.banana.jarvis.core.camel;

import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

public class GenericInterceptor extends AbstractPhaseInterceptor<Message> {

	public GenericInterceptor() {
		super(Phase.PRE_INVOKE);
	}

	@Override
	public void handleMessage(Message message) throws Fault {
		
		for(String key : message.keySet())
		{
			System.out.println("key : " + key + "value : " + message.get(key));
		}
	}
}
