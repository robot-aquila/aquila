package ru.prolib.aquila.web.utils.httpclient;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.web.utils.FileDownloader;
import ru.prolib.aquila.web.utils.WUIOException;
import ru.prolib.aquila.web.utils.WUInvalidResponseException;
import ru.prolib.aquila.web.utils.WUProtocolException;

@Deprecated
public class HttpClientFileDownloader implements FileDownloader {
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(HttpClientFileDownloader.class);
	}
	
	private final CloseableHttpClient httpClient;
	private final ResponseValidator responseValidator;

	/**
	 * Create downloader.
	 * <p>
	 * @param httpClient - http client instance
	 * @param responseValidator - response validator
	 */
	public HttpClientFileDownloader(CloseableHttpClient httpClient,
			ResponseValidator responseValidator)
	{
		this.httpClient = httpClient;
		this.responseValidator = responseValidator;
	}
	
	/**
	 * Create downloader with stub response validator.
	 * <p>
	 * @param httpClient - http client instance
	 */
	public HttpClientFileDownloader(CloseableHttpClient httpClient) {
		this(httpClient, ResponseValidatorStub.getInstance());
	}
	
	/* (non-Javadoc)
	 * @see ru.prolib.aquila.finam.tools.web.FileDownloader#download(java.net.URI, java.io.OutputStream)
	 */
	@Override
	public void download(URI uri, OutputStream output)
			throws WUInvalidResponseException, WUProtocolException, WUIOException
	{
		logger.debug("Downloading: {}", uri);
		CloseableHttpResponse response;
		try {
			response = httpClient.execute(new HttpGet(uri));
		} catch ( ClientProtocolException e ) {
			throw new WUProtocolException("Error executing HTTP-request", e);
		} catch ( IOException e ) {
			throw new WUIOException("Error execution HTTP-request", e);
		}
		try {
			responseValidator.validateResponse(response);
			HttpEntity entity = response.getEntity();
			InputStream input = new BufferedInputStream(entity.getContent());
			try {
				IOUtils.copy(input, output);
			} catch ( IOException e ) {
				throw new WUIOException("Error downloading", e);
			} finally {
				IOUtils.closeQuietly(input);
			}
		} catch ( IOException e ) {
			throw new WUIOException("Error obtaining the response content", e);
		} finally {
			IOUtils.closeQuietly(response);
		}
	}

}
