package com.rocket.core.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jetty.http.HttpMethod;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.util.Maps;

public class RestRequest<REQ> implements JsonSerializable {

	public static final int DEFAULT_TIMEOUT_MILLISECONDS = 60000;

	private final REQ requestObject;

	private final GenericUrl url;

	private final int timeoutMillis;
	private final MediaType contentType;
	private final HttpMethod method;
	private final Map<String, List<String>> userHeaders;

	public RestRequest(final GenericUrl url) {
		this(url, HttpMethod.GET, MediaType.APPLICATION_JSON, null, DEFAULT_TIMEOUT_MILLISECONDS, null);
	}

	public RestRequest(final GenericUrl url, final HttpMethod method, final REQ requestObject) {
		this(url, method, MediaType.APPLICATION_JSON, null, DEFAULT_TIMEOUT_MILLISECONDS, requestObject);
	}

	public RestRequest(final GenericUrl url, final HttpMethod method, final MediaType type, final REQ requestObject) {
		this(url, method, type, null, DEFAULT_TIMEOUT_MILLISECONDS, requestObject);
	}

	public RestRequest(final GenericUrl url, final HttpMethod method, final MediaType type,
			final Map<String, List<String>> headers, final REQ requestObject) {
		this(url, method, type, null, DEFAULT_TIMEOUT_MILLISECONDS, requestObject);
	}

	public RestRequest(final GenericUrl url, final HttpMethod method, final MediaType type,
			final Map<String, List<String>> headers, final int timeoutMillis, final REQ requestObject) {
		if (!(method.equals(HttpMethod.POST) || method.equals(HttpMethod.PUT)) && requestObject != null)
			throw new IllegalArgumentException("Cannot have request content for method" + method);
		if (url == null)
			throw new IllegalArgumentException("Url is a mandatory parameter");
		this.url = url;
		this.method = method;
		this.contentType = type;
		this.userHeaders = headers;
		this.requestObject = requestObject;
		this.timeoutMillis = timeoutMillis;

	}

	public static RestRequest.Builder<Void> newBuilder() {
		return new Builder<>();
	}

	public static class Builder<REQ> {
		private GenericUrl url;

		private int timeoutMillis;
		private MediaType contentType;
		private HttpMethod method;
		private Map<String, List<String>> userHeaders = Maps.newHashMap();
		private REQ requestObject;

		public Builder() {
			timeoutMillis = 60000;
			contentType = MediaType.APPLICATION_JSON;
			method = HttpMethod.GET;
		}

		public Builder<REQ> withUrl(GenericUrl url) {
			this.url = url;
			return this;
		}

		public Builder<REQ> withContentType(MediaType contentType) {
			this.contentType = contentType;
			return this;
		}

		public Builder<REQ> withMethod(HttpMethod method) {
			this.method = method;
			return this;
		}

		public Builder<REQ> withHeaders(Map<String, List<String>> headers) {
			userHeaders.putAll(headers);
			return this;
		}

		public Builder<REQ> withTimeOutMillis(int timeoutMillis) {
			this.timeoutMillis = timeoutMillis;
			return this;
		}

		public Builder<REQ> withRequestObject(REQ reqObj) {
			this.requestObject = reqObj;
			return this;
		}

		public RestRequest<REQ> build() {
			return new RestRequest<>(url, method, contentType, userHeaders, timeoutMillis, requestObject);
		}

		public RestRequest<REQ> buildOptions(final GenericUrl url) {
			return buildMethod(HttpMethod.OPTIONS, url, null);
		}

		public <T> RestRequest<T> buildPut(final GenericUrl url, final T content) {
			return buildMethod(HttpMethod.PUT, url, content);
		}

		public RestRequest<REQ> buildGet(final GenericUrl url) {
			return buildMethod(HttpMethod.GET, url, null);
		}

		public <T> RestRequest<T> buildPost(final GenericUrl url, final T content) {
			return buildMethod(HttpMethod.POST, url, content);
		}

		public RestRequest<REQ> buildDelete(final GenericUrl url) {
			return buildMethod(HttpMethod.DELETE, url, null);
		}

		public <T> RestRequest<T> buildMethod(final HttpMethod method, final GenericUrl url, final T content) {
			return new RestRequest<T>(url, method, contentType, userHeaders, timeoutMillis, content);
		}

	}

	private HttpMethod getMethod() {
		return this.method;
	}

	public REQ getRequestObject() {
		return requestObject;
	}

	public GenericUrl getUrl() {
		return url;
	}

	public int getTimeoutMillis() {
		return timeoutMillis;
	}

	public MediaType getContentType() {
		return contentType;
	}

	public Map<String, List<String>> getUserHeaders() {
		return userHeaders;
	}

	@Override
	public void serialize(JsonGenerator gen, SerializerProvider serializers) throws IOException {
		gen.writeStartObject();
		gen.writeFieldName("url");
		gen.writeObject(this.url);
		gen.writeFieldName("method");
		gen.writeString(this.getMethod().toString());
		gen.writeFieldName("payload");
		gen.writeObject(this.requestObject);
		gen.writeEndObject();

	}

	@Override
	public void serializeWithType(JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer)
			throws IOException {
		throw new UnsupportedOperationException();
	}
}
