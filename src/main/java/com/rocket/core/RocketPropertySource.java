package com.rocket.core;

import java.util.Properties;

import org.springframework.core.env.PropertySource;
import org.springframework.util.PropertyPlaceholderHelper;

public class RocketPropertySource extends PropertySource<String>
		implements PropertyPlaceholderHelper.PlaceholderResolver {

	private final Properties properties;

	public RocketPropertySource(final Properties props) {
		super("rocket");
		this.properties = props;
	}

	@Override
	public String resolvePlaceholder(String key) {
		return properties.getProperty(key);
	}

	@Override
	public Object getProperty(String key) {
		return properties.getProperty(key);
	}

	public Properties getProperties() {
		return properties;
	}
}
