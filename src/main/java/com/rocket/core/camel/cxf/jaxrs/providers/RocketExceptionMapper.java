package com.rocket.core.camel.cxf.jaxrs.providers;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class RocketExceptionMapper implements ExceptionMapper<Throwable> {

	private static final Boolean SEND_ERROR_MESSAGE_TO_CLIENT;

	private static final Logger l = LoggerFactory.getLogger(RocketExceptionMapper.class);

	static {
		SEND_ERROR_MESSAGE_TO_CLIENT = Boolean
				.parseBoolean(System.getProperty("rocket.sentErrorMessageToClient", "true"));
	}

	@Override
	public Response toResponse(Throwable exception) {

		l.error("Error Occurred - " + exception.getMessage(), exception);
		String response = "Unexcepted error in service.";
		if (SEND_ERROR_MESSAGE_TO_CLIENT)
			response += " Error Message : " + exception.getMessage();
		return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(response).build();
	}

}
