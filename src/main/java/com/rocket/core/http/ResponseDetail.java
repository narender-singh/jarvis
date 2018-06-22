package com.rocket.core.http;

import com.google.api.client.http.HttpHeaders;

public class ResponseDetail<RESP> {

	private int statusCode;
	private HttpHeaders headers;
	private RESP content;
	private final Class<RESP> returnType;

	public ResponseDetail(Class<RESP> type) {
		this.returnType = type;
	}

	public Class<RESP> getReturnType() {
		return this.returnType;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public HttpHeaders getHeaders() {
		return headers;
	}

	public void setHeaders(HttpHeaders headers) {
		this.headers = headers;
	}

	public RESP getContent() {
		return content;
	}

	public void setContent(RESP content) {
		this.content = content;
	}

}
