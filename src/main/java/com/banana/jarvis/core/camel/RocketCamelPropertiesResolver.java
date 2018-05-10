package com.banana.jarvis.core.camel;

import java.util.List;
import java.util.Properties;

import org.apache.camel.CamelContext;
import org.apache.camel.component.properties.PropertiesLocation;
import org.apache.camel.component.properties.PropertiesResolver;

public class RocketCamelPropertiesResolver implements PropertiesResolver {

	private Properties properties;

	public RocketCamelPropertiesResolver(final Properties props) {
		this.properties = props;
	}

	@Override
	public Properties resolveProperties(CamelContext context, boolean ignoreMissingLocation,
			List<PropertiesLocation> locations) throws Exception {
		return properties;
	}

	@Override
	public String toString() {
		return "RocketCamelPropertiesResolver{ properties=" + properties + " }";
	}

}
