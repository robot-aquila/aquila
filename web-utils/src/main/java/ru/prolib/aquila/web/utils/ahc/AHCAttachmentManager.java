package ru.prolib.aquila.web.utils.ahc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;

import com.google.common.net.HttpHeaders;

import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachment;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentCriteria;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentException;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentImpl;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentManager;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentNotFoundException;
import ru.prolib.aquila.web.utils.httpattachment.HTTPDownloadInitiator;

public class AHCAttachmentManager implements HTTPAttachmentManager {
	private final AHCClientFactory factory;
	
	public AHCAttachmentManager(AHCClientFactory factory) {
		this.factory = factory;
	}
	
	public AHCClientFactory getClientFactory() {
		return factory;
	}

	@Override
	public HTTPAttachment getLast(HTTPAttachmentCriteria criteria, HTTPDownloadInitiator initiator)
			throws HTTPAttachmentException, IOException
	{
		String url = criteria.getURL();
		if ( url == null ) {
			throw new HTTPAttachmentException("URL is not specified");
		}
		try ( CloseableHttpClient httpClient = factory.createHttpClient() ) {
			try ( CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
				int statusCode = response.getStatusLine().getStatusCode();
				if ( statusCode != 200 ) {
					throw new HTTPAttachmentNotFoundException(criteria, "Bad response code: " + statusCode);
				}
				Header h = null;
				String x, expected;
				expected = criteria.getContentType();
				if ( expected != null ) {
					h = response.getFirstHeader(HttpHeaders.CONTENT_TYPE);
					if ( h == null ) {
						throw new HTTPAttachmentNotFoundException(criteria, "Content type expected but not found in response");
					}
					x = h.getValue();
					if ( ! expected.equals(x) ) {
						throw new HTTPAttachmentNotFoundException(criteria, "Content type mismatch. Actual: " + x);
					}
				}
				expected = criteria.getContentDisposition();
				if ( expected != null ) {
					h = response.getFirstHeader(HttpHeaders.CONTENT_DISPOSITION);
					if ( h == null ) {
						throw new HTTPAttachmentNotFoundException(criteria, "Content disposition expected but not found in response");
					}
					x = h.getValue();
					if ( ! expected.equals(x) ) {
						throw new HTTPAttachmentNotFoundException(criteria, "Content disposition mismatch. Actual: " + x);
					}
				}
				InputStream input = new BufferedInputStream(response.getEntity().getContent());
				File file = File.createTempFile("ahc-attachment-", null);
				file.deleteOnExit();
				FileUtils.copyInputStreamToFile(input, file);
				return new HTTPAttachmentImpl(file);
			}
		} catch ( IOException e ) {
			throw new HTTPAttachmentException(e);
		}
	}

}
