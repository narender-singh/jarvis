package com.rocket;

import java.util.AbstractMap;
import java.util.Map;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.camel.builder.RouteBuilder;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.rocket.core.Rocket;

public class RocketTest {

	private static final ObjectMapper OM = new ObjectMapper(); 
	
	@Test
	public void rocketbuildTest() throws Exception {
		Rocket r = Rocket.build().withClasses(Beans.class, Routes.class).initialize().start();
		String portNo = r.getProperty("http.portNo");
		HttpRequestFactory requestFactory = new NetHttpTransport().createRequestFactory();
		HttpRequest request = requestFactory.buildGetRequest(new GenericUrl("http://localhost:" + portNo + "/test"));
		JsonNode response = OM.readTree(request.execute().parseAsString());
		Assert.assertEquals("key",response.get("key").asText());
		Assert.assertEquals("value",response.get("value").asText());
		r.close();
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

		@Path("/")
		@GET
		@Produces({ MediaType.APPLICATION_JSON })
		public Map.Entry<String, String> test() {
			return new AbstractMap.SimpleEntry<String, String>("key", "value");
		}
	}

}
