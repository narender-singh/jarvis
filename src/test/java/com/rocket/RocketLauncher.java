package com.rocket;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.camel.Consume;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rocket.core.Rocket;

public class RocketLauncher {

	private static Rocket rocket;

	static {

	}

	public static void Launch() {
		rocket = Rocket.build().withClasses(Beans.class, Routes.class).initialize().start();
	}

	public static void stop() throws Exception {
		rocket.close();
	}

	public static Rocket getRocket() {
		return rocket;
	}

	@Configuration
	public static class Beans {

		@Bean
		WS webService() {
			return new WS();
		}
	}

	public static class Routes extends RouteBuilder {

		@Override
		public void configure() throws Exception {
			//
			from("jetty:http://0.0.0.0:{{http.portNo}}/test?matchOnUriPrefix=true").to("cxfbean:webService");
		}

	}

	@Path("/")
	public static class WS {

		private static final Map<String, String> TEST_DATA = new HashMap<>();

		{
			TEST_DATA.put("get", "GET");
			TEST_DATA.put("put", "PUT");
			TEST_DATA.put("post", "POST");
			TEST_DATA.put("delete", "DELETE");
			TEST_DATA.put("options", "OPTIONS");
			TEST_DATA.put("head", "HEAD");
			TEST_DATA.put("key", "value");
		}

		@Path("/")
		@GET
		@Produces({ MediaType.APPLICATION_JSON })
		public Map.Entry<String, String> getDummy() {
			return TEST_DATA.entrySet().stream().filter(x -> x.getKey().equals("key")).findFirst().get();
		}

		@Path("/get")
		@GET
		@Produces({ MediaType.APPLICATION_JSON })
		public Map<String, String> get() {
			return TEST_DATA;
		}

		@Path("/put")
		@PUT
		@Produces({ MediaType.TEXT_PLAIN })
		@Consumes({ MediaType.APPLICATION_JSON })
		public String put(Map.Entry<String, String> s) {
			if (TEST_DATA.containsKey(s.getKey())) {
				TEST_DATA.put(s.getKey(), s.getValue());
				return s.getValue();
			}
			TEST_DATA.put(s.getKey(), s.getValue());
			return s.getValue();
		}

		@Path("/")
		@POST
		@Produces({ MediaType.TEXT_PLAIN })
		@Consumes({ MediaType.APPLICATION_JSON })
		public String post(Map.Entry<String, String> s) {
			TEST_DATA.put(s.getKey(), s.getValue());
			return s.getValue();
		}

		@Path("/")
		@DELETE
		@Produces({ MediaType.TEXT_PLAIN })
		@Consumes({ MediaType.TEXT_PLAIN })
		public String delete(String s) {
			return TEST_DATA.remove(s);
		}

		// @Path("/head")
		// @POST
		// @Produces( {MediaType.APPLICATION_JSON})
		// @Consumes( {MediaType.APPLICATION_JSON})
		// public String head(){
		//
		// }
		//
		// @Path("/options")
		// @POST
		// @Produces( {MediaType.APPLICATION_JSON})
		// @Consumes( {MediaType.APPLICATION_JSON})
		// public String options(){
		//
		// }

	}
}
