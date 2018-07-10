package com.rocket.core.http;

import java.io.IOException;
import java.io.OutputStream;

import com.google.api.client.http.AbstractHttpContent;
import com.rocket.core.utils.JsonUtils;

public class JsonContent extends AbstractHttpContent {

	private Object record;

	public JsonContent(final Object record, MediaType contentType) {
		super(contentType.getMimeType());
		this.record = record;
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		JsonUtils.writeJsonToStream(out, record);
	}

}
