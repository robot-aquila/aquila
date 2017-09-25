package ru.prolib.aquila.web.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import com.machinepublishers.jbrowserdriver.JBrowserDriver;
import com.machinepublishers.jbrowserdriver.Settings;
import com.machinepublishers.jbrowserdriver.Timezone;

public class WebDriverFactory {
	private static final int DEFAULT_SOCKET_TIMEOUT = 30000;
	
	public static WebDriver createJBrowserDriver(int socketTimeoutMs) {
		return new JBrowserDriver(Settings.builder()
				.timezone(Timezone.EUROPE_MOSCOW)
				.ssl("compatible")
				.blockAds(true)
				.headless(true)
				.quickRender(true)
				.socketTimeout(socketTimeoutMs)
				.connectionReqTimeout(socketTimeoutMs)
				.connectTimeout(socketTimeoutMs)
				.maxConnections(128)
				.javascript(true)
				.logJavascript(true)
				.build());
	}
	
	public static WebDriver createJBrowserDriver() {
		return createJBrowserDriver(DEFAULT_SOCKET_TIMEOUT);
	}
	
	public static WebDriver createFirefoxDriver() {
		// Note: path to geckodriver executable must be in PATH
		// Note: path to firefox executable must be in PATH
		return new FirefoxDriver();
		
		// Deprecated approach
    	//File ffBinary = new File("D:/Program Files (x86)/Mozilla Firefox/firefox.exe");
    	//return new FirefoxDriver(new FirefoxBinary(ffBinary), profile);
	}
	
	public static WebDriver createHtmlUnitDriver() {
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.setJavascriptEnabled(true);
		return driver;
	}

}
