package ru.prolib.aquila.finam.tools.web;

import static org.junit.Assert.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.LocalDate;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
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

@Ignore
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
	@Ignore
	public void testGetAvailableMarkets() throws Exception {
		fail("TODO: incomplete");
	}
	
	@Test
	@Ignore
	public void testGetAvailableQuotes() throws Exception {
		fail("TODO: incomplete");
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
		
}
