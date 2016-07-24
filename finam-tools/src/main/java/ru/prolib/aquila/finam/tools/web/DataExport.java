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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
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
		} catch ( DataExportException e ) {
			throw e;
		} catch ( Exception e ) {
			throw unhandled(e);
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
		} catch ( DataExportException e ) {
			throw e;
		} catch ( Exception e ) {
			throw unhandled(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * Get map of available market options.
	 * <p>
	 * Note: The call of this method may use several minutes to execute and will
	 * lock the facade until done.
	 * <p>
	 * @return map where key is a market id and value is an option text.
	 * @throws DataExportException - an error occurred
	 */
	public Map<Integer, String> getAvailableMarkets() throws DataExportException {
		List<NameValuePair> pairs;
		lock.lock();
		try {
			pairs = webForm.getMarketOptions();
		} catch ( DataExportException e ) {
			throw e;
		} catch ( Exception e ) {
			throw unhandled(e);
		} finally {
			lock.unlock();
		}
		return toMap(pairs);
	}
	
	/**
	 * Get map of available quote options of market.
	 * <p>
	 * Note: The call of this method may use several minutes to execute and will
	 * lock the facade until done.
	 * <p>
	 * @param marketId - the market id
	 * @return map where key is a quote id and value is an option text
	 * @throws DataExportException - an error occurred
	 */
	public Map<Integer, String> getAvailableQuotes(int marketId) throws DataExportException {
		List<NameValuePair> pairs;
		lock.lock();
		try {
			pairs = webForm.selectMarket(marketId).getQuoteOptions();
		} catch ( DataExportException e ) {
			throw e;
		} catch ( Exception e ) {
			throw unhandled(e);
		} finally {
			lock.unlock();
		}
		return toMap(pairs);
	}
	
	/**
	 * Get map of available futures.
	 * <p>
	 * The futures data section contains the real futures and some synthetic
	 * quotes which does not mapped to a real instrument. This method filters
	 * all quotes of futures market data section and returns only data of
	 * actually existing futures. For example RTS is a combined synthetic
	 * instrument and will be skipped. The RTS-12.16(RIZ6) is a real futures and
	 * will be returned.
	 * <p>
	 * Note: The call of this method may use several minutes to execute and will
	 * lock the facade until done.
	 * <p>
	 * @param stripCodes - if true then will strip all codes. For example
	 * RTS-12.16(RIZ6) will be converted to RTS-12.16.
	 * @return map where key is a quote id and value is an option text
	 * @throws DataExportException - an error occurred
	 */
	public Map<Integer, String> getTrueFuturesQuotes(boolean stripCodes)
			throws DataExportException
	{
		List<NameValuePair> pairs, result = new ArrayList<>();
		lock.lock();
		try {
			pairs = webForm.selectMarket(14).getQuoteOptions();
		} catch ( DataExportException e ) {
			throw e;
		} catch ( Exception e ) {
			throw unhandled(e);
		} finally {
			lock.unlock();
		}
		for ( NameValuePair pair : pairs ) {
			NameValuePair dummy = splitFuturesCode(pair.getName());
			if ( dummy != null ) {
				if ( stripCodes ) {
					pair = new BasicNameValuePair(dummy.getName(), pair.getValue());
				}
				result.add(pair);
			}
		}
		return toMap(result);
	}
	
	/**
	 * Split a futures code.
	 * <p>
	 * For example RTS-12.16(RIZ6) will be converted to a pair where name is
	 * RTS-12.16 and value is RIZ6
	 * <p>
	 * @param text - the full futures name as it used on FIMAN site
	 * @return the name components or null if the text is not a futures name
	 */
	public NameValuePair splitFuturesCode(String text) {
		if ( ! text.endsWith(")") ) {
			// Not a futures, combined data or the ticker without a code (but we need it).
			// Skip such option.
			return null;
		}
		String tokens[] = StringUtils.split(text, '(');
		if ( tokens.length != 2 ) {
			// The opening brace was not found. Cannot determine the ticker code.
			// Skip such option.
			return null;
		}
		return new BasicNameValuePair(tokens[0], tokens[1].substring(0, tokens[1].length() - 1));
	}
	
	/**
	 * Download a market data file from FINAM web-site.
	 * <p>
	 * @param uri - fully-formed URI to download from. The URI uses "as is". No additional checks performed.
	 * @param output - the output stream to store the downloaded data
	 * @throws DataExportException - common exception for all error situations
	 */
	public void download(URI uri, OutputStream output) throws DataExportException {
		try {
			new HttpClientFileDownloader(httpClient).download(uri, output);
		} catch ( DataExportException e ) {
			throw e;
		} catch ( Exception e ) {
			throw unhandled(e);
		}
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

	/**
	 * Download a market data file from FINAM web-site.
	 * <p>
	 * @param baseUri - base URI to resolve address of downloading file. It used
	 * to combine with the query string which was built from the export parameters.
	 * @param params - the data export parameters
	 * @param target - the target file. If the filename ends with .gz suffix
	 * then output will be gzipped.
	 * @throws DataExportException - common exception for all error situations
	 */
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
	
	/**
	 * Download a market data file from FINAM web site.
	 * <p>
	 * The method uses a FINAM web-form action URI to make a request.
	 * Note: The call of this method may use several minutes to execute and will
	 * lock the facade until done.
	 * <p>
	 * @param params - the data export parameters
	 * @param target - the target file. If the filename ends with .gz suffix
	 * then output will be gzipped.
	 * @throws DataExportException - common exception for all error situations
	 */
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
	
	private DataExportException unhandled(Throwable t) {
		return new DataExportException(ErrorClass.WEB_DRIVER, "Unhandled exception", t);
	}
	
	private Map<Integer, String> toMap(List<NameValuePair> pairs) throws DataExportException {	
		Map<Integer, String> result = new LinkedHashMap<>();
		for ( NameValuePair dummy : pairs ) {
			String text = dummy.getName(), id = dummy.getValue();
			try {
				result.put(Integer.valueOf(id), text);
			} catch ( NumberFormatException e ) {
				throw new DataExportException(ErrorClass.WEB_FORM,
						"Invalid option [" + text + "] id: " + id);
			}
		}
		return result;
	}
	
}
