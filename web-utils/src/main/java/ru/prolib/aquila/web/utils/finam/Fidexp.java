package ru.prolib.aquila.web.utils.finam;

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
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.prolib.aquila.web.utils.WUException;
import ru.prolib.aquila.web.utils.WUIOException;
import ru.prolib.aquila.web.utils.WUUnexpectedException;
import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachment;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentCriteriaBuilder;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentException;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentManager;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentNotFoundException;
import ru.prolib.aquila.web.utils.httpattachment.di.WebDriverGet;

/**
 * The facade of FINAM data export system.
 * <p>
 * Note this class is not thread-safe.
 */
public class Fidexp implements Closeable {
	/**
	 * How many attempts to perform the operation will be done before
	 * the situation is found to be impracticable due to errors. 
	 */
	public static final int MAX_ATTEMPTS = 3;
	private static final Logger logger;
	
	static {
		logger = LoggerFactory.getLogger(Fidexp.class);
	}
	
	private final WebDriver webDriver;
	private final HTTPAttachmentManager attachmentManager;
	private final FidexpForm webForm;
	private final boolean closeResources;
	
	public Fidexp(WebDriver webDriver, HTTPAttachmentManager attachmentManager, boolean closeResources) {
		this.webDriver = webDriver;
		this.attachmentManager = attachmentManager;
		this.webForm = new FidexpForm(webDriver);
		this.closeResources = closeResources;
	}
	
	public Fidexp(WebDriver webDriver, HTTPAttachmentManager attachmentManager) {
		this(webDriver, attachmentManager, true);
	}
	
	/**
	 * Get web form.
	 * <p>
	 * Service method. For testing purposes only.
	 * <p>
	 * @return instance of web form
	 */
	FidexpForm getWebForm() {
		return webForm;
	}
	
	/**
	 * Get WebDriver.
	 * <p>
	 * Service method. For testing purposes only.
	 * <p>
	 * @return instance of web driver
	 */
	WebDriver getWebDriver() {
		return webDriver;
	}
	
	/**
	 * Get HTTP attachment manager.
	 * <p>
	 * Service method. For testing purposes only.
	 * <p>
	 * @return instance of HTTP attachment manager
	 */
	HTTPAttachmentManager getAttachmentManager() {
		return attachmentManager;
	}
	
