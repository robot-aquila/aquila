package ru.prolib.aquila.finam.tools.web;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.io.IOUtils;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;

@Ignore // Integration test
public class DataExportTest {
	private static final File sample = new File("fixture/RTS-140701-140731-W1.txt");
	
	private DataExportParams params;
	private DataExport facade;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@Before
	public void setUp() throws Exception {
		params = new DataExportParams()
			.setMarketId(14)
			.setQuoteID(17455)
			.setDateFrom(LocalDate.of(2014, 7, 1))
			.setDateTo(LocalDate.of(2014, 7, 31))
			.setPeriod(Period.W1)
			.setFileName("SPFB.RTS_140701_140731")
			.setFileExt(FileExt.TXT)
			.setContractName("SPFB.RTS")
			.setDateFormat(DateFormat.YYYYMMDD)
			.setTimeFormat(TimeFormat.HHMMSS)
			.setCandleTime(CandleTime.END_OF_CANDLE)
			.setUseMoscowTime(true)
			.setFieldSeparator(FieldSeparator.COMMA)
			.setDigitSeparator(DigitSeparator.NONE)
			.setDataFormat(DataFormat.TICKER_PER_DATE_TIME_OPEN_HIGH_LOW_CLOSE_VOL)
			.setAddHeader(true)
			.setFillEmptyPeriods(false);
		facade = new DataExport();
	}
	
	@After
	public void tearDown() throws Exception {
		IOUtils.closeQuietly(facade);
	}
	
	protected WebDriver createJBrowserDriver() {
		return new JBrowserDriver(Settings.builder()
				.timezone(Timezone.EUROPE_MOSCOW)
				.ssl("compatible")
				.build());
	}
	
	protected WebDriver createFirefoxDriver() {
		FirefoxProfile profile = new FirefoxProfile();
    	// http://browsers.about.com/od/aboutconfigentries/a/browser_download_folderList.htm
    	profile.setPreference("browser.download.folderList", 2);
    	profile.setPreference("browser.download.manager.showWhenStarting", false);
    	profile.setPreference("browser.download.dir", "D:/tmp");
    	// yes, this error in the finam response
    	profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "finam/expotfile"); 
    	File ffBinary = new File("D:/Program Files (x86)/Mozilla Firefox/firefox.exe");
    	return new FirefoxDriver(new FirefoxBinary(ffBinary), profile);
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
		CloseableHttpClient httpClient = HttpClients.createDefault();
		WebDriver webDriver = createJBrowserDriver();
		try {
			try ( DataExport export = new DataExport(httpClient, webDriver) ) {
				export.testFormIntegrity();
			}
		} finally {
			IOUtils.closeQuietly(httpClient);
			webDriver.close();
		}
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
			expected.add(getFuturesNameWithCode("VTBR", "VB", next));
			expected.add(getFuturesNameWithCode("SBRF", "SR", next));
			current = current.plusMonths(3);
		}
		
		Map<Integer, String> actual = facade.getTrueFuturesQuotes(false);
		
		for ( String dummy : expected ) {
			assertTrue("Futures not found: " + dummy, actual.containsValue(dummy));
		}
		assertFalse(actual.containsValue("RTS"));
		assertFalse(actual.containsValue("Si"));
		assertFalse(actual.containsValue("VTBR"));
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
			expected.add(getFuturesName("VTBR", next));
			expected.add(getFuturesName("SBRF", next));
			current = current.plusMonths(3);
		}
		
		Map<Integer, String> actual = facade.getTrueFuturesQuotes(true);
		
		for ( String dummy : expected ) {
			assertTrue("Futures not found: " + dummy, actual.containsValue(dummy));
		}
		assertFalse(actual.containsValue("RTS"));
		assertFalse(actual.containsValue("Si"));
		assertFalse(actual.containsValue("VTBR"));
		assertFalse(actual.containsValue("SBRF"));
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
		
		facade.downloadTickData(14, 17455, LocalDate.now().minusDays(1), target);
		
		assertTrue(target.length() > 0);
	}
		
}
