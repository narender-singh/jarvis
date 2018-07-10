package com.rocket.http;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.rocket.RocketLauncher;
import com.rocket.RocketTest;
import com.rocket.core.http.MediaType;
import com.rocket.core.http.ResponseDetail;
import com.rocket.core.http.RestClient;

public class RestClientTest extends RocketTest {

	// private static final Logger l =
	// LoggerFactory.getLogger(RestClientTest.class);

	// private static String url;

	// @BeforeClass
	public static void before() {
		RocketLauncher.Launch();
		// url = "http://localhost:" +
		// RocketLauncher.getRocket().getProperty("http.portNo") + "/test";
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

	// @Test
	// public void delete() throws URISyntaxException, IOException {
	// RestClient.get(url, MediaType.APPLICATION_JSON, JsonNode.class);
	// }

	@AfterClass
	public static void after() throws Exception {
		RocketLauncher.stop();
	}
}
