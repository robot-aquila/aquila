package ru.prolib.aquila.web.utils.httpattachment;

import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Search criteria of an attachment.
 */
public class HTTPAttachmentCriteria {
	private final Instant startDownload;
	private final String url, fileName, contentType, contentDisposition;
	
	public HTTPAttachmentCriteria(Instant startDownload, String url, String fileName, String contentType, String contentDisposition) {
		this.startDownload = startDownload;
		this.url = url;
		this.fileName = fileName;
		this.contentType = contentType;
		this.contentDisposition = contentDisposition;
	}
	
	/**
	 * Get expected URL of an attachment.
	 * <p>
	 * @return URL or null if unknown
	 */
	public String getURL() {
		return url;
	}
	
	/**
	 * Get expected file name of an attachment.
	 * <p>
	 * @return file name or null if unknown
	 */
	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Get expected content type header of an attachment.
	 * <p>
	 * @return content type or null if unknown
	 */
	public String getContentType() {
		return contentType;
	}
	
	/**
	 * Get expected content disposition header of an attachment.
	 * <p>
	 * @return content disposition or null if unknown
	 */
	public String getContentDisposition() {
		return contentDisposition;
	}
	
	/**
	 * Get time of start download.
	 * <p>
	 * @return local time after which download begins. This is mandatory
	 * parameter which is always known.
	 */
	public Instant getTimeOfStartDownload() {
		return startDownload;
	}
	
	@Override
	public boolean equals(Object other) {
		if ( other == this ) {
			return true;
		}
		if ( other == null || other.getClass() != HTTPAttachmentCriteria.class ) {
			return false;
		}
		HTTPAttachmentCriteria o = (HTTPAttachmentCriteria) other;
		return new EqualsBuilder()
			.append(o.startDownload, startDownload)
			.append(o.url, url)
			.append(o.fileName, fileName)
			.append(o.contentType, contentType)
			.append(o.contentDisposition, contentDisposition)
			.isEquals();
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder(9282611, 87361)
			.append(startDownload)
			.append(url)
			.append(fileName)
			.append(contentType)
			.append(contentDisposition)
			.toHashCode();
	}
	
	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.append("startDownload", startDownload)
			.append("url", url)
			.append("fileName", fileName)
			.append("contentType", contentType)
			.append("contentDisposition", contentDisposition)
			.toString();
	}

}
