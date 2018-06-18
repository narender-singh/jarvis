package com.rocket.core.camel;

import java.util.List;

import javax.annotation.Nonnull;

import org.springframework.context.ApplicationContext;

public interface UserJaxRsProvider {

	List<Object> getProviders(@Nonnull ApplicationContext context);
}
