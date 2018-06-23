package com.rocket.core.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import javax.ws.rs.core.Form;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.google.api.client.http.AbstractHttpContent;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpMediaType;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.UrlEncodedContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.common.io.ByteStreams;
import com.rocket.core.Habitat;
import com.rocket.core.utils.JsonUtils;
import com.rocket.core.utils.RocketUtils;

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

	private RestClient() {
	}

	public static HttpResponse get(final String url) throws URISyntaxException, IOException {
		return get(url, DEFAULT_MEDIATYPE);
	}

	public static HttpResponse get(final String url, final MediaType contentType)
			throws URISyntaxException, IOException {
		return (HttpResponse) get(
				RestRequest.newBuilder().withContentType(contentType).buildGet(new GenericUrl(new URI(url))),
				HttpResponse.class).getContent();
	}

	public static <RESP> ResponseDetail<RESP> get(final String url, final Class<RESP> returnType)
			throws URISyntaxException, IOException {
		return get(url, DEFAULT_MEDIATYPE, DEFAULT_TIMEOUT_MILLIS, returnType);
	}

	public static <RESP> ResponseDetail<RESP> get(final String url, final MediaType contentType,
			final Class<RESP> returnType) throws URISyntaxException, IOException {
		return get(url, contentType, DEFAULT_TIMEOUT_MILLIS, returnType);
	}

	public static <RESP> ResponseDetail<RESP> get(final String url, final MediaType contentType,
			final int timeOutMillis, final Class<RESP> returnType) throws URISyntaxException, IOException {
		return get(RestRequest.newBuilder().withContentType(contentType).withTimeOutMillis(timeOutMillis)
				.buildGet(new GenericUrl(new URI(url))), returnType);
	}

	public static <REQ, RESP> ResponseDetail<RESP> get(RestRequest<REQ> req, Class<RESP> returnType)
			throws IOException {
		return doHttpRequest(req, new ResponseDetail<RESP>(returnType));
	}

	public static <REQ> HttpResponse post(final String url, final REQ content) throws URISyntaxException, IOException {
		return post(url, DEFAULT_MEDIATYPE, content);
	}

	public static <REQ> HttpResponse post(final String url, final MediaType contentType, final REQ content)
			throws URISyntaxException, IOException {
		return (HttpResponse) post(
				RestRequest.newBuilder().withContentType(contentType).buildPost(new GenericUrl(new URI(url)), content),
				HttpResponse.class).getContent();
	}

	public static <RESP, REQ> ResponseDetail<RESP> post(final String url, final REQ content,
			final Class<RESP> returnType) throws URISyntaxException, IOException {
		return post(url, content, DEFAULT_MEDIATYPE, DEFAULT_TIMEOUT_MILLIS, returnType);
	}

	public static <RESP, REQ> ResponseDetail<RESP> post(final String url, final REQ content,
			final MediaType contentType, final Class<RESP> returnType) throws URISyntaxException, IOException {
		return post(url, content, contentType, DEFAULT_TIMEOUT_MILLIS, returnType);
	}

	public static <RESP, REQ> ResponseDetail<RESP> post(final String url, final REQ content,
			final MediaType contentType, final int timeOutMillis, final Class<RESP> returnType)
			throws URISyntaxException, IOException {
		return post(RestRequest.newBuilder().withContentType(contentType).withTimeOutMillis(timeOutMillis)
				.buildPost(new GenericUrl(new URI(url)), content), returnType);
	}

	public static <REQ, RESP> ResponseDetail<RESP> post(RestRequest<REQ> req, Class<RESP> returnType)
			throws IOException {
		return doHttpRequest(req, new ResponseDetail<RESP>(returnType));
	}

	public static <REQ> HttpResponse put(final String url, final REQ content) throws URISyntaxException, IOException {
		return put(url, DEFAULT_MEDIATYPE, content);
	}

	public static <REQ> HttpResponse put(final String url, final MediaType contentType, final REQ content)
			throws URISyntaxException, IOException {
		return (HttpResponse) put(
				RestRequest.newBuilder().withContentType(contentType).buildPut(new GenericUrl(new URI(url)), content),
				HttpResponse.class).getContent();
	}

	public static <RESP, REQ> ResponseDetail<RESP> put(final String url, final REQ content,
			final Class<RESP> returnType) throws URISyntaxException, IOException {
		return put(url, content, DEFAULT_MEDIATYPE, DEFAULT_TIMEOUT_MILLIS, returnType);
	}

	public static <RESP, REQ> ResponseDetail<RESP> put(final String url, final REQ content, final MediaType contentType,
			final Class<RESP> returnType) throws URISyntaxException, IOException {
		return put(url, content, contentType, DEFAULT_TIMEOUT_MILLIS, returnType);
	}

	public static <RESP, REQ> ResponseDetail<RESP> put(final String url, final REQ content, final MediaType contentType,
			final int timeOutMillis, final Class<RESP> returnType) throws URISyntaxException, IOException {
		return put(RestRequest.newBuilder().withContentType(contentType).withTimeOutMillis(timeOutMillis)
				.buildPut(new GenericUrl(new URI(url)), content), returnType);
	}

	public static <REQ, RESP> ResponseDetail<RESP> put(RestRequest<REQ> req, Class<RESP> returnType)
			throws IOException {
		return doHttpRequest(req, new ResponseDetail<RESP>(returnType));
	}

	public static HttpResponse delete(final String url) throws URISyntaxException, IOException {
		return delete(url, DEFAULT_MEDIATYPE);
	}

	public static HttpResponse delete(final String url, final MediaType contentType)
			throws URISyntaxException, IOException {
		return (HttpResponse) delete(
				RestRequest.newBuilder().withContentType(contentType).buildDelete(new GenericUrl(new URI(url))),
				HttpResponse.class).getContent();
	}

	public static <RESP> ResponseDetail<RESP> delete(final String url, final Class<RESP> returnType)
			throws URISyntaxException, IOException {
		return delete(url, DEFAULT_MEDIATYPE, DEFAULT_TIMEOUT_MILLIS, returnType);
	}

	public static <RESP> ResponseDetail<RESP> delete(final String url, final MediaType contentType,
			final Class<RESP> returnType) throws URISyntaxException, IOException {
		return delete(url, contentType, DEFAULT_TIMEOUT_MILLIS, returnType);
	}

	public static <RESP> ResponseDetail<RESP> delete(final String url, final MediaType contentType,
			final int timeOutMillis, final Class<RESP> returnType) throws URISyntaxException, IOException {
		return delete(RestRequest.newBuilder().withContentType(contentType).withTimeOutMillis(timeOutMillis)
				.buildDelete(new GenericUrl(new URI(url))), returnType);
	}

	public static <REQ, RESP> ResponseDetail<RESP> delete(RestRequest<REQ> req, Class<RESP> returnType)
			throws IOException {
		return doHttpRequest(req, new ResponseDetail<RESP>(returnType));
	}
	
	public static HttpResponse options(final String url) throws URISyntaxException, IOException {
		return options(url, DEFAULT_MEDIATYPE);
	}

	public static HttpResponse options(final String url, final MediaType contentType)
			throws URISyntaxException, IOException {
		return (HttpResponse) options(
				RestRequest.newBuilder().withContentType(contentType).buildOptions(new GenericUrl(new URI(url))),
				HttpResponse.class).getContent();
	}

	public static <RESP> ResponseDetail<RESP> options(final String url, final Class<RESP> returnType)
			throws URISyntaxException, IOException {
		return options(url, DEFAULT_MEDIATYPE, DEFAULT_TIMEOUT_MILLIS, returnType);
	}

	public static <RESP> ResponseDetail<RESP> options(final String url, final MediaType contentType,
			final Class<RESP> returnType) throws URISyntaxException, IOException {
		return options(url, contentType, DEFAULT_TIMEOUT_MILLIS, returnType);
	}

	public static <RESP> ResponseDetail<RESP> options(final String url, final MediaType contentType,
			final int timeOutMillis, final Class<RESP> returnType) throws URISyntaxException, IOException {
		return options(RestRequest.newBuilder().withContentType(contentType).withTimeOutMillis(timeOutMillis)
				.buildOptions(new GenericUrl(new URI(url))), returnType);
	}

	public static <REQ, RESP> ResponseDetail<RESP> options(RestRequest<REQ> req, Class<RESP> returnType)
			throws IOException {
		return doHttpRequest(req, new ResponseDetail<RESP>(returnType));
	}	

	public static <RESP, REQ> ResponseDetail<RESP> doHttpRequest(RestRequest<REQ> request,
			ResponseDetail<RESP> returnType) throws IOException {

		REQ object = request.getRequestObject();
		Class<?> contentClass = null;
		if (object != null)
			contentClass = object.getClass();
		HttpContent content = null;
		HttpHeaders reqheaders = null;
		HttpRequest http = null;
		switch (request.getMethod()) {
		case GET:
			http = HTTP_REQUEST_FACTORY.buildGetRequest(request.getUrl());
			break;
		case POST:
		case PUT:
			if (Form.class.equals(contentClass)) {
				content = new UrlEncodedContent(((Form) object).asMap());
			} else if (CharSequence.class.equals(contentClass)) {
				content = new AbstractHttpContent(request.getContentType().getMimeType()) {

					@Override
					public void writeTo(OutputStream out) throws IOException {
						BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
						writer.write(object.toString());
						writer.flush();
					}
				};
			} else if (byte[].class.equals(contentClass)) {
				content = new ByteArrayContent(request.getContentType().getMimeType(), (byte[]) object);
			} else if (InputStream.class.equals(contentClass)) {
				content = new InputStreamContent(request.getContentType().getMimeType(), (InputStream) object);
			} else {
				content = new JsonHttpContent(new JacksonFactory(), object);
			}
			http = HTTP_REQUEST_FACTORY.buildRequest(request.getMethod().toString(), request.getUrl(), content);
			break;
		case OPTIONS:
			http = HTTP_REQUEST_FACTORY.buildRequest("OPTIONS", request.getUrl(), null);
			break;
		case DELETE:
			http = HTTP_REQUEST_FACTORY.buildDeleteRequest(request.getUrl());
			break;
		default:
			throw new RuntimeException("Unsupported or unknown http method");
		}
		reqheaders = http.getHeaders();
		for (Entry<String, List<String>> header : request.getUserHeaders().entrySet()) {
			String key = header.getKey();
			if (reqheaders.containsKey(key))
				LOG.info("Replacing default header {} default value {} with user value{}", key, reqheaders.get(key),
						header.getValue());
			reqheaders.put(key, header.getValue());
		}
		// return StaticHolder.getDefaultExecutor().submit(new
		// RequestCallable<REQ, RESP>(http, returnType)).get();
		try {
			return (new RequestCallable<REQ, RESP>(http, returnType)).call();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static class RequestCallable<REQ, RESP> implements Callable<ResponseDetail<RESP>> {

		private final HttpRequest httpRequest;
		private final ResponseDetail<RESP> httpResponse;

		public RequestCallable(HttpRequest req, ResponseDetail<RESP> responseType) {
			this.httpRequest = req;
			this.httpResponse = responseType;
		}

		@SuppressWarnings("unchecked")
		@Override
		public ResponseDetail<RESP> call() throws Exception {
			HttpResponse response = httpRequest.execute();
			handleErrorResponse(response);
			httpResponse.setHeaders(response.getHeaders());
			httpResponse.setStatusCode(response.getStatusCode());
			if (response.getClass().equals(httpResponse.getReturnType())) {
				httpResponse.setContent((RESP) response);

			} else {
				doResponseConversion(response);
			}
			return httpResponse;
		}

		public static void handleErrorResponse(HttpResponse response) throws IOException {
			if (!response.isSuccessStatusCode()) {
				InputStream stream = response.getContent();
				if (stream != null) {
					LOG.error("Error from server {}", response.parseAsString());
				}
				throw new HttpResponseException(response);
			}
		}

		public void doResponseConversion(HttpResponse response)
				throws IOException, JAXBException, SAXException, ParserConfigurationException {
			InputStream content = response.getContent();
			if (content == null)
				return;
			httpResponse.setContent(
					parseObject(response.getMediaType(), response.getContent(), httpResponse.getReturnType()));
		}

		@SuppressWarnings("unchecked")
		public RESP parseObject(HttpMediaType type, InputStream in, Class<RESP> res)
				throws JAXBException, SAXException, IOException, ParserConfigurationException {
			MediaType responseType = MediaType.parse(type.getType(), type.getSubType());
			switch (responseType) {

			case APPLICATION_OCTETSTREAM:
				if (byte[].class.equals(res)) {
					return (RESP) ByteStreams.toByteArray(in);
				}
			case APPLICATION_JSON:
				return JsonUtils.Deserialize(in, res);
			case APPLICATION_XML:
				return RocketUtils.readPojoFromXml(in, res);
			case TEXT_CSV:
			case TEXT_HTML:
			case TEXT_PLAIN:
			case TEXT_XML:
			case IMAGE_JPEG:
			case IMAGE_PNG:
			case IMAGE_ANY:
			case APPLICATION_PDF:
			case APPLICATION_XLS:
			case APPLICATION_XLSX:
			case APPLICATION_DOC:
			case APPLICATION_DOCX:
			case WILDCARD:
				return parseStringOrByte(in, res, type);
			default:
				throw new RuntimeException("Unsupported content type " + type.getType() + "/" + type.getSubType());
			}
		}

		@SuppressWarnings("unchecked")
		private RESP parseStringOrByte(InputStream in, Class<RESP> res, HttpMediaType type) throws IOException {
			if (byte[].class.equals(res)) {
				return (RESP) ByteStreams.toByteArray(in);
			} else if (CharSequence.class.equals(res)) {
				return (RESP) new String(ByteStreams.toByteArray(in), StandardCharsets.UTF_8);
			} else {
				throw new RuntimeException("Unsupported mediaType : " + type.getType() + "/" + type.getSubType());
			}
		}

	}

}
