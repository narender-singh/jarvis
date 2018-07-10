//package com.rocket.http;
//
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.util.AbstractMap;
//import java.util.Map;
//
//import org.junit.AfterClass;
//import org.junit.Assert;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.rocket.RocketLauncher;
//import com.rocket.RocketTest;
//import com.rocket.core.http.MediaType;
//import com.rocket.core.http.ResponseDetail;
//import com.rocket.core.http.RestClient;
//
//public class RestClientTest {
//
//	//private static String url;
//	protected static RocketLauncher launcher;
//
//	//@BeforeClass
//	public static void before() throws InterruptedException {
//		Thread.sleep(20000);
//		launcher = new RocketLauncher();
//		launcher.Launch();
//	//	url = "http://localhost:" + launcher.getRocket().getProperty("http.portNo") + "/test";
//	}
//
//	
//	// @Test
//	// public void delete() throws URISyntaxException, IOException {
//	// RestClient.get(url, MediaType.APPLICATION_JSON, JsonNode.class);
//	// }
//
//	//@AfterClass
//	public static void after() throws Exception {
//		launcher.stop();
//	}
//}
