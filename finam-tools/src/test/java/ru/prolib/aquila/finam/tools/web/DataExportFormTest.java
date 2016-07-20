package ru.prolib.aquila.finam.tools.web;

import static org.junit.Assert.*;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import org.apache.http.impl.client.HttpClientBuilder;
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
	public void test() throws Exception {
		WebDriver webDriver = createJBrowserDriver();
		//WebDriver webDriver = createPhantomJSDriver();
		try ( DataExportForm form = new DataExportForm(webDriver) ) {
			form.selectMarket("МосБиржа фьючерсы");
			List<SelectorOption> elements = form.getQuoteOptions();
			for ( SelectorOption e : elements ) {
				System.out.println("text=" + e.getText()
						+ " id=" + e.getID()
						+ " idx=" + e.getIndex());
			}
			form.selectQuote("RTS-9.16(RIU6)");
			form.selectDate(LocalDate.of(2016,  7,  15));
			//form.getSubmitButton().click();
		}
	}
	
	private WebDriver createJBrowserDriver() {
		return new JBrowserDriver(Settings.builder().timezone(Timezone.EUROPE_MOSCOW).build());
	}
	
	//private WebDriver createPhantomJSDriver() {
	//	DesiredCapabilities caps = new DesiredCapabilities();
	//	caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "D:/utils/phantomjs/phantomjs.exe");
	//	return new PhantomJSDriver(caps);
	//}
	
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
