package com.banana.jarvis.core;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.type.MethodMetadata;

import com.banana.jarvis.core.annotation.InstantiateFirst;
import com.banana.jarvis.core.utils.StringIntPair;

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

	public RocketPropertySource getPropsSource() {
		return propertySource;
	}

	@Override
	protected void onRefresh() throws BeansException {
		ConfigurableListableBeanFactory beanFactory = this.getBeanFactory();
		beanFactory.addBeanPostProcessor(postProcessor);
		String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
	    PriorityQueue<StringIntPair> ordered = new PriorityQueue<>(4, StringIntPair.NR_COMPARATOR);
	    String annName = InstantiateFirst.class.getName();
	    for (String name : beanDefinitionNames) {
	      BeanDefinition beanDefinition = beanFactory.getBeanDefinition(name);
	      if (beanDefinition instanceof AnnotatedBeanDefinition) {
	        AnnotatedBeanDefinition abd = (AnnotatedBeanDefinition) beanDefinition;
	        MethodMetadata factoryMethodMetadata = abd.getFactoryMethodMetadata();
	        if (factoryMethodMetadata != null) {
	          Map<String, Object> annotationAttributes = factoryMethodMetadata.getAnnotationAttributes(annName);
	          if (annotationAttributes != null) {
	            Integer priority = (Integer) annotationAttributes.get("priority");
	            ordered.add(new StringIntPair(priority, name));
	          } else {
	            InstantiateFirst annotation = beanFactory.findAnnotationOnBean(name, InstantiateFirst.class);
	            if (annotation != null) {
	              ordered.add(new StringIntPair(annotation.priority(), name));
	            }

	          }
	        } else {
	          InstantiateFirst annotation = beanFactory.findAnnotationOnBean(name, InstantiateFirst.class);
	          if (annotation != null) {
	            ordered.add(new StringIntPair(annotation.priority(), name));
	          }
	        }
	      }

	    }
	    for (StringIntPair name : ordered) {
	      this.getBean(name.getString());
	    }
		super.onRefresh();
	}

	public void waitForInitializationToComplete() {
		postProcessor.waitForInitializationToComplete(600L, TimeUnit.SECONDS);
	}
}
