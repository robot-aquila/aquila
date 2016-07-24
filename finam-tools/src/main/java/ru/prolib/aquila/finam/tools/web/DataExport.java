package ru.prolib.aquila.finam.tools.web;

import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.io.IOUtils;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;

/**
 * The facade of FINAM data export system.
 */
public class DataExport implements Closeable {
	private final Lock lock = new ReentrantLock();
	private final CloseableHttpClient httpClient;
	private final WebDriver webDriver;
	private final DataExportForm webForm;
	private boolean closeResources = false;
	
	public DataExport(CloseableHttpClient httpClient, WebDriver webDriver) {
		this.httpClient = httpClient;
		this.webDriver = webDriver;
		this.webForm = new DataExportForm(webDriver);
		closeResources = false;
	}
	
	public DataExport() {
		this(HttpClients.createDefault(), new JBrowserDriver(Settings.builder()
				.timezone(Timezone.EUROPE_MOSCOW)
				.ssl("compatible")
				.blockAds(true)
				.headless(true)
				.quickRender(true)
				.build()));
		closeResources = true;
	}
	
	public void close() {
		lock.lock();
		try {
			if ( closeResources ) {
				IOUtils.closeQuietly(httpClient);
				webDriver.close();
			}
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Run a web-form integrity test.
	 * <p>
	 * This test will try to select form options which expected to be available.
	 * If this test fails then the form may be changed of its structure.
	 * In this case the form parsing algorithm should be revised.
	 * Note: this test may use up to several minutes to pass.
	 * The call of this method will lock the facade until the test finished.
	 * <p>
	 * @throws DataExportException - an error occurred
	 */
	public void testFormIntegrity() throws DataExportException {
		lock.lock();
		try {
			webForm.selectMarket(14)
				.selectQuote(17455)
				.selectDateFrom(LocalDate.now().minusDays(5))
				.selectDateTo(LocalDate.now())
				.selectPeriod(Period.H1)
				.selectFilename("zulu-charlie24")
				.selectFileExt(FileExt.CSV)
				.selectContractName("RTS")
				.selectDateFormat(DateFormat.DDslashMMslashYY)
				.selectTimeFormat(TimeFormat.HHcolonMMcolonSS)
				.useCandleStartTime(false)
				.useMoscowTime(true)
				.selectFieldSeparator(FieldSeparator.SPACE)
				.selectDigitSeparator(DigitSeparator.APOSTROPHE)
				.selectDataFormat(DataFormat.TICKER_PER_DATE_TIME_CLOSE_VOL)
				.useAddHeader(false)
				.useFillEmptyPeriods(true)
				.getFormActionURI();
		} finally {
			lock.unlock();
		}
	}
	
	/**
	 * Get form action URI.
	 * <p>
	 * Note: The call of this method may use several minutes to execute and will
	 * lock the facade until done.
	 * <p>
	 * @return form action URI currently specified in the web form
	 * @throws DataExportException - an error occurred
	 */
	public URI getFormActionURI() throws DataExportException {
		lock.lock();
		try {
			return webForm.getFormActionURI();
		} finally {
			lock.unlock();
		}
	}
	
	public Map<Integer, String> getAvailableMarkets() throws DataExportException {
		return null;
	}
	
	public Map<Integer, String> getAvailableQuotes(int marketId) throws DataExportException {
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
			if ( target.getName().endsWith(".gz") ) {
				output = new GZIPOutputStream(output);
			}
		} catch ( IOException e ) {
			throw new DataExportException(ErrorClass.IO, "Error creating output stream", e);
		}
		try {
			download(baseUri, params, output);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
	
	public void download(DataExportParams params, File target)
			throws DataExportException
	{
		download(getFormActionURI(), params, target);
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
	
}
