package ru.prolib.aquila.finam.tools.web;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.BasicConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

import ru.prolib.aquila.finam.tools.web.DataExportForm.SelectorOption;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;

public class DataExportFormTest {
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		//BasicConfigurator.resetConfiguration();
		//BasicConfigurator.configure();
	}

	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	@Ignore
	public void test_downloader() throws Exception {
		CloseableHttpClient httpClient = HttpClients.createDefault();
		try {
			DataExportParams params = new DataExportParams()
				.setDateFrom(LocalDate.of(2016, 7, 15))
				.setDateTo(LocalDate.of(2016, 7, 15));
			URI formAction = new URI("http://195.128.78.52/");
			URI uri = new DataExportFormQueryBuilder().buildQuery(formAction, params);
			HttpClientFileDownloader downloader = new HttpClientFileDownloader(httpClient);
			downloader.download(uri, new File("D:/tmp/finam.csv"));
		} finally {
			httpClient.close();
		}
	}

	@Test
	@Ignore
	public void test() throws Exception {
		WebDriver webDriver = createJBrowserDriver();

		long dummy = System.currentTimeMillis();
		List<SelectorOption> markets, quotes;
		try ( DataExportForm form = new DataExportForm(webDriver) ) {
			markets = form.getMarketOptions();
			form.selectMarket("МосБиржа фьючерсы");
			quotes = form.getQuoteOptions();
			form.selectQuote("RTS")
				.selectDate(LocalDate.of(2002,  12,  31))
				.selectPeriod_Ticks()
				.selectContractName("boozoo") // see the method notes
				.selectFileName("zulu.charlie")
				.selectFileExt_Txt()
				.selectDateFormat_DDMMYY()
				.selectTimeFormat_HHcolMM()
				.selectCandleTime_StartOfCandle()
				.selectMoscowTime(false)
				.selectFieldSeparator_Dot()
				.selectDigitSeparator_Comma()
				.selectFileFormat_TimePriceVolId()
				.selectAddHeader(false)
				.selectFillEmptyPeriods(true);

			System.out.println("ActionURL: " + form.getFormActionURL());
			URL url = form.getFormActionURL();
			System.out.println("Host: " + url.getHost());
			//System.out.println("URL: " + webDriver.getCurrentUrl());
			//Thread.sleep(15000);
		}
		System.out.println("Finished in " + (System.currentTimeMillis() - dummy) + " ms.");
	}
	
	private void dumpOptions(List<SelectorOption> elements) {
		for ( SelectorOption e : elements ) {
			System.out.println("text=" + e.getText()
					+ " id=" + e.getID()
					+ " idx=" + e.getIndex());
		}
	}
	
	private WebDriver createJBrowserDriver() {
		return new JBrowserDriver(Settings.builder()
				.timezone(Timezone.EUROPE_MOSCOW)
				.ssl("compatible")
				.build());
	}
	
	private WebDriver createFirefoxDriver() {
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

}
