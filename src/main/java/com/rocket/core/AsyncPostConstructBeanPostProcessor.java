package com.rocket.core;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import com.rocket.core.annotation.AsyncPostConstruct;

public class AsyncPostConstructBeanPostProcessor implements BeanPostProcessor {

	Map<Object, Future<Object>> asyncInitializations;

	public AsyncPostConstructBeanPostProcessor() {
		asyncInitializations = new HashMap<>();
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Method[] methods = bean.getClass().getMethods();
		for (final Method method : methods) {
			if (method.getAnnotation(AsyncPostConstruct.class) != null) {
				if (method.getParameterCount() > 0) {
					throw new BeanInitializationException(
							"AsyncPostConstruct  cannot have parameters for" + beanName + ", method " + method);
				} else {
					asyncInitializations.put(bean, StaticHolder.getDefaultExecutor().submit(() -> {
						return method.invoke(bean);
					}));
				}
			}
		}
		return bean;
	}

	public void waitForInitializationToComplete(long l, TimeUnit seconds) {
		for (Future<Object> f : asyncInitializations.values()) {
			try {
				f.get(l, seconds);
			} catch (InterruptedException | ExecutionException | TimeoutException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

}
