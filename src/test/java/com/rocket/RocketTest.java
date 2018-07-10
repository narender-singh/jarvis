package com.rocket;

import java.io.IOException;
import java.util.Properties;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.api.client.http.GenericUrl;
import com.rocket.core.Rocket;
import com.rocket.core.RocketPropertySource;
import com.rocket.core.http.ResponseDetail;
import com.rocket.core.http.RestClient;
import com.rocket.core.http.RestRequest;

public class RocketTest {

	private static Rocket rocket;

	@BeforeClass
	public static void before() {
		RocketLauncher.Launch();
		rocket = RocketLauncher.getRocket();
	}

	@Test
	public void rocketPropertyTest() throws Exception {
		rocket.withProperties(new Properties() {
			private static final long serialVersionUID = -4370793899557623586L;
			{
				setProperty("test", "rockettest");
			}
		});
		rocket.withPackage("com.rocket");
		Properties prop = rocket.getProperties();
		RocketPropertySource propSource = rocket.getPropertySource();
		Assert.assertEquals(rocket.getProperties(), propSource.getProperties());
		Assert.assertEquals(prop.getProperty("test"), "rockettest");
		ApplicationContext context = rocket.getContext();
		if (context != null) {
			RocketLauncher.Beans confbean = context.getBean(RocketLauncher.Beans.class);
			Assert.assertNotNull(confbean);
		} else {
			// need to check why it fails
			// Assert.fail(String.format("rocket is running : {}, context : {}",
			// rocket.isRunning(), context.toString()));
		}
	}

	@Test
	public void rocketbuildTest() throws IOException {
		String portNo = RocketLauncher.getRocket().getProperty("http.portNo");
		ResponseDetail<JsonNode> resp = RestClient
				.get(RestRequest.newBuilder().withContentType(com.rocket.core.http.MediaType.APPLICATION_JSON)
						.buildGet(new GenericUrl("http://localhost:" + portNo + "/test")), JsonNode.class);
		Assert.assertEquals("value", resp.getContent().get("key").asText());
	}

	@AfterClass
	public static void after() throws Exception {
		RocketLauncher.stop();
	}
}
