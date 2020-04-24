package ru.prolib.aquila.web.utils.finam;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.net.URI;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
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

import ru.prolib.aquila.web.utils.WUWebPageException;

public class FidexpIT {
	private static final Logger logger;
	private static final File JBROWSER_CONF_FILE = new File("it-config/jbd.ini");
	//private static final File sample = new File("fixture/SPFB.RTS_150701_150731-W1.txt");
	
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
		facadeFactory = FidexpFactorySTD.newFactoryRemote(JBROWSER_CONF_FILE, false);
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
		Thread.sleep(ThreadLocalRandom.current().nextLong(5000L, 10000L));
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
		return prefix + "-" + date.getMonthValue() + "." + String.format("%02d", date.getYear() % 100);
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
		expected.put( 17, "МосБиржа фьючерсы Архив");
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
		//expected.put(18564, "ДИКСИ ао"); // excluded from TOP
		expected.put(825,   "Татнфт 3ао");
		expected.put(20346, "МРСКСиб");
		expected.put(18684, "ОГК-2 ао");
		expected.put(20266, "РусГидро");
		expected.put(   13, "Сургнфгз-п");
		expected.put(19623, "Уркалий-ао");
		expected.put(81766, "Якутскэнрг");
		
		Map<Integer, String> actual = facade.getAvailableQuotes(1);
		//System.out.println(actual);
		
		Iterator<Map.Entry<Integer, String>> it = expected.entrySet().iterator();
		while ( it.hasNext() ) {
			Map.Entry<Integer, String> dummy = it.next();
			int id = dummy.getKey();
			assertEquals("Selector option mismatch: id=" + id,
					dummy.getValue(), actual.get(id));
		}
	}
	
	@Test
	public void testGetFuturesNameWithCode_LocalBugfix20191109() {
		assertEquals("GAZR-3.20(GZH0)", getFuturesNameWithCode("GAZR", "GZ", LocalDate.of(2020, 3, 16)));
		assertEquals("GAZR-3.19(GZH9)", getFuturesNameWithCode("GAZR", "GZ", LocalDate.of(2019, 3, 16)));
		assertEquals("GAZR-3.08(GZH8)", getFuturesNameWithCode("GAZR", "GZ", LocalDate.of(2008, 3, 16)));
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
		assertNotNull(facade);
		
		// <a href="#" index="139" value="17451" class="">GAZP</a>
		//facade.downloadTickData(14, 17455, start, target);
		facade.downloadTickData(14, 17451, start, target);
		
		assertTrue(target.length() > 0);
		try ( BufferedReader reader = Fidexp.createReaderCP1251(target) ) {
			assertEquals("<DATE>,<TIME>,<LAST>,<VOL>", reader.readLine());
		}
	}
	
	@Test
	public void testParamsToURIUsingQueryBuilder() throws Exception {
		URI actual = facade.paramsToURIUsingQueryBuilder(params);
		
		assertEquals(paramsEquivURI, actual);
	}
	
	@Test
	@Ignore // TODO: This test fails due to damn FF popup alert
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
	
}
