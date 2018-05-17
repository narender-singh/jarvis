package com.rocket.core.camel;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;

@Provider
@Produces({ MediaType.WILDCARD })
@Consumes({ MediaType.WILDCARD })
public class JsonProvider extends JacksonJsonProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object> {

}
