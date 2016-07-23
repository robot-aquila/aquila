package ru.prolib.aquila.finam.tools.web;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
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

public class HttpClientFileDownloader implements FileDownloader {
	private final CloseableHttpClient httpClient;
	
	public HttpClientFileDownloader(CloseableHttpClient httpClient) {
		this.httpClient = httpClient;
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.finam.tools.web.FileDownloader#download(java.net.URI, java.io.File)
	 */
	@Override
	public void download(URI uri, File target) throws
		UnexpectedResponse, ClientProtocolException, IOException
	{
		CloseableHttpResponse response = httpClient.execute(new HttpGet(uri));
		try {
			validateResponse(response);
			HttpEntity entity = response.getEntity();
			OutputStream output = new BufferedOutputStream(new FileOutputStream(target));
			InputStream input = new BufferedInputStream(entity.getContent());
			try {
				IOUtils.copy(input, output);
			} finally {
				input.close();
				output.close();
			}
		} finally {
			response.close();
		}
	}

	private void validateResponse(CloseableHttpResponse response)
		throws UnexpectedResponse
	{
		int statusCode = response.getStatusLine().getStatusCode();
		if ( statusCode != 200 ) {
			throw new UnexpectedResponse("Unexpected HTTP status code: " + statusCode);
		}
		Header header = response.getFirstHeader("content-type");
		if ( header == null ) {
			throw new UnexpectedResponse("Header Content-Type not exists");
		}
		String contentType = header.getValue();
		if ( ! "finam/expotfile".equals(contentType) ) {
			throw new UnexpectedResponse("Unexpected Content-Type: " + contentType);
		}
		header = response.getFirstHeader("content-disposition");
		if ( header == null ) {
			throw new UnexpectedResponse("Header Content-Disposition not exists");
		}
		String contentDisposition = header.getValue();
		if ( ! contentDisposition.startsWith("attachment;") ) {
			throw new UnexpectedResponse("Unexpected Content-Disposition: " + contentDisposition);
		}
	}
	
}
