package com.rocket.core.http;

import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.rocket.core.Habitat;
import com.rocket.core.StaticHolder;

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

	private static final MediaType DEFAULT_MEDIATYPE = MediaType.APPLICATION_JSON;
	private static final int DEFAULT_TIMEOUT_MILLIS = 60000;

	private static final int ARRAY_START = "[".getBytes(StandardCharsets.UTF_8)[0] & 0xff;

	private RestClient() {
	}

	public static HttpResponse get(final String url) throws URISyntaxException {
		return get(url, DEFAULT_MEDIATYPE);
	}

	public static HttpResponse get(final String url, final MediaType contentType) throws URISyntaxException {
		return get(RestRequest.newBuilder().withContentType(contentType).buildGet(new GenericUrl(new URI(url))), null);
	}

	public static <RESP> RESP get(final String url, final Class<RESP> returnType) throws URISyntaxException {
		return get(url, DEFAULT_MEDIATYPE, DEFAULT_TIMEOUT_MILLIS, returnType);
	}

	public static <RESP> RESP get(final String url, final MediaType contentType, final Class<RESP> returnType)
			throws URISyntaxException {
		return get(url, contentType, DEFAULT_TIMEOUT_MILLIS, returnType);
	}

	public static <RESP> RESP get(final String url, final MediaType contentType, final int timeOutMillis,
			final Class<RESP> returnType) throws URISyntaxException {
		return get(RestRequest.newBuilder().withContentType(contentType).withTimeOutMillis(timeOutMillis)
				.buildGet(new GenericUrl(new URI(url))), returnType);
	}

	public static <REQ, RESP> RESP get(RestRequest<REQ> req, Class<RESP> returnType) {
		return null;
	}

	public static <RESP, REQ> Future<RESP> doHttpRequest(RestRequest<REQ> request, Class<RESP> returnType)
			throws IOException {

		HttpContent content = null;
		HttpHeaders reqheaders = null;
		HttpRequest http = null;
		switch (request.getMethod()) {
		case GET:
			http = HTTP_REQUEST_FACTORY.buildGetRequest(request.getUrl());
			reqheaders = http.getHeaders();
			break;
		case POST:
		case PUT:
			break;
		case OPTIONS:
			http = HTTP_REQUEST_FACTORY.buildRequest("OPTIONS", request.getUrl(), null);
			reqheaders = http.getHeaders();
			break;
		case DELETE:
			http = HTTP_REQUEST_FACTORY.buildDeleteRequest(request.getUrl());
			reqheaders = http.getHeaders();
			break;
		default:
			throw new RuntimeException("Unsupported or unknown http method");
		}

		return StaticHolder.getDefaultExecutor().submit(new RequestCallable<REQ, RESP>(request, http, returnType));
	}

	private static class RequestCallable<REQ, RESP> implements Callable<RESP> {

		private final HttpRequest httpRequest;
		private final RestRequest<REQ> restRequest;
		private final Class<RESP> returnType;

		public RequestCallable(RestRequest<REQ> restReq, HttpRequest req, Class<RESP> responseType) {
			this.httpRequest = req;
			this.restRequest = restReq;
			this.returnType = responseType;
		}

		@Override
		public RESP call() throws Exception {
			return null;
		}
	}

}
