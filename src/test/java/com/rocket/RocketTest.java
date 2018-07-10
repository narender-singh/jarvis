package com.rocket;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.Map;
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
import com.rocket.core.http.MediaType;
import com.rocket.core.http.ResponseDetail;
import com.rocket.core.http.RestClient;
import com.rocket.core.http.RestRequest;

public class RocketTest {

	protected static Rocket rocket;
	protected static String url;
	protected static RocketLauncher launcher;

	@BeforeClass
	public static void before() throws InterruptedException {
		Thread.sleep(20000);
		launcher = new RocketLauncher();
		launcher.Launch();
		rocket = launcher.getRocket();
		url = "http://localhost:" + launcher.getRocket().getProperty("http.portNo") + "/test";
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
	public void get() throws URISyntaxException, IOException {
		ResponseDetail<JsonNode> result = RestClient.get(url + "/get", MediaType.APPLICATION_JSON, JsonNode.class);
		Assert.assertEquals("GET", result.getContent().get("get").asText());
	}

	@Test
	public void put() throws URISyntaxException, IOException {
		Map.Entry<String, String> model = new AbstractMap.SimpleEntry<>("put", "PUT_VALUE_UPDATED");
		ResponseDetail<String> data = RestClient.put(url + "/put", model, MediaType.APPLICATION_JSON, String.class);
		Assert.assertEquals("PUT_VALUE_UPDATED", data.getContent());
	}

	@Test
	public void post() throws URISyntaxException, IOException {
		Map.Entry<String, String> node = new AbstractMap.SimpleEntry<>("patch", "PATCH_ADDED");
		ResponseDetail<String> response = RestClient.post(url, node, MediaType.APPLICATION_JSON, String.class);
		Assert.assertEquals("PATCH_ADDED", response.getContent());
	}


	@Test
	public void rocketbuildTest() throws IOException {
		String portNo = launcher.getRocket().getProperty("http.portNo");
		ResponseDetail<JsonNode> resp = RestClient
				.get(RestRequest.newBuilder().withContentType(com.rocket.core.http.MediaType.APPLICATION_JSON)
						.buildGet(new GenericUrl("http://localhost:" + portNo + "/test")), JsonNode.class);
		Assert.assertEquals("value", resp.getContent().get("key").asText());
	}

	@AfterClass
	public static void after() throws Exception {
		launcher.stop();
	}
}
