package com.rocket.http;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rocket.RocketLauncher;
import com.rocket.core.http.MediaType;
import com.rocket.core.http.ResponseDetail;
import com.rocket.core.http.RestClient;

public class RestClientTest {

	private String url;

	@Before
	public void before() {
		RocketLauncher.Launch();
		url = "http://localhost:" + RocketLauncher.getRocket().getProperty("http.portNo") + "/test";
	}

	@Test
	public void get() throws URISyntaxException, IOException {
		ResponseDetail<JsonNode> result = RestClient.get(url + "/get", MediaType.APPLICATION_JSON, JsonNode.class);
		Assert.assertEquals("GET", result.getContent().get("get").asText());
	}

	//@Test
	public void put() throws URISyntaxException, IOException {
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		node.put("put", "PUT_VALUE_UPDATED");
		ResponseDetail<String> data = RestClient.put(url, node, MediaType.APPLICATION_JSON, String.class);
		Assert.assertEquals("PUT_VALUE_UPDATED", data.getContent());
	}

	//@Test
	public void post() throws URISyntaxException, IOException {
		ObjectNode node = JsonNodeFactory.instance.objectNode();
		node.put("patch", "PATCH_ADDED");
		ResponseDetail<String> response = RestClient.post(url, node, MediaType.APPLICATION_JSON, String.class);
		Assert.assertEquals("PATCH_ADDED", response.getContent());
	}

	// @Test
	// public void delete() throws URISyntaxException, IOException {
	// RestClient.get(url, MediaType.APPLICATION_JSON, JsonNode.class);
	// }

	@After
	public void after() throws Exception {
		RocketLauncher.stop();
	}
}
