package ru.prolib.aquila.web.utils.httpattachment;

import java.time.Instant;

public class HTTPAttachmentCriteriaBuilder {
	private Instant startDownload;
	private String url, fileName, contentType, contentDisposition;
	
	public HTTPAttachmentCriteriaBuilder withTimeOfStartDownload(Instant time) {
		this.startDownload = time;
		return this;
	}
	
	public HTTPAttachmentCriteriaBuilder withTimeOfStartDownloadCurrent() {
		return withTimeOfStartDownload(Instant.now());
	}
	
	public HTTPAttachmentCriteriaBuilder withURL(String url) {
		this.url = url;
		return this;
	}
	
	public HTTPAttachmentCriteriaBuilder withFileName(String fileName) {
		this.fileName = fileName;
		return this;
	}
	
	public HTTPAttachmentCriteriaBuilder withContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}
	
	public HTTPAttachmentCriteriaBuilder withContentDisposition(String contentDisposition) {
		this.contentDisposition = contentDisposition;
		return this;
	}
	
	public HTTPAttachmentCriteria build() {
		return new HTTPAttachmentCriteria(startDownload == null ? Instant.now() : startDownload,
				url, fileName, contentType, contentDisposition);
	}

}
