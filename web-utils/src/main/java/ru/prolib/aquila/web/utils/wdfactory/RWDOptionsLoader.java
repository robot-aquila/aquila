package ru.prolib.aquila.web.utils.wdfactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

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
		Ini ini = new Ini(config_file);
		RWDOptions default_options = defaultOptions();
		if ( ! ini.containsKey(INI_SECTION_NAME) ) {
			return default_options;
		}
		Section config = ini.get(INI_SECTION_NAME);
		URL hub_url = toURL(config.getOrDefault(INI_KEY_HUB_URL, default_options.getHubUrl().toString()));
		Capabilities capabilities = default_options.getCapabilities();
		String driver = config.getOrDefault(INI_KEY_DRIVER, DEFAULT_DRIVER);
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
		return new RWDOptions(hub_url, capabilities);
	}

}
