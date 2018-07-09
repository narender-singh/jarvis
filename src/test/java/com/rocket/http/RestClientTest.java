package com.rocket.http;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.Future;

import org.apache.cxf.transport.http.HTTPTransportFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.api.client.googleapis.util.Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.rocket.RocketLauncher;
import com.rocket.TestModel;
import com.rocket.core.http.MediaType;
import com.rocket.core.http.ResponseDetail;
import com.rocket.core.http.RestClient;

public class RestClientTest {

	// private static final Logger l =
	// LoggerFactory.getLogger(RestClientTest.class);

	private String url;

	@Before
	public void before() {
		RocketLauncher.Launch();
		url = "http://localhost:" + RocketLauncher.getRocket().getProperty("http.portNo") + "/test";
	}

	//@Test
	public void get() throws URISyntaxException, IOException {
		ResponseDetail<JsonNode> result = RestClient.get(url + "/get", MediaType.APPLICATION_JSON, JsonNode.class);
		Assert.assertEquals("GET", result.getContent().get("get").asText());
	}

	@Test
	public void put() throws URISyntaxException, IOException {
		TestModel model = new TestModel();
		model.setKey("PUT_UPDATED");
		ResponseDetail<String> data = RestClient.put(url + "/put", model, MediaType.APPLICATION_JSON, String.class);
		Assert.assertEquals("PUT_VALUE_UPDATED", data.getContent());
	}

	//@Test
	public void post() throws URISyntaxException, IOException {
		Map.Entry<String,String> node = new AbstractMap.SimpleEntry<>("patch", "PATCH_ADDED");		
		ResponseDetail<String> response = RestClient.post(url, node, MediaType.APPLICATION_JSON, String.class);
		Assert.assertEquals("PATCH_ADDED", response.getContent());
	}

	//@Test
	public void puttest() throws Exception{
		try {
			HttpRequestFactory factory = new NetHttpTransport().createRequestFactory();
			HttpRequest req = factory.buildPostRequest(new GenericUrl(url), new JsonHttpContent(Utils.getDefaultJsonFactory(), new AbstractMap.SimpleEntry<>("patch", "PATCH_ADDED")));
			HttpResponse response =  req.execute();
			String fResp = response.parseAs(String.class);			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
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
