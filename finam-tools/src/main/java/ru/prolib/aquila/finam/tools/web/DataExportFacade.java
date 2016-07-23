package ru.prolib.aquila.finam.tools.web;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.io.IOUtils;

public class DataExportFacade implements Closeable {
	private final CloseableHttpClient httpClient;
	
	public DataExportFacade() {
		this.httpClient = HttpClients.createDefault();
	}
	
	public Map<Integer, String> getAvailableMarkets() {
		return null;
	}
	
	public Map<Integer, String> getAvailableQuotes() {
		return null;
	}
	
	/**
	 * Download a market data file from FINAM web-site.
	 * <p>
	 * @param uri - fully-formed URI to download from. The URI uses "as is". No additional checks performed.
	 * @param output - the output stream to store the downloaded data
	 * @throws DataExportException - common exception for all error situations
	 */
	public void download(URI uri, OutputStream output) throws DataExportException {
		HttpClientFileDownloader fileDownloader = new HttpClientFileDownloader(httpClient);
		fileDownloader.download(uri, output);
	}
	
	/**
	 * Download a market data file from FINAM web-site.
	 * <p>
	 * @param baseUri - base URI to resolve address of downloading file. It used
	 * to combine with the query string which was built from the export parameters.
	 * @param params - the data export parameters
	 * @param output - the output stream to store the downloaded data
	 * @throws DataExportException - common exception for all error situations
	 */
	public void download(URI baseUri, DataExportParams params, OutputStream output)
			throws DataExportException
	{
		download(combine(baseUri, params), output);
	}

	public void download(URI baseUri, DataExportParams params, File target)
			throws DataExportException
	{
		OutputStream output;
		try {
			output = new BufferedOutputStream(new FileOutputStream(target));
		} catch ( FileNotFoundException e ) {
			throw new DataExportException(ErrorClass.POSSIBLE_LOGIC,
					"Error creating output stream", e);
		}
		try {
			download(baseUri, params, output);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
	
	private URI combine(URI baseUri, DataExportParams params)
		throws DataExportException
	{
		try {
			return new DataExportFormQueryBuilder().buildQuery(baseUri, params);
		} catch ( URISyntaxException e ) {
			throw new DataExportException(ErrorClass.REQUEST_INITIALIZATION,
					"Error building a query", e);
		}
	}
	
	public void close() {
		IOUtils.closeQuietly(httpClient);
	}

}
