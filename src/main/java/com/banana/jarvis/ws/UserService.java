package com.banana.jarvis.ws;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.banana.jarvis.core.RocketSpringContext;
import com.banana.jarvis.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectWriter;

@Path("/users")
@Produces({ MediaType.APPLICATION_JSON })
public class UserService {

	@Autowired
	private ObjectWriter objectWriter;
	private volatile int index = 0;

	List<User> users = new ArrayList<>();

	@GET
	@Path("/all")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getUsers() throws JsonProcessingException {
		users.add(new User(index++, "Narender", "App Infra"));
		return Response.status(200).entity(objectWriter.writeValueAsString(users)).build();
	}

	@POST
	@Path("/")
	@Produces({ MediaType.APPLICATION_JSON })
	@Consumes({ MediaType.APPLICATION_JSON })
	public Response addUser(User user) {
		users.add(user);
		return Response.status(204).build();
	}

	public void printUser() {
		System.out.println("printing User \n");
	}	
}
