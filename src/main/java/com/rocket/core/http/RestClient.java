package com.rocket.core.http;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.rocket.core.Habitat;

public final class RestClient {

	public static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	public static final String USER_AGENT = "RocketRestClient-"
			+ RestClient.class.getPackage().getImplementationVersion();
	public static final String USER_APP = Habitat.getAppName();
	public static final String USER = Habitat.getUser();
	private static final Logger LOG = LoggerFactory.getLogger(RestClient.class);
	private static final HttpRequestFactory HTTP_REQUEST_FACTORY = HTTP_TRANSPORT
			.createRequestFactory((final HttpRequest request) -> {
				request.setThrowExceptionOnExecuteError(false);
				final HttpHeaders headers = request.getHeaders();
				headers.setUserAgent(USER_AGENT);
				headers.set(Headers.FROM, USER);
				headers.set(Headers.USER_APP, USER_APP);
			});
}