	@Override
	public void close() {
		if ( closeResources ) {
			webDriver.quit();
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
	 * @throws WUException - an error occurred
	 */
	public void testFormIntegrity() throws WUException {
		try {
			checkFormIsReady(true);
			webForm
				.selectMarket(14)
				.selectQuote(17455)
				.selectDateFrom(LocalDate.now().minusDays(5))
				.selectDateTo(LocalDate.now())
				.selectPeriod(FidexpPeriod.H1)
				.selectFilename("zulu-charlie24")
				.selectFileExt(FidexpFileExt.CSV)
				.selectContractName("RTS")
				.selectDateFormat(FidexpDateFormat.DDslashMMslashYY)
				.selectTimeFormat(FidexpTimeFormat.HHcolonMMcolonSS)
				.useCandleStartTime(false)
				.useMoscowTime(true)
				.selectFieldSeparator(FidexpFieldSeparator.SPACE)
				.selectDigitSeparator(FidexpDigitSeparator.APOSTROPHE)
				.selectDataFormat(FidexpDataFormat.TICKER_PER_DATE_TIME_CLOSE_VOL)
				.useAddHeader(false)
				.useFillEmptyPeriods(true);
			// TODO: Add test for download by two methods: using direct and WebDriver execution
			// Do not try to load tick data in such test because it silently fails with JBD
			// Need enhancements to Attachment Manager interface 
		} catch ( WUException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new WUUnexpectedException(e);
		}
	}
	
	/**
	 * Get an URL of data download using interaction with web form.
	 * <p>
	 * This method acts as customer which interacts with form via browser.
	 * This means that the result must equals to result obtained by user
	 * when he requested service via browser. In other words - it must work
	 * if the  service works at all. As a drawback - it works very slow
	 * because of browser simulation. As side effect -  the result may be
	 * obtained only by pressing the form button and getting file
	 * downloaded. This method  should be used to make integration tests
	 * from time to time to be sure that the service still have the same
	 * requirements to clients as before. The
	 * {@link #paramsToURIUsingQueryBuilder(FidexpFormParams)} method with
	 * direct downloading should be used as a regular approach to data
	 * gathering. 
	 * <p> 
	 * @param params - set of parameters which describe what to download
	 * @return URI of doenloaded file
	 * @throws WUException - an error occurred
	 */
	public URI paramsToURIUsingFormAction(FidexpFormParams params) throws WUException {
		try {
			checkFormIsReady(true);
			webForm
				.selectMarket(params.getMarketID())
				.selectQuote(params.getQuoteID())
				.selectDateFrom(params.getDateFrom())
				.selectDateTo(params.getDateTo())
				.selectPeriod(params.getPeriod())
				.selectFileExt(params.getFileExt())
				.selectContractName(params.getContractName())
				.selectDateFormat(params.getDateFormat())
				.selectTimeFormat(params.getTimeFormat())
				.useCandleStartTime(params.getCandleTime() == FidexpCandleTime.START_OF_CANDLE)
				.useMoscowTime(params.getUseMoscowTime())
				.selectFieldSeparator(params.getFieldSeparator())
				.selectDigitSeparator(params.getDigitSeparator())
				.selectDataFormat(params.getDataFormat())
				.useAddHeader(params.getAddHeader())
				.useFillEmptyPeriods(params.getFillEmptyPeriods())
				.selectFilename(params.getFileName()) // use it in the last turn
				.send();
			return new URI(webDriver.getCurrentUrl());
		} catch ( WUException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new WUUnexpectedException(e);
		}
	}
	
	public URI paramsToURIUsingQueryBuilder(FidexpFormParams params) throws WUException {
		try {
			return new FidexpFormQueryBuilder().buildQuery(new URI("http://export.finam.ru"), params);
		} catch ( URISyntaxException e ) {
			throw new WUUnexpectedException(e);
		}
	}
	
	/**
	 * Get map of available market options.
	 * <p>
	 * Note: The call of this method may use several minutes to execute and will
	 * lock the facade until done.
	 * <p>
	 * @return map where key is a market id and value is an option text.
	 * @throws WUException - an error occurred
	 */
	public Map<Integer, String> getAvailableMarkets() throws WUException {
		List<NameValuePair> pairs;
		try {
			checkFormIsReady(true);
			pairs = webForm.getMarketOptions();
		} catch ( WUException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new WUUnexpectedException(e);
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
	 * @throws WUException - an error occurred
	 */
	public Map<Integer, String> getAvailableQuotes(int marketId) throws WUException {
		List<NameValuePair> pairs;
		try {
			checkFormIsReady(true);
			pairs = webForm.selectMarket(marketId).getQuoteOptions();
		} catch ( WUException e ) {
			throw e;
		} catch ( Exception e ) {
			throw new WUUnexpectedException(e);
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
	 * @throws WUException - an error occurred
	 */
	public Map<Integer, String> getTrueFuturesQuotes(boolean stripCodes)
			throws WUException
	{
		List<NameValuePair> pairs = null, result = new ArrayList<>();
		int attempts = MAX_ATTEMPTS;
		do {
			attempts --;
			try {
				checkFormIsReady(true);
				pairs = webForm.selectMarket(14).getQuoteOptions();
				break;
			} catch ( WUException e ) {
				if ( attempts > 0 ) {
					logger.debug("Ignore error and retry (number of attempts remained: {}): ", attempts, e);
				} else {
					throw e;
				}
			} catch ( Exception e ) {
				throw new WUUnexpectedException(e);
			}
		} while ( attempts > 0 );
		
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
		if ( StringUtils.countMatches(tokens[0], "-") != 1 ) {
			// It's something like RTS-12.17-3.18 or Si-12.17-3.18
			// Just skip
			return null;
		}
		return new BasicNameValuePair(tokens[0], tokens[1].substring(0, tokens[1].length() - 1));
	}
	
	/**
	 * Download a market data file from FINAM web-site.
	 * <p>
	 * @param uri - fully-formed URI to download from. The URI uses "as is". No additional checks performed.
	 * @param output - the output stream to store the downloaded data
	 * @throws WUException - an error occurred
	 */
	@Deprecated
	public void download(URI uri, OutputStream output) throws WUException {
		HTTPAttachmentCriteriaBuilder builder = new HTTPAttachmentCriteriaBuilder()
			.withURL(uri.toString())
			.withContentType("finam/expotfile")
			.withTimeOfStartDownloadCurrent();
		try {
			HTTPAttachment attachment = attachmentManager.getLast(builder.build(), new WebDriverGet(webDriver, uri));
			FileUtils.copyFile(attachment.getFile(), output);
			attachment.remove();
		} catch ( IOException e ) {
			throw new WUIOException(e);
		} catch ( HTTPAttachmentNotFoundException e ) {	
			throw new WUIOException(e);
		} catch ( HTTPAttachmentException e ) {
			throw new WUIOException(e);
		} catch ( WebDriverException e ) {
			throw new WUIOException(e);
		}
	}
	
	/**
	 * Download a market data file from FINAM web-site.
	 * <p>
	 * @param baseUri - base URI to resolve address of downloading file. It used
	 * to combine with the query string which was built from the export parameters.
	 * @param params - the data export parameters
	 * @param output - the output stream to store the downloaded data
	 * @throws WUException - an error occurred
	 */
	@Deprecated
	public void download(URI baseUri, FidexpFormParams params, OutputStream output)
			throws WUException
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
	 * @throws WUException - an error occurred
	 */
	@Deprecated
	public void download(URI baseUri, FidexpFormParams params, File target)
			throws WUException
	{
		OutputStream output;
		try {
			output = new BufferedOutputStream(new FileOutputStream(target));
			if ( target.getName().endsWith(".gz") ) {
				output = new GZIPOutputStream(output);
			}
		} catch ( IOException e ) {
			throw new WUIOException("Error creating output stream", e);
		}
		try {
			download(baseUri, params, output);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
	
	public void download(FidexpFormParams params, OutputStream output) throws WUException {
		download(paramsToURIUsingQueryBuilder(params), output);
	}
	
	/**
	 * Download a market data file from FINAM web site.
	 * <p>
	 * Note: The call of this method may use several minutes to execute and will
	 * lock the facade until done.
	 * <p>
	 * @param params - the data export parameters
	 * @param target - the target file. If the filename ends with .gz suffix
	 * then output will be gzipped.
	 * @throws WUException - an error occurred
	 */
	public void download(FidexpFormParams params, File target) throws WUException {
		OutputStream output;
		try {
			output = new BufferedOutputStream(new FileOutputStream(target));
			if ( target.getName().endsWith(".gz") ) {
				output = new GZIPOutputStream(output);
			}
		} catch ( IOException e ) {
			throw new WUIOException("Error creating output stream", e);
		}
		try {
			download(params, output);
		} finally {
			IOUtils.closeQuietly(output);
		}
	}
	
	/**
	 * Download a tick data file from FINAM web site.
	 * <p>
	 * @param marketId - the market ID
	 * @param quoteId - the quote ID
	 * @param date - the date
	 * @param target - the target file. If the filename ends with .gz suffix
	 * then output will be gzipped.
	 * @throws WUException - an error occurred
	 */
	public void downloadTickData(int marketId, int quoteId, LocalDate date,
			File target) throws WUException
	{
		download(new FidexpFormParams()
			.setMarketId(marketId)
			.setQuoteID(quoteId)
			.setDateFrom(date)
			.setDateTo(date)
			.setPeriod(FidexpPeriod.TICKS)
			.setFileName("dummy-file") // TODO: make better filename
			.setFileExt(FidexpFileExt.CSV)
			.setDateFormat(FidexpDateFormat.YYYYMMDD)
			.setTimeFormat(FidexpTimeFormat.HHMMSS)
			.setCandleTime(FidexpCandleTime.START_OF_CANDLE)
			.setUseMoscowTime(true)
			.setFieldSeparator(FidexpFieldSeparator.COMMA)
			.setDigitSeparator(FidexpDigitSeparator.NONE)
			.setDataFormat(FidexpDataFormat.DATE_TIME_LAST_VOL)
			.setAddHeader(true)
			.setFillEmptyPeriods(false), target);
	}
	
	@Deprecated
	private URI combine(URI baseUri, FidexpFormParams params) throws WUException {
		try {
			return new FidexpFormQueryBuilder().buildQuery(baseUri, params);
		} catch ( URISyntaxException e ) {
			throw new WUUnexpectedException("Error building a query", e);
		}
	}
	
	private Map<Integer, String> toMap(List<NameValuePair> pairs) throws WUException {	
		Map<Integer, String> result = new LinkedHashMap<>();
		for ( NameValuePair dummy : pairs ) {
			String text = dummy.getName(), id = dummy.getValue();
			try {
				result.put(Integer.valueOf(id), text);
			} catch ( NumberFormatException e ) {
				throw new WUWebPageException("Invalid option [" + text + "] id: " + id);
			}
		}
		return result;
	}
	
	private void checkFormIsReady(boolean reload) throws WUException {
		if ( reload ) {
			webForm.open().ensurePageLoaded();
		}
		URI uri = null;
		try {
			uri = new URI(webDriver.getCurrentUrl());
		} catch ( URISyntaxException e ) {
			throw new WUUnexpectedException(e);
		}
		if ( (uri.getHost().equals("www.finam.ru") || uri.getHost().equals("finam.ru"))
		  && (uri.getPath().startsWith("/profile/") && uri.getPath().endsWith("/export/")) )
		{
			// we're on the right page
			// to do additional tests?
		} else {
			webForm.open();
		}
	}
	
}
