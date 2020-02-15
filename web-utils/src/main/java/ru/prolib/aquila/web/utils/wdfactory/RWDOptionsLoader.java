package ru.prolib.aquila.web.utils.wdfactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class RWDOptionsLoader {
	private static final String DRIVER_FIREFOX = "firefox";
	private static final String DRIVER_CHROME = "chrome";
	private static final String INI_SECTION_NAME = "remote-driver";
	private static final String INI_KEY_DRIVER = "driver";
	private static final String INI_KEY_HUB_URL = "hub.url";
	private static final String DEFAULT_HUB_URL = "http://localhost:4444/wd/hub";
	private static final String DEFAULT_DRIVER = DRIVER_CHROME;
	private static final String ENV_KEY_DRIVER = "AQUILA_IT_DRIVER";
	private static final String ENV_KEY_HUB_URL = "AQUILA_IT_HUB_URL";
	
	public static class RWDOptions {
		private final URL hubUrl;
		private final Capabilities capabilities;
		
		public RWDOptions(URL hubUrl, Capabilities capabilities) {
			this.hubUrl = hubUrl;
			this.capabilities = capabilities;
		}
		
		public URL getHubUrl() {
			return hubUrl;
		}
		
		public Capabilities getCapabilities() {
			return capabilities;
		}
		
	}
	
	static URL toURL(String url) {
		try {
			return new URL(url);
		} catch ( MalformedURLException e ) {
			throw new IllegalArgumentException(e);
		}
	}
	
	private final FFOptionsLoader firefoxOptionsLoader;
	
	public RWDOptionsLoader(FFOptionsLoader firefox_options_loader) {
		this.firefoxOptionsLoader = firefox_options_loader;
	}
	
	public RWDOptionsLoader() {
		this(new FFOptionsLoader());
	}
	
	public RWDOptions defaultOptions() {
		return new RWDOptions(toURL(DEFAULT_HUB_URL), new ChromeOptions());
	}
	
	public RWDOptions loadOptions(File config_file) throws IOException {
		String driver = DEFAULT_DRIVER, hub_url = DEFAULT_HUB_URL;
		
		// Lowest priority is the configuration file
		if ( config_file.exists() ) {
			Ini ini = new Ini(config_file);
			if ( ini.containsKey(INI_SECTION_NAME) ) {
				Section config = ini.get(INI_SECTION_NAME);
				hub_url = config.getOrDefault(INI_KEY_HUB_URL, hub_url);
				driver = config.getOrDefault(INI_KEY_DRIVER, driver);
			}
		}
		
		// The next priority is environment variables
		Map<String, String> env = System.getenv();
		hub_url = env.getOrDefault(ENV_KEY_HUB_URL, hub_url);
		driver = env.getOrDefault(ENV_KEY_DRIVER, driver);
		
		Capabilities capabilities = null;
		switch ( driver ) {
		case DRIVER_FIREFOX:
			FirefoxOptions fo = firefoxOptionsLoader.loadOptions(config_file);
			capabilities = fo;
			fo.setCapability(FirefoxDriver.MARIONETTE, true);
			break;
		case DRIVER_CHROME:
			capabilities = new ChromeOptions();
			break;
		default:
			throw new IllegalArgumentException("Unsupported driver: " + driver);
		}
		return new RWDOptions(toURL(hub_url), capabilities);
	}

}
