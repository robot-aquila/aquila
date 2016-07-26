package ru.prolib.aquila.finam.tools.web;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientFileDownloader implements FileDownloader {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(HttpClientFileDownloader.class);
	}
	
	private final CloseableHttpClient httpClient;
	
	public HttpClientFileDownloader(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.finam.tools.web.FileDownloader#download(java.net.URI, java.io.OutputStream)
	 */
	@Override
	public void download(URI uri, OutputStream output) throws DataExportException {
		logger.debug("Downloading: {}", uri);
		CloseableHttpResponse response;
		try {
			response = httpClient.execute(new HttpGet(uri));
		} catch ( ClientProtocolException e ) {
			throw new DataExportException(ErrorClass.PROTOCOL, "Error executing HTTP-request", e);
		} catch ( IOException e ) {
			throw new DataExportException(ErrorClass.IO, "Error execution HTTP-request", e);
		}
		try {
			validateResponse(response);
			HttpEntity entity = response.getEntity();
			InputStream input = new BufferedInputStream(entity.getContent());
			try {
				IOUtils.copy(input, output);
			} catch ( IOException e ) {
				throw new DataExportException(ErrorClass.IO, "Error downloading", e);
			} finally {
				IOUtils.closeQuietly(input);
			}
		} catch ( IOException e ) {
			throw new DataExportException(ErrorClass.IO, "Error obtaining the response content", e);
		} finally {
			IOUtils.closeQuietly(response);
		}
	}

	private void validateResponse(CloseableHttpResponse response)
		throws DataExportException
	{
		int statusCode = response.getStatusLine().getStatusCode();
		if ( statusCode != 200 ) {
			throw errRespValid("Unexpected HTTP status code: " + statusCode);
		}
		Header header = response.getFirstHeader("content-type");
		if ( header == null ) {
			throw errRespValid("Header Content-Type not exists");
		}
		String contentType = header.getValue();
		if ( ! "finam/expotfile".equals(contentType) ) {
			throw errRespValid("Unexpected Content-Type: " + contentType);
		}
		header = response.getFirstHeader("content-disposition");
		if ( header == null ) {
			throw errRespValid("Header Content-Disposition not exists");
		}
		String contentDisposition = header.getValue();
		if ( ! contentDisposition.startsWith("attachment;") ) {
			throw errRespValid("Unexpected Content-Disposition: " + contentDisposition);
		}
	}
	
	private DataExportException errRespValid(String msg) throws DataExportException {
		return new DataExportException(ErrorClass.RESPONSE_VALIDATION, msg);
	}

}
