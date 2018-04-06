package com.banana.jarvis.core;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.MutablePropertySources;

public class RocketSpringContext extends AnnotationConfigApplicationContext {

	private RocketPropertySource propertySource;
	private AsyncPostConstructBeanPostProcessor postProcessor;

	public RocketSpringContext(RocketPropertySource props, AsyncPostConstructBeanPostProcessor app) {
		this.propertySource = props;
		this.postProcessor = app;
		AbstractEnvironment rocketEnv = new AbstractEnvironment() {
			@Override
			protected void customizePropertySources(MutablePropertySources propertySources) {
				propertySources.addFirst(propertySource);
			}
		};
		this.setEnvironment(rocketEnv);
		PropertySourcesPlaceholderConfigurer placeholderConfiguer = new PropertySourcesPlaceholderConfigurer();
		placeholderConfiguer.setEnvironment(rocketEnv);
		placeholderConfiguer.setPropertySources(rocketEnv.getPropertySources());
		placeholderConfiguer.setIgnoreUnresolvablePlaceholders(true);
		addBeanFactoryPostProcessor(placeholderConfiguer);

	}

	@Override
	protected void onRefresh() throws BeansException {
		ConfigurableListableBeanFactory factory = this.getBeanFactory();
		factory.addBeanPostProcessor(postProcessor);
		super.onRefresh();
	}

	public void waitForInitializationToComplete() {
		postProcessor.waitForInitializationToComplete(600L, TimeUnit.SECONDS);
	}
}
