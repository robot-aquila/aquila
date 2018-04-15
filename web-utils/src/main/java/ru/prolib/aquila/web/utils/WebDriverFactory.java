package ru.prolib.aquila.web.utils;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import ru.prolib.aquila.web.utils.jbd.JBDWebDriverFactory;

public interface WebDriverFactory {
	
	@Deprecated
	public static WebDriver createJBrowserDriver(int socketTimeoutMs) {
		return new JBDWebDriverFactory().withMoexTestedSettings(socketTimeoutMs).createWebDriver();
	}
	
	@Deprecated
	public static WebDriver createJBrowserDriver() {
		return new JBDWebDriverFactory().withMoexTestedSettings().createWebDriver();
	}
	
	@Deprecated
	public static WebDriver createFirefoxDriver() {
		// Note: path to geckodriver executable must be in PATH
		// Note: path to firefox executable must be in PATH
		return new FirefoxDriver();
		
		// Deprecated approach
    	//File ffBinary = new File("D:/Program Files (x86)/Mozilla Firefox/firefox.exe");
    	//return new FirefoxDriver(new FirefoxBinary(ffBinary), profile);
	}

	@Deprecated
	public static WebDriver createHtmlUnitDriver() {
		HtmlUnitDriver driver = new HtmlUnitDriver();
		driver.setJavascriptEnabled(true);
		return driver;
	}
	
	WebDriver createWebDriver();

}
