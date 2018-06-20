package com.rocket.core.http;

public enum MediaType {

	TEXT_XML("text/xml"), TEXT_PLAIN("text/plain"), TEXT_HTML("text/html"), TEXT_CSV("text/csv"), FORM_URLENCODING(
			"application/x-www-form-urlencoded"), APPLICATION_XML("application/xml"), APPLICATION_JSON(
					"application/json"), APPLICATION_OCTETSTREAM("application/octet-stream"), APPLICATION_PDF(
							"application/pdf"), APPLICATION_XLS("application/vnd.ms-excel"), APPLICATION_XLSX(
									"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), APPLICATION_DOC(
											"application/msword"), APPLICATION_DOCX(
													"application/vnd.openxmlformats-officedocument.wordprocessingml.document"), IMAGE_JPEG(
															"image/jpeg"), IMAGE_PNG(
																	"image/png"), IMAGE_ANY("image/*"), WILDCARD("*/*");

	private final String mimeType;

	private MediaType(final String mimeType) {
		this.mimeType = mimeType;
	}

	public String getMimeType() {
		return mimeType;
	}

	@Override
	public String toString() {
		return mimeType;
	}
}
