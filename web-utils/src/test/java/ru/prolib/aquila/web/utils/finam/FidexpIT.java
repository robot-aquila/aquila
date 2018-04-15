package ru.prolib.aquila.web.utils.finam;

import static org.junit.Assert.*;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.BasicConfigurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;

import ru.prolib.aquila.web.utils.WUWebPageException;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentCriteriaBuilder;
import ru.prolib.aquila.web.utils.httpattachment.HTTPAttachmentManager;
import ru.prolib.aquila.web.utils.jbd.JBDAttachmentManager;
import ru.prolib.aquila.web.utils.jbd.JBDWebDriverFactory;

public class FidexpIT {
	private static final Logger logger;
	private static final File	JBROWSER_CONF_FILE = new File("it-config/jbd.ini");
	private static final File sample = new File("fixture/SPFB.RTS_150701_150731-W1.txt");
	
	static {
		logger = LoggerFactory.getLogger(FidexpIT.class);
	}
	
	private static FidexpFactory facadeFactory;
	
	private FidexpFormParams params;
	private URI paramsEquivURI;
	private Fidexp facade;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BasicConfigurator.resetConfiguration();
		BasicConfigurator.configure();
		facadeFactory = FidexpFactorySTD.newDefaultFactory(JBROWSER_CONF_FILE, false);
	}

	@Before
	public void setUp() throws Exception {
		params = new FidexpFormParams()
			.setMarketId(14)
			.setQuoteID(17455) // Склеенный фьюч на индекс РТС
			.setDateFrom(LocalDate.of(2015, 7, 1))	// Не знаю почему, но для 2014 не работает: календарь содержит
			.setDateTo(LocalDate.of(2015, 7, 31))	// только четные дни месяца (2,2,4,4,etc). При этом общее
													// количество элементов-дней 30 вместо 31 для июля
			.setFileName("SPFB.RTS_150701_150731")
			.setPeriod(FidexpPeriod.W1)
			.setFileExt(FidexpFileExt.TXT)
			.setContractName("SPFB.RTS")
			.setDateFormat(FidexpDateFormat.YYYYMMDD)
			.setTimeFormat(FidexpTimeFormat.HHMMSS)
			.setCandleTime(FidexpCandleTime.END_OF_CANDLE)
			.setUseMoscowTime(true)
			.setFieldSeparator(FidexpFieldSeparator.COMMA)
			.setDigitSeparator(FidexpDigitSeparator.NONE)
			.setDataFormat(FidexpDataFormat.TICKER_PER_DATE_TIME_OPEN_HIGH_LOW_CLOSE_VOL)
			.setAddHeader(true)
			.setFillEmptyPeriods(false);
		paramsEquivURI = new URI("http://export.finam.ru/SPFB.RTS_150701_150731.txt?market=14&em=17455&code=SPFB.RTS&apply=0"
				+ "&df=1&mf=6&yf=2015&from=01.07.2015&dt=31&mt=6&yt=2015&to=31.07.2015&p=9&f=SPFB.RTS_150701_150731"
				+ "&e=.txt&cn=SPFB.RTS&dtf=1&tmf=1&MSOR=1&mstime=on&mstimever=1&sep=1&sep2=1&datf=1&at=1");
		facade = facadeFactory.createInstance();
	}
	
	@After
	public void tearDown() throws Exception {
		IOUtils.closeQuietly(facade);
	}
	
	private LocalDate nextFuturesDate(LocalDate date) {
		int year = date.getYear();
		int month = date.getMonthValue();
		int x = (month - 1) / 3 * 3 + 3;
		if ( x == month ) x += 3;
		if ( x > 12 ) { x = 3; year ++; }
		return LocalDate.of(year, x, 16);
	}
	
	private String getFuturesName(String prefix, LocalDate date) {
		return prefix + "-" + date.getMonthValue() + "."
			+ StringUtils.strip(String.valueOf(date.getYear()).substring(2, 4), "0");
	}
	
	private String getFuturesCode(String prefix, LocalDate date) {
		Map<Integer, String> codes = new HashMap<>();
		codes.put(3, "H");
		codes.put(6, "M");
		codes.put(9, "U");
		codes.put(12, "Z");
		String year = String.valueOf(date.getYear());
		return prefix + codes.get(date.getMonthValue())
				+ year.substring(year.length() - 1);
	}
	
	private String getFuturesNameWithCode(String namePfx, String codePfx, LocalDate date) {
		return getFuturesName(namePfx, date) + "(" + getFuturesCode(codePfx, date) + ")";
	}
	
	@Test
	public void testTestFormIntegrity() throws Exception {
		facade.testFormIntegrity();
	}
	
	@Test
	public void testSplitFuturesCode() throws Exception {
		assertEquals(new BasicNameValuePair("RTS-9.16", "RIU6"), facade.splitFuturesCode("RTS-9.16(RIU6)"));
		assertNull(facade.splitFuturesCode("RTS"));
	}
	
	@Test
	public void testGetAvailableMarkets() throws Exception {
		Map<Integer, String> expected = new LinkedHashMap<>();
		expected.put(200, "МосБиржа топ");
		expected.put(  1, "МосБиржа акции");
		expected.put( 14, "МосБиржа фьючерсы");
		expected.put(  2, "МосБиржа облигации");
		expected.put( 17, "ФОРТС Архив");
		expected.put( 38, "RTS Standard Архив");
		
		Map<Integer, String> actual = facade.getAvailableMarkets();
		
		Iterator<Map.Entry<Integer, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<Integer, String> dummy = it.next();
			int id = dummy.getKey();
			assertEquals("Selector option mismatch: id=" + id,
					dummy.getValue(), actual.get(id));
		}
	}
	
	@Test
	public void testGetAvailableQuotes() throws Exception {
		Map<Integer, String> expected = new LinkedHashMap<>();
		expected.put(13855, "Аптеки36и6");
		expected.put(81757, "Башнефт ао");
		expected.put(81954, "Варьеган");
		expected.put(16842, "ГАЗПРОМ ао");
		expected.put(18564, "ДИКСИ ао");
		expected.put(20346, "МРСКСиб");
		expected.put(18684, "ОГК-2 ао");
		expected.put(20266, "РусГидро");
		expected.put(   13, "Сургнфгз-п");
		expected.put(19623, "Уркалий-ао");
		expected.put(81766, "Якутскэнрг");
		
		Map<Integer, String> actual = facade.getAvailableQuotes(1);
		
		Iterator<Map.Entry<Integer, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<Integer, String> dummy = it.next();
			int id = dummy.getKey();
			assertEquals("Selector option mismatch: id=" + id,
					dummy.getValue(), actual.get(id));
		}
	}
	
	@Test
	public void testGetTrueFuturesQuotes() throws Exception {
		Set<String> expected = new HashSet<>();
		LocalDate current = LocalDate.now();
		for ( int i = 0; i < 2; i ++ ) {
			LocalDate next = nextFuturesDate(current);
			expected.add(getFuturesNameWithCode("RTS", "RI", next));
			expected.add(getFuturesNameWithCode("Si", "Si", next));
			expected.add(getFuturesNameWithCode("Eu", "Eu", next));
			expected.add(getFuturesNameWithCode("GAZR", "GZ", next));
			expected.add(getFuturesNameWithCode("SBRF", "SR", next));
			current = current.plusMonths(3);
		}
		
		Map<Integer, String> actual = facade.getTrueFuturesQuotes(false);
		
		for ( String dummy : expected ) {
			assertTrue("Futures not found: " + dummy, actual.containsValue(dummy));
		}
		assertFalse(actual.containsValue("RTS"));
		assertFalse(actual.containsValue("Si"));
		assertFalse(actual.containsValue("Eu"));
		assertFalse(actual.containsValue("GAZR"));
		assertFalse(actual.containsValue("SBRF"));
	}
	
	@Test
	public void testGetTrueFuturesQuotes_StripQuotes() throws Exception {
		Set<String> expected = new HashSet<>();
		LocalDate current = LocalDate.now();
		for ( int i = 0; i < 2; i ++ ) {
			LocalDate next = nextFuturesDate(current);
			expected.add(getFuturesName("RTS", next));
			expected.add(getFuturesName("Si", next));
			expected.add(getFuturesName("Eu", next));
			expected.add(getFuturesName("GAZR", next));
			expected.add(getFuturesName("SBRF", next));
			current = current.plusMonths(3);
		}
		
		Map<Integer, String> actual = facade.getTrueFuturesQuotes(true);
		
		for ( String dummy : expected ) {
			assertTrue("Futures not found: " + dummy, actual.containsValue(dummy));
		}
		assertFalse(actual.containsValue("RTS"));
		assertFalse(actual.containsValue("Si"));
		assertFalse(actual.containsValue("Eu"));
		assertFalse(actual.containsValue("GAZR"));
		assertFalse(actual.containsValue("SBRF"));
	}
	
	@Test
	public void testGetTrueFuturesQuotes_SkipCombined() throws Exception {
		Map<Integer, String> actual = facade.getTrueFuturesQuotes(true);
		
		// Check that combined tickers like RTS-12.17-3.18, Si-12.17-3.18 were skipped
		for ( String symbol : actual.values() ) {
			assertEquals("Unexpected symbol: " + symbol, 1, StringUtils.countMatches(symbol, "-"));
		}
	}

	@Test
	@Ignore
	public void testDownload2_UriOs() throws Exception {
		fail("TODO: incomplete");
	}
	
	@Test
	@Ignore
	public void testDownload3_UriParamsOs() throws Exception {
		fail("TODO: incomplete");
	}
	
	@Test
	@Ignore
	public void testDownload3_UriParamsFile() throws Exception {
		fail("TODO: incomplete");
	}
	
	@Test
	@Ignore
	public void testDownload3_UriParamsFile_Gzipped() throws Exception {
		fail("TODO: incomplete");
	}
	
	@Test
	public void testDownload2_ParamsFile() throws Exception {
		File target = File.createTempFile("download-test-", ".csv");
		target.deleteOnExit();
		
		//System.out.println(new FidexpFormQueryBuilder().buildQuery(new URI("http://hahaha/"), params));
		facade.download(params, target);
		
		assertEquals(FileUtils.readLines(sample), FileUtils.readLines(target));
	}
	
	@Test
	public void testDownload2_ParamsFile_Gzipped() throws Exception {		
		File target = File.createTempFile("download-test-gz-", ".csv.gz");
		target.deleteOnExit();
		
		facade.download(params, target);

		File sampleGz = File.createTempFile("download-test-gz-sample-", ".csv.gz");
		sampleGz.deleteOnExit();
		OutputStream output = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(sampleGz)));
		FileUtils.copyFile(sample, output);
		IOUtils.closeQuietly(output);
		
		assertEquals(sampleGz.length(), target.length());
		byte[] sampleBytes = FileUtils.readFileToByteArray(sampleGz);
		byte[] targetBytes = FileUtils.readFileToByteArray(target);
		for ( int i = 0; i < sampleBytes.length; i ++ ) {
			assertEquals("byte #" + i, sampleBytes[i], targetBytes[i]);
		}
	}
	
	@Test
	public void testDownloadTickData4() throws Exception {
		File target = File.createTempFile("download-ticks-", ".csv.gz");
		target.deleteOnExit();
		
		LocalDate start = LocalDate.now().minusDays(1);
		switch ( start.getDayOfWeek() ) {
		case SATURDAY:
			start = start.minusDays(1);
		case SUNDAY:
			start = start.minusDays(2);
			break;
		default:
			break;
		}
		
		facade.downloadTickData(14, 17455, start, target);
		
		assertTrue(target.length() > 0);
	}
	
	@Test
	public void testParamsToURIUsingQueryBuilder() throws Exception {
		URI actual = facade.paramsToURIUsingQueryBuilder(params);
		
		assertEquals(paramsEquivURI, actual);
	}
	
	@Test
	public void testParamsToURIUsingFormAction() throws Exception {
		URI actual = facade.paramsToURIUsingFormAction(params);
		
		assertEquals(paramsEquivURI, actual);
	}
	
	@Test
	public void testThatTheFormHasKnownAddressPattern() throws Exception {
		int testMaxMarkets = 8;
		int testMinSuccess = 5;
		int testMaxRetriesPerMarket = 3;

		WebDriver driver = facade.getWebDriver();
		FidexpForm form = facade.getWebForm().open();
		List<NameValuePair> nvpMarketList = form.getMarketOptions();
		Collections.shuffle(nvpMarketList);
		int countMarkets = Math.min(testMaxMarkets, nvpMarketList.size());
		assertNotEquals(0, countMarkets);
		int success = 0;
		String prevURL = null;
		for ( int i = 0; i < countMarkets; i ++ ) {
			String marketName = nvpMarketList.get(i).getName();
			for ( int j = 0; j < testMaxRetriesPerMarket; j ++ ) {
				try {
					form.selectMarket(marketName);
					List<NameValuePair> nvpQuoteList = form.getQuoteOptions();
					if ( nvpQuoteList.size() <= 1 ) {
						logger.warn("Skip market cuz it does not have options to select a quote: {}", marketName);
						break;
					}
					String quoteName = nvpQuoteList.get(1).getName(); // get first quote in list
					form.selectQuote(quoteName).ensurePageLoaded();
					//logger.debug("Is it correct quote#1 [{}] for market [{}]?", quoteName, marketName);
					String currURL = driver.getCurrentUrl();
					if ( currURL == null || (prevURL != null && currURL.equals(prevURL)) ) {
						logger.warn("Something is wrong with URL after switching to quote {} of market {}", quoteName, marketName);
						logger.warn("PrevURL: {}", prevURL);
						logger.warn("CurrURL: {}", currURL);
						logger.warn("Refresh form and try again");
						form.open();
						continue;
					}

					prevURL = currURL; // refresh URL after passing the test
					URI uri = new URI(currURL);
					if ( (uri.getHost().equals("www.finam.ru") || uri.getHost().equals("finam.ru"))
					  && (uri.getPath().startsWith("/profile/") && uri.getPath().endsWith("/export/")) )
					{
						success ++;
						logger.debug("Market {} successfully passed the test", marketName);
					} else {
						logger.warn("Market {} URL {} does not match supported pattern: {}", marketName, currURL);
					}
					break;
				} catch ( WUWebPageException e ) {
					logger.error("Error accessing page: ", e);
					form.open();
				}
				
			}
		}
		logger.debug("{} of {} URL pattern tests passed. Min. for success is {}", new Object[] { success, testMaxMarkets, testMinSuccess });
		assertTrue(success >= testMinSuccess);
	}
	
	// TODO: to remove
	@Ignore
	@Test
	public void testDownload_WebDriver() throws Exception {
		JBDWebDriverFactory factory = new JBDWebDriverFactory();
		factory.withMoexTestedSettings();
		factory.loadIni(JBROWSER_CONF_FILE);
		//factory.getSettingsBuilder()
		//	.saveAttachments(true)
		//	.logWire(true);
		JBrowserDriver webDriver = (JBrowserDriver) factory.createWebDriver();
		HTTPAttachmentManager attachmentManager = new JBDAttachmentManager(webDriver);
		File attachmentsDir = webDriver.attachmentsDir();
		try {
			HTTPAttachmentCriteriaBuilder criteriaBuilder = new HTTPAttachmentCriteriaBuilder()
				.withTimeOfStartDownloadCurrent();
			webDriver.get("http://export.finam.ru/SPFB.RTS_140701_140731.txt?market=14&em=17455&code=SPFB.RTS&apply=0&df=1&mf=6&yf=2014&from=01.07.2014&dt=31&mt=6&yt=2014&to=31.07.2014&p=9&f=SPFB.RTS_140701_140731&e=.txt&cn=SPFB.RTS&dtf=1&tmf=1&MSOR=1&mstime=on&mstimever=1&sep=1&sep2=1&datf=1&at=1");
			System.out.println("attachmentsDir: " + attachmentsDir);
			attachmentManager.getLast(criteriaBuilder.build(), null);
			Thread.sleep(60000L);
			//System.out.println("---page source---");
			//System.out.println(webDriver.getPageSource());
			//String x = FileUtils.readFileToString(new File("/home/whirlwind/work/tmp/udd-test/SPFB.RTS_140701_140731.txt"));
			//System.out.println("---file source---");
			//System.out.println(x);
		} finally {
			webDriver.close();
		}
	}
	
	@Ignore
	@Test
	public void testDownload_AHC() throws Exception {
		String url = "http://export.finam.ru/SPFB.RTS_180413_180413.txt?market=14&em=17455&code=SPFB.RTS&apply=0&df=13&mf=3&yf=2018&from=13.04.2018&dt=13&mt=3&yt=2018&to=13.04.2018&p=1&f=SPFB.RTS_180413_180413&e=.txt&cn=SPFB.RTS&dtf=1&tmf=1&MSOR=1&mstime=on&mstimever=1&sep=1&sep2=1&datf=6&at=1";
		
		List<Header> defaultHeaders = new ArrayList<>();
		defaultHeaders.add(new BasicHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0 (X11; Linux x86_64; rv:52.0) Gecko/20100101 Firefox/52.0"));
		defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT, "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"));
		defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US,en;q=0.5"));
		defaultHeaders.add(new BasicHeader(HttpHeaders.ACCEPT_ENCODING, "gzip, deflate"));
		
		try ( CloseableHttpClient httpClient = HttpClients.custom()
			.setDefaultHeaders(defaultHeaders)
			.setProxy(new HttpHost("localhost", 3128))
			.build() )
		{
			try ( CloseableHttpResponse response = httpClient.execute(new HttpGet(url))) {
				InputStream input = new BufferedInputStream(response.getEntity().getContent());
				FileUtils.copyInputStreamToFile(input, new File("/home/whirlwind/work/tmp/download-ahc-test.txt"));
				
			} catch ( ClientProtocolException e ) {
				throw new Exception("Error: ", e);
			} catch ( IOException e ) {
				throw new Exception("Error: ", e);
			}
		}
	}

}
