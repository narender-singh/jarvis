package org.apache.camel.component.jetty;

import org.apache.camel.component.jetty9.JettyHttpComponent9;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RocketJettyComponent extends JettyHttpComponent9 {

	private static final Logger l = LoggerFactory.getLogger(RocketJettyComponent.class);

	private static final int DEFAULT_THREAD_COUNT;

	static {
		DEFAULT_THREAD_COUNT = 1000;
		l.info("Default Thread Count of RocketJettyComponent is {}", DEFAULT_THREAD_COUNT);
	}

}
